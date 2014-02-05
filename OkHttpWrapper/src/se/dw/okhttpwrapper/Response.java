package se.dw.okhttpwrapper;

import org.json.JSONObject;


public class Response {
	private String url = null;
	private long start = System.currentTimeMillis(), stop = 0;
	private String response = null;
	private Exception error = null;
	private String getparams = null;
	private String postparams = null;
	private int responseCode = -1;
	
	public int getResponseCode() {
		return responseCode;
	}
	public String getResponse() {
		return response;
	}
	public String getResponseTrimmed() {
		return response.trim();
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public void setPostParams(String postparams) {
		this.postparams = postparams;
	}
	public void setGetParams(String getparams) {
		this.getparams = getparams;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode  = responseCode;
	}
	public void stop() {
		stop = System.currentTimeMillis();
	}
	public long getResponseTime(){
		long diff = stop-start;
		if(diff<0){
			return -1;
		}
		return diff;
	}
	public String toString() {
		String toString = "URL='"+url+"'";
		toString += "\nResponseCode='"+responseCode+"'";
		toString += "\nGetParams='"+getparams+"'";
		toString += "\nPostParams='"+postparams+"'";
		toString += "\nResponseTime='"+getResponseTime()+"'ms";
		toString += "\nResponse='"+response+"'";
		if(error != null){
			toString += "\nException='"+error.toString()+"'";
		}
		return toString;
	}
	public void setException(Exception error) {
		this.error = error;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	//TODO
	public boolean isJSON(){
		return false;
	}
	public JSONObject getJSON(){
		return null;
	}
	
}
