# OkHttpWrapper

OkHttpWrapper wraps OkHttp by Square into a easy to use package.

## Init
´´´
OkHttpWrapper.initiate("https://xxx.yyy.net/rest.php", "username", "password");
´´´
## Normal request
´´´
Request request = new Request();
request.setUrl("asdasd");
request.addPostParam("function", "get_owner_ads");
request.addPostParam("ownerid", "6");
request.addPostParam("myotfid", "8");
 
request.execute(new ResponseListener() {
	public void onFinish(Response response) {
		Log.d("Response",""+response);
	}
	public void onError(Exception e, Response response) {
		Log.d("Response",""+response);
	}
});
´´´

## Alternatively chain the request
´´´
new Request().setUrl("https://xxx.yyy.net/rest.php").addPostParam("ownerid", "6").execute();
´´´