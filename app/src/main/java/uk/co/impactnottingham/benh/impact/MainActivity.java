package uk.co.impactnottingham.benh.impact;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
    private static final int    SCROLL_LOAD_OFFSET       = 3;

    private final List<Article> articles;

    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawer;

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

        mRecyclerView = findViewById(R.id.recycler_headlines);
        mDrawer = findViewById(R.id.drawer_layout);

        mAdapter = new HeadlineAdapter(new ArrayList<>());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {  // Only look if we're scrolling down
                    int itemCount       = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

//                    Log.d(TAG, "onScrolled: itemCount = " + itemCount + "     last visible = " + lastVisibleItem);

                    if (itemCount - SCROLL_LOAD_OFFSET <= lastVisibleItem) {
                        loadArticles();
                    }
                }
            }
        });

        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        loadArticles();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
