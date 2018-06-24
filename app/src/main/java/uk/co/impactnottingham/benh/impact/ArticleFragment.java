package uk.co.impactnottingham.benh.impact;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bth on 19/05/2018.
 */
public class ArticleFragment extends Fragment {

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

    public void setData(Article data) {
        mArticle = data;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mTitle.setText(mArticle.getTitle());
        mDate.setText(getTimeFromNow(mArticle.getDate()));

        mArticle.loadImageLink(WordpressREST.IMAGE_SIZE_MEDIUM, () -> view.post(() -> GlideApp.with(view).load(mArticle.getImageLink().toString()).into(mImage)));
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
