package uk.co.impactnottingham.benh.wordpress;

import javax.net.ssl.HttpsURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This is not a very safe way of doing parameters (may fail silently)
 * but it allows the code to be much simpler.
 *
 * @author BenH14
 */
class RequestParameters {
    private Map<String, String> mRequestParameters;

    public RequestParameters() {
        mRequestParameters = new HashMap<>();
    }

    public void addParameter(String key, String value) {
        mRequestParameters.put(key, value);
    }

    HttpsURLConnection addRequestHeaders(HttpsURLConnection connection) {
        for (Map.Entry<String, String> e : mRequestParameters.entrySet()) {
            connection.setRequestProperty(e.getKey(), e.getValue());
        }
        return connection;
    }
}