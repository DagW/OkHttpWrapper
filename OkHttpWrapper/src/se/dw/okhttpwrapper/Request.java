package se.dw.okhttpwrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;

public class Request {
	
	private String url = null;
	private List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	private List<NameValuePair> getparams = new ArrayList<NameValuePair>();
	
	public String getUrl(){
		return url;
	}
	
	public Request setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public Request addPostParam(String key, String value){
		postparams.add(new BasicNameValuePair(key, value));
		return this;
	}
	public Request addGetParam(String key, String value){
		getparams.add(new BasicNameValuePair(key, value));
		return this;
	}
	
	private String createParameters(List<NameValuePair> list){
		String params = "";
		if(list.size()>0){
			for(NameValuePair pair : list){
				
				String name = pair.getName();
				String value = pair.getValue();
				
				try {
					name = URLEncoder.encode(name, "utf-8");
					value = URLEncoder.encode(value, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				params += name+"="+value;
				
				if(list.indexOf(pair) != list.size()-1){
					params += "&";
				}
				
			}
		}
		return params;
	}
	
	private String getPostParameters(){
		return createParameters(postparams);
	}
	
	private String getGetParameters(){
		return createParameters(getparams);
	}

	public Response execute(){
		Response response = new Response();
		try {
			return request(this);
		} catch (IOException e) {
			response.setException(e);
		}
		return response;
	}
	
	public void execute(ResponseListener listener){
		AsyncRequest req = new AsyncRequest(listener);
		req.execute(this);
	}
	
	private static class AsyncRequest extends AsyncTask<Request, Integer, Response>{
		ResponseListener listener = null;
		Exception error = null;
		public AsyncRequest(ResponseListener listener) {
			this.listener = listener;
		}
		protected void onPostExecute(Response result) {
			if(error != null){
				listener.onError(error, result);
			}else{
				listener.onFinish(result);
			}
		}
		protected Response doInBackground(Request... params) {
			Response response = new Response();
			try {
				response = request(params[0]);
			} catch (IOException e) {
				error = e;
				response.setException(error);
			}
			return response;
		}
	}
	
	private static Response request(Request request) throws IOException{
		String url = request.getUrl();
		if(url == null){
			url = OkHttpWrapper.getRestUrl();
		}
		return request(url, request.getGetParameters(), request.getPostParameters());
	}
	private static Response request(String url, String getparams, String postparams) throws IOException{
		OkHttpClient client = OkHttpWrapper.getClient();
		
		Response response = new Response();
		response.setUrl(url);
		
		response.setPostParams(postparams);
		response.setGetParams(getparams);
		
		if(client == null){
			response.setException(new Exception("OkHttpWrapper not initialized, or request url is null"));
			return response;
		}
		
		// Create request for remote resource.
		HttpURLConnection connection;
		connection = client.open(new URL(url+"?"+getparams));

		OutputStreamWriter out = null;
	    InputStream in = null;
		
	    try {
	        // Write the request.
	        connection.setRequestMethod("POST");
	        out = new OutputStreamWriter(connection.getOutputStream());
	        out.write(postparams);
	        out.close();

	        // Read the response.
	        response.setResponseCode(connection.getResponseCode());
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	          throw new IOException("Unexpected HTTP response: "
	              + connection.getResponseCode() + " " + connection.getResponseMessage());
	        }
	        
	        in = connection.getInputStream();
	        
	        byte[] byteresponse = readFully(in);
	        String stringresponse = new String(byteresponse, "UTF-8");
	        
	        response.setResponse( stringresponse );
	        response.stop();
	        
	    } finally {
		   // Clean up.
		   if (out != null) out.close();
		   if (in != null) in.close();
	    }
	    
	    return response;
	}
	
	private static byte[] readFully(InputStream in) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    for (int count; (count = in.read(buffer)) != -1; ) {
	      out.write(buffer, 0, count);
	    }
	    return out.toByteArray();
	  }
}
