package uk.co.impactnottingham.benh.wordpress;

import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

    RequestParameters() {
        mRequestParameters = new HashMap<>();
    }

    public void addParameter(String key, String value) {
        mRequestParameters.put(key, value);
    }

    URL addRequestParams(URL url) throws MalformedURLException {
        StringBuilder urlString = new StringBuilder(url.toString());
        urlString.append("?");
        for (Map.Entry<String, String> e : mRequestParameters.entrySet()) {
            urlString.append(e.getKey());
            urlString.append("=");
            urlString.append(e.getValue());
            urlString.append("&");
        }

        return new URL(urlString.toString());
    }
}