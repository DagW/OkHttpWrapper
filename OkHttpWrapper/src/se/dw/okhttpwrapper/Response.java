package se.dw.okhttpwrapper;


public class Response {
	private String url = null;
	private long start = System.currentTimeMillis(), stop = 0;
	private String response = null;
	private Exception error = null;
	
	public String getResponse() {
		return response;
	}
	public String getResponseTrimmed() {
		return response.trim();
	}
	public void setResponse(String response) {
		this.response = response;
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
		String toString = "URL='"+url+"'\nResponseTime='"+getResponseTime()+"'ms\nResponse='"+response+"'";
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
}
