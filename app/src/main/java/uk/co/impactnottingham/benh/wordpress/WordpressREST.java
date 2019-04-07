package uk.co.impactnottingham.benh.wordpress;

import android.annotation.SuppressLint;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by benh14 on 1/26/18.
 */
public class WordpressREST {

    private static final String TAG = WordpressREST.class.getName();

    //CONSTANTS
    private static final String POSTS_ENDPOINT_ADDRESS = "https://impactnottingham.com/wp-json/wp/v2/posts";
    private static final String MEDIA_ENDPOINT_ADDRESS = "https://impactnottingham.com/wp-json/wp/v2/media/";

    public static final int IMAGE_SIZE_FULL         = 0;
    public static final int IMAGE_SIZE_LARGE        = 1;
    public static final int IMAGE_SIZE_MEDIUM_LARGE = 2;
    public static final int IMAGE_SIZE_MEDIUM       = 3;
    public static final int IMAGE_SIZE_THUMBNAIL    = 4;


    private List<Article> parseArticles(JsonReader json) throws IOException {

        List<Article> articles = new ArrayList<>();

        json.beginArray();
        while (json.hasNext()) {
            articles.add(new Article.Builder().parseJSON(json));
        }
        json.endArray();
        json.close();

        return articles;
    }

    private JsonReader getPostsJson(RequestParameters params) throws IOException {
        return getJson(new URL(POSTS_ENDPOINT_ADDRESS), params);
    }

    private JsonReader getMediaJson(int mediaId) throws IOException {
        return getJson(new URL(MEDIA_ENDPOINT_ADDRESS + String.valueOf(mediaId)), new RequestParameters());
    }

    @SuppressLint("UseSparseArrays")
    Map<Integer, URL> getImageLinks(JsonReader json) throws IOException {
        Map<Integer, URL> links = new HashMap<>();

        int size = 0;

        json.beginObject();

        while (json.hasNext()) {
            String name = json.nextName();
            if (name.equals("media_details") || name.equals("sizes")) {
                json.beginObject();
            } else if (isSizeName(name)) {
                size = getSizeCode(name);
                json.beginObject();
            } else if (name.equals("source_url")) {
                links.put(size, new URL(json.nextString()));
                json.endObject();
            } else {
                json.skipValue();
            }
        }

        return links;
    }

    private boolean isSizeName(String input) {
        return (input.equals("full") ||
                input.equals("large") ||
                input.equals("medium_large") ||
                input.equals("medium") ||
                input.equals("thumbnail"));
    }

    private int getSizeCode(String input) {
        if (input.equals("full")) {
            return IMAGE_SIZE_FULL;
        } else if (input.equals("large")) {
            return IMAGE_SIZE_LARGE;
        } else if (input.equals("medium_large")) {
            return IMAGE_SIZE_MEDIUM_LARGE;
        } else if (input.equals("medium")) {
            return IMAGE_SIZE_MEDIUM;
        } else if (input.equals("thumbnail")) {
            return IMAGE_SIZE_THUMBNAIL;
        }
        throw new IllegalArgumentException("Not a Size Name!");
    }

    Map<Integer, URL> loadImageLinks(int id) throws IOException {
        JsonReader json = getMediaJson(id);
        return getImageLinks(json);
    }

    private JsonReader getJson(URL rootURL, RequestParameters params) throws IOException {
        try {
            URL url = params.addRequestParams(rootURL);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

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

        throw new IOException("No JSON Returned on http request, is the website down?");
    }

    public List<Article> getArticlesList(RequestParameters params) throws IOException {
        JsonReader json = getPostsJson(params);

        List<Article> list = new ArrayList<>();

        try {
            list = parseArticles(json);
            json.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}
