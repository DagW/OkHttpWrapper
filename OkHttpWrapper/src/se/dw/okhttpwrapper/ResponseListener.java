package se.dw.okhttpwrapper;

public interface ResponseListener {
	void onFinish(Response result);
	void onError(Exception error, Response exception);
}
