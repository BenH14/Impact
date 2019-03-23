package uk.co.impactnottingham.benh.impact;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.GetArticlesTask;
import uk.co.impactnottingham.benh.wordpress.RequestParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int    ARTICLES_PER_REQUEST = 10;
    private static final int    SCROLL_LOAD_OFFSET   = 3;
    public static final String GET_INVOLVED_URL = "https://impactnottingham.com/get-involved/";

    private final List<Article> articles;

    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawer;

    private int mPage;

    private AtomicReference<Boolean> loading;

    private HeadlineAdapter mAdapter;
    private Category        mCategory;
    private ProgressBar     mBackgroundSpinner;
    private ProgressBar     mBottomSpinner;

    public MainActivity() {
        mPage = 1;
        articles = new ArrayList<>();
        loading = new AtomicReference<>(false);
    }

    private void changeCategory(Category category) {
        mCategory = category;
        mPage = 1;
        Drawable actionbar = getDrawable(R.drawable.actionbar_bg);
        if (mCategory == Category.DEFAULT) {
            actionbar.clearColorFilter();
        } else {
            actionbar.setColorFilter(new PorterDuffColorFilter(mCategory.getColor(this), PorterDuff.Mode.SRC_ATOP));
        }
        getSupportActionBar().setBackgroundDrawable(actionbar);
        clearArticles();
        loadArticles();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_headlines);
        mDrawer = findViewById(R.id.drawer_layout);
        mBackgroundSpinner = findViewById(R.id.background_spinner);
        mBottomSpinner = findViewById(R.id.bottom_spinner);

        mCategory = Category.DEFAULT;

        //Set on click listeners for the navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
//        navView.setBackgroundResource(R.drawable.drawer_background);
        navView.setCheckedItem(R.id.nav_home);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    changeCategory(Category.DEFAULT);
                    break;
                case R.id.nav_news:
                    changeCategory(Category.NEWS);
                    break;
                case R.id.nav_features:
                    changeCategory(Category.FEATURES);
                    break;
                case R.id.nav_lifestyle:
                    changeCategory(Category.LIFESTYLE);
                    break;
                case R.id.nav_entertainment:
                    changeCategory(Category.ENTERTAINMENT);
                    break;
                case R.id.nav_reviews:
                    changeCategory(Category.REVIEWS);
                    break;
                case R.id.nav_sport:
                    changeCategory(Category.SPORT);
                    break;
                case R.id.nav_get_involved:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GET_INVOLVED_URL));
                    startActivity(browserIntent);
                    break;
                default:
                    Log.w(TAG, "onCreate: Weird item selected in navview, probably haven't created switch case");
            }
            mDrawer.closeDrawers();
            return true;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDrawerTints(navView, PorterDuff.Mode.DST_IN);
        }

        View header = navView.getHeaderView(0);
        TextView printIssueCountdownNum = header.findViewById(R.id.print_issue_countdown);
        TextView printIssueCountdownLabel = header.findViewById(R.id.print_issue_countdown_label);

        if (printIssueCountdownNum != null && printIssueCountdownLabel != null) {
            Log.i(TAG, "onCreate: print issue countdown notnull");
            new PrintIssueCountdown(printIssueCountdownNum, printIssueCountdownLabel).start(this);
        } else {
            Log.i(TAG, "onCreate: print issue countdown null");
        }

        mAdapter = new HeadlineAdapter(new ArrayList<>(), getSupportFragmentManager());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int itemCount       = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (dy > 0) {  // Only look if we're scrolling down

                    if (itemCount - SCROLL_LOAD_OFFSET <= lastVisibleItem) {
                        loadArticles();
                    }
                }

                if (lastVisibleItem == itemCount - 1 && !mBackgroundSpinner.isShown()) {
                    mBottomSpinner.setVisibility(View.VISIBLE);
                } else {
                    mBottomSpinner.setVisibility(View.INVISIBLE);
                }
            }
        });

        Toolbar mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
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
        RequestParameters parameters = new RequestParameters();
        if (mCategory != Category.DEFAULT) {
            parameters.addParameter("categories", String.valueOf(mCategory.getId()));
            Log.i(TAG, "loadArticles: category = " + mCategory.getId());
        }
        loadArticles(parameters);
    }

    private void loadArticles(RequestParameters parameters) {
        if (!loading.getAndSet(true)) {
            Log.d(TAG, "loadArticles: Loading new Articles");
            parameters.addParameter("page", String.valueOf(mPage));
            parameters.addParameter("per_page", String.valueOf(ARTICLES_PER_REQUEST));

            new GetArticlesTask(this::onArticlesLoad).execute(parameters);
            mPage++;
        }
    }

    private void clearArticles() {
        articles.clear();
        runOnUiThread(() -> mAdapter.clear());
        mBackgroundSpinner.setVisibility(View.VISIBLE);
        mBottomSpinner.setVisibility(View.INVISIBLE);
    }

    private void onArticlesLoad(List<Article> newArticles) {
        Log.d(TAG, "onArticlesLoad: New articles loaded ");
        loading.set(false);


        this.articles.addAll(newArticles);
        runOnUiThread(() -> {
            mBackgroundSpinner.setVisibility(View.INVISIBLE);
            for (Article a : newArticles) {
                mAdapter.add(a);
            }
        });
    }

    @SuppressLint("NewApi")
    private void setDrawerTints(NavigationView navView, PorterDuff.Mode mode) {
        navView.getMenu().findItem(R.id.nav_news).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_features).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_lifestyle).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_entertainment).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_reviews).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_sport).setIconTintMode(mode);
        navView.getMenu().findItem(R.id.nav_podcasts).setIconTintMode(mode);
    }
}
