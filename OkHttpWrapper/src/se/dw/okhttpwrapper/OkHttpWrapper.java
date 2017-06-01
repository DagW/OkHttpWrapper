package se.dw.okhttpwrapper;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * A Wrapper for the OkHttp Library
 * <p>
 * Init
 * OkHttpWrapper.initiate("https://xxx.yyy.net/rest.php", "username", "password");
 * <p>
 * Normal request
 * Request request = new Request();
 * request.setUrl("asdasd");
 * request.addPostParam("function", "get_owner_ads");
 * request.addPostParam("ownerid", "6");
 * request.addPostParam("myotfid", "8");
 * <p>
 * request.execute(new ResponseListener() {
 * public void onFinish(Response response) {
 * Log.d("Response",""+response);
 * }
 * public void onError(Exception e, Response response) {
 * Log.d("Response",""+response);
 * }
 * });
 * <p>
 * Alternatively chain the request
 * new Request().setUrl("http://asdasd").addPostParam("ownerid", "6").execute();
 */
public class OkHttpWrapper {
    private static final String LOG_TAG = "OkHttpWrapper";
    private static OkHttpClient client = null;
    private static String USER = null, PASSWORD = null;
    private static String REST_URL = null;
    private static long timeout = 10L;

    /**
     * Initiate the wrapper
     *
     * @param url      Rest default path, you can also send url with every request
     * @param username BasicAuth username, null if no basic auth
     * @param password BasicAuth password, null if no basic auth
     */
    public static void initialize(String url, String username, String password) {
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
    public static void initiate(String url) {
        REST_URL = url;
        client = null;
    }

    protected static OkHttpClient getClient() {
        if (REST_URL == null) {
            Log.w(LOG_TAG, "OkHttpWrapper not initialized");
        }

        if (client == null) {
            client = new OkHttpClient();

            if (USER != null && PASSWORD != null) {
                //Authenticator
                Authenticator.setDefault(new Authenticator() {
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

    public static String getRestUrl() {
        return REST_URL;
    }
}
