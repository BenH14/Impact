package uk.co.impactnottingham.benh.wordpress;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;

/**
 * Created by bth on 20/05/2018.
 */
public class GetArticlesTask extends AsyncTask<RequestParameters, Void, Void> {

    private final ArticlesLoadListener mListener;

    public interface ArticlesLoadListener {
        void onLoad(List<Article> articleList);
    }

    public GetArticlesTask(ArticlesLoadListener listener) {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(RequestParameters... requestParameters) {
        for (RequestParameters params : requestParameters) {
            try {
                List articles = new WordpressREST().getArticlesList(params);
                mListener.onLoad(articles);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
