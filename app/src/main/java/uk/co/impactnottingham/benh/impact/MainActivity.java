package uk.co.impactnottingham.benh.impact;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG                      = "MainActivity";
    private static final int    ARTICLES_PER_REQUEST     = 10;
    private static final int    SCROLL_LOAD_OFFSET       = 1;

    private final List<Article> articles;

    private RelativeLayout rootLayout;
    private RecyclerView mRecyclerView;

    private int mPage;

    private AtomicReference<Boolean> loading;

    private HeadlineAdapter mAdapter;

    public MainActivity() {
        mPage = 1;
        articles = new ArrayList<>();
        loading = new AtomicReference<>(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.activity_main);
        mRecyclerView = findViewById(R.id.recycler_headlines);

        mAdapter = new HeadlineAdapter(new ArrayList<>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                Log.d(TAG, "onScrolled: itemCount = " + itemCount + "     last visible = " + lastVisibleItem);

                if(itemCount - SCROLL_LOAD_OFFSET <= lastVisibleItem) {
                    loadArticles();
                }
            }
        });

        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
        mainToolbar.setTitle("Toolbar Title");

        loadArticles();
    }

    private void loadArticles() {
        if (!loading.getAndSet(true)) {
            Log.d(TAG, "loadArticles: Loading new Articles");
            RequestParameters params = new RequestParameters();
            params.addParameter("page", String.valueOf(mPage));
            params.addParameter("per_page", String.valueOf(ARTICLES_PER_REQUEST));

            new GetArticlesTask(this::onArticlesLoad).execute(params);
            mPage++;
        }
    }

    private void onArticlesLoad(List<Article> newArticles) {
        Log.d(TAG, "onArticlesLoad: New articles loaded ");
        loading.set(false);

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
