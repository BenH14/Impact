package uk.co.impactnottingham.benh.impact;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import uk.co.impactnottingham.benh.glide.AppGlideModuleImplementation;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final List<Article> articles;

    private RelativeLayout rootLayout;
    private RecyclerView mRecyclerView;

    private HeadlineAdapter mAdapter;

    public MainActivity() {
        articles = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.activity_main);
        mRecyclerView = findViewById(R.id.recycler_headlines);

        mAdapter = new HeadlineAdapter(new ArrayList<>());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitle("Toolbar Title");

        //Load Articles
        new GetArticlesTask(this::onArticlesLoad).execute(new RequestParameters());
    }

    private void onArticlesLoad(List<Article> newArticles) {
        Log.d(TAG, "onArticlesLoad: New articles loaded");
        this.articles.addAll(newArticles);
        runOnUiThread(() -> {
            for (Article a : newArticles) {
                mAdapter.add(a);
            }
        });
    }

    @UiThread
    private void addHeadline(Article article) {
        View headline = getLayoutInflater().inflate(R.layout.headline_list_row, findViewById(R.id.activity_main), false);
        Log.d(TAG, "addHeadline: inflating headlines view");

        TextView title   = headline.findViewById(R.id.headline_title);
        TextView excerpt = headline.findViewById(R.id.headline_excerpt);

        title.setText(article.getTitle());
        excerpt.setText(article.getSnippet());
        rootLayout.addView(headline);

        new GetImageLinkTask(WordpressREST.IMAGE_SIZE_THUMBNAIL, (URL url) -> runOnUiThread(() ->
                GlideApp.with(this)
                        .asDrawable()
                        .load(url.toString())
                        .thumbnail(0.1f)
                        .into((ImageView) headline.findViewById(R.id.headline_image)))
        ).execute(article.getImageId());
    }
}
