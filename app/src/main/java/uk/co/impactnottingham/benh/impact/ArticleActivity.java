package uk.co.impactnottingham.benh.impact;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

import java.util.GregorianCalendar;

public class ArticleActivity extends AppCompatActivity {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MS_IN_SECOND      = 1000;
    private static final int MINUTES_IN_HOUR   = 60;
    private static final int HOURS_IN_DAY      = 24;
    private static final int DAYS_IN_MONTH     = 30;

    private Article mArticle;

    @BindView(R.id.article_image)
    ImageView mImage;
    @BindView(R.id.article_date)
    TextView  mDate;
    @BindView(R.id.article_headline)
    TextView  mTitle;
    @BindView(R.id.article_html)
    WebView   mContent;
    @BindView(R.id.article_toolbar)
    Toolbar   mToolbar;

    public void setData(Article data) {
        mArticle = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_read_article);
        ButterKnife.bind(this);

        setData((Article) getIntent().getSerializableExtra("ARTICLE"));
        setSupportActionBar(mToolbar);

        mTitle.setText(mArticle.getTitle());
        mDate.setText(getTimeFromNow(mArticle.getDate()));

        mArticle.loadImageLink(WordpressREST.IMAGE_SIZE_MEDIUM, () -> runOnUiThread(() -> GlideApp.with(this).load(mArticle.getImageLink().toString()).into(mImage)));
        mContent.loadData(mArticle.getContent(), "text/html", null);
    }

    /**
     * Generates a string of how long ago this article was published, eg 5 mins
     *
     * @return a string of how many minutes, hours, days or months ago this was published
     */
    private String getTimeFromNow(GregorianCalendar date) {
        long timeFromNow = System.currentTimeMillis() - date.getTimeInMillis();

        timeFromNow /= MS_IN_SECOND;  // Into Seconds
        if (timeFromNow < SECONDS_IN_MINUTE) {
            return "just now";
        }

        timeFromNow /= SECONDS_IN_MINUTE;  // Into Minutes
        if (timeFromNow < MINUTES_IN_HOUR) {
            return timeFromNow + " minutes";
        }

        timeFromNow /= MINUTES_IN_HOUR;  // Into Hours
        if (timeFromNow < HOURS_IN_DAY) {
            return timeFromNow + " hours";
        }

        timeFromNow /= HOURS_IN_DAY;  // Into days
        if (timeFromNow < DAYS_IN_MONTH) {
            return timeFromNow + " days";
        }

        timeFromNow /= DAYS_IN_MONTH;  // Roughly months
        return timeFromNow + "months";
    }

}

