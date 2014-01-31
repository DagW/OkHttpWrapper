package se.dw.okhttpwrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Request {
	String url = null;
	List<NameValuePair> postparams = new ArrayList<NameValuePair>();
	List<NameValuePair> getparams = new ArrayList<NameValuePair>();
	
	public String getUrl(){
		return url;
	}
	
	public String getPostParameters(){
		return createParameters(postparams);
	}
	
	public String getGetParameters(){
		return createParameters(getparams);
	}

	public void addPostParam(String key, String value){
		postparams.add(new BasicNameValuePair(key, value));
	}
	public void addGetParam(String key, String value){
		getparams.add(new BasicNameValuePair(key, value));
	}
	
	private String createParameters(List<NameValuePair> list){
		String params = "";
		if(list.size()>0){
			for(NameValuePair pair : list){
				params += pair.getName()+"="+pair.getValue();
				params += "&";
			}
			if(params.endsWith("&")){
				params.substring(0, params.length()-1);
			}
		}
		return params;
	}
}
