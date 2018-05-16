package uk.co.impactnottingham.benh.wordpress;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    URL getImageLink(JsonReader json, int size) throws IOException {

        URL    url = null;
        String sizeName;

        switch (size) {
            case IMAGE_SIZE_FULL:
                sizeName = "full";
                break;
            case IMAGE_SIZE_LARGE:
                sizeName = "large";
                break;
            case IMAGE_SIZE_MEDIUM_LARGE:
                sizeName = "medium_large";
                break;
            case IMAGE_SIZE_MEDIUM:
                sizeName = "medium";
                break;
            case IMAGE_SIZE_THUMBNAIL:
                sizeName = "thumbnail";
                break;
            default:
                throw new IllegalArgumentException("Invalid image size requested, use the constants");
        }

        json.beginObject();

        while (json.hasNext()) {
            String name = json.nextName();
            if (name.equals("media_details") || name.equals("sizes") || name.equals(sizeName)) {
                json.beginObject();
            } else if (name.equals("source_url")) {
                url = new URL(json.nextString());
            } else {
                json.skipValue();
            }
        }

        return url;
    }

    URL loadImageLink(int id, int size) throws IOException {
        JsonReader json = getMediaJson(id);
        return getImageLink(json, size);
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

    List<Article> getArticlesList(RequestParameters params) throws IOException {
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
