package se.dw.okhttpwrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

/**
 * 		A Wrapper for the excellent OkHttp Library
 * 
 * 		OkHttpWrapper.initiate("https://xxx.yyy.net/rest.php", "username", "password");
 *		Request request = new Request();
 *		request.addPostParam("function", "get_owner_ads");
 *		request.addPostParam("ownerid", "6");
 *		request.addPostParam("myotfid", "8");
 *		OkHttpWrapper.execute(request, new ResponseListener() {
 *			public void onFinish(Response response) {
 *				Log.d("Response",""+response);
 *			}
 *			public void onError(Exception e, Response response) {
 *				Log.d("Response",""+response);
 *			}
 *		});
 *
 */
public class OkHttpWrapper {

	private static OkHttpClient client = null;
	private static String USER = null, PASSWORD=null;
	private static String REST_URL = null;
	private static long timeout = 10L;
	
	/**
	 * Initiate the wrapper
	 * 
	 * @param url Rest default path, you can also send url with every request
	 * @param username BasicAuth username, null if no basic auth
	 * @param password BasicAuth password, null if no basic auth
	 */
	public static void initiate(String url, String username, String password){
		REST_URL = url;
		USER = username;
		PASSWORD = password;
		client = null;
	}
	
	/**
	 * Initiate the wrapper
	 * 
	 * @param url Rest default path, you can also send url with every request
	 */
	public static void initiate(String url){
		REST_URL = url;
		client = null;
	}
	
	private static OkHttpClient getClient(){
		if(REST_URL == null){
			Log.e("OkHttpWrapper","OkHttpWrapper not initialized");
			return null;
		}
		
		if(client == null){
			client = new OkHttpClient();
			
			if(USER != null && PASSWORD != null){
				//Authenticator
				Authenticator.setDefault(new Authenticator(){
				    protected PasswordAuthentication getPasswordAuthentication() {
				        return new PasswordAuthentication(USER, PASSWORD.toCharArray());
				    }
			    });
			}
			
			// Ignore invalid SSL endpoints.
			client.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String s, SSLSession session) {
					return true;
				}
			});
			
			client.setConnectTimeout(timeout, TimeUnit.SECONDS);
			
			return client;
		}
		return client;
	}
	
	/**
	 * 
	 * @param request Request object
	 * @return Response object
	 */
	public static Response execute(Request request){
		Response response = new Response();
		try {
			return request(request);
		} catch (IOException e) {
			response.setException(e);
		}
		return response;
	}
	
	public static void execute(Request request, ResponseListener listener){
		AsyncRequest req = new AsyncRequest(listener);
		req.execute(request);
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
			url = REST_URL;
		}
		return request(url, request.getGetParameters(), request.getPostParameters());
	}
	private static Response request(String url, String getparams, String postparams) throws IOException{
		OkHttpClient client = getClient();
		
		Response response = new Response();
		response.setUrl(url);
		
		if(client == null){
			response.setException(new Exception("OkHttpWrapper not initialized"));
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
	        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
	          throw new IOException("Unexpected HTTP response: "
	              + connection.getResponseCode() + " " + connection.getResponseMessage());
	        }
	        
	        in = connection.getInputStream();
	        
	        byte[] byteresponse = readFully(in);
	        String stringresponse = new String(byteresponse, "UTF-8");
	        
//			InputStreamReader isr = new InputStreamReader(in);
//			 
//			BufferedReader r = new BufferedReader(isr);
//			StringBuilder total = new StringBuilder();
//			String line;
//			while ((line = r.readLine()) != null) {
//			  total.append(line);
//			}
//	        String stringresponse = total.toString();
	        
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
