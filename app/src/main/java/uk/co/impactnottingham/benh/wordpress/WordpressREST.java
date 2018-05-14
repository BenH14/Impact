package uk.co.impactnottingham.benh.wordpress;

import android.util.JsonReader;
import android.util.Log;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by benh14 on 1/26/18.
 */
public class WordpressREST {

    private static final String TAG = WordpressREST.class.getName();

    /**
     * This is not a very safe way of doing parameters (may fail silently)
     * but it allows the code to be much simpler.
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

    //CONSTANTS
    private static final String POSTS_ENDPOINT_ADDRESS = "https://impactnottingham/wp-json/wp/v2/posts";

    public List<Article> parseArticles(JsonReader json) throws IOException {

        List<Article> articles = new ArrayList<>();

        json.beginArray();
        while (json.hasNext()) {
            articles.add(new Article.Builder().parseJSON(json));
        }
        json.endArray();
        json.close();

        return articles;
    }

    public JsonReader getPostsJson(RequestParameters params) {

        try {
            URL                postsURL   = new URL(POSTS_ENDPOINT_ADDRESS);
            HttpsURLConnection connection = (HttpsURLConnection) postsURL.openConnection();
            connection = params.addRequestHeaders(connection);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream       responseStream       = connection.getInputStream();
                InputStreamReader responseStreamReader = new InputStreamReader(responseStream, "UTF-8");
                return new JsonReader(responseStreamReader);
            } else {
                Log.e(TAG, "Connection returned response code " + responseCode + ".");
            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Malformed URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.wtf(TAG, "Opening connection failed");
            e.printStackTrace();
        }

        return null;

    }
}
