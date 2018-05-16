package uk.co.impactnottingham.benh.wordpress;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by benh14 on 16/05/2018.
 */
public class GetImageLinkTask extends AsyncTask<Integer, Integer, Void> {
    private static final String TAG = GetImageLinkTask.class.getName();

    private final int               size;
    private final ImageLoadListener listener;

    public interface ImageLoadListener {
        void onLoad(URL url);
    }

    public GetImageLinkTask(int size, ImageLoadListener listener) {
        super();
        this.size = size;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        WordpressREST wp = new WordpressREST();
        for (Integer id : integers) {
            try {
                listener.onLoad(wp.loadImageLink(id, size));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Image failed to load");
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
