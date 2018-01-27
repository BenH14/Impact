package uk.co.impactnottingham.benh.wordpress;

import android.media.Image;
import uk.co.impactnottingham.benh.impact.Headline;
import uk.co.impactnottingham.benh.impact.LoadCallback;

import java.net.URL;
import java.util.Date;

/**
 * Created by benh14 on 12/18/17.
 */
public class Article implements Headline {

    private long     mId;
    private String   mTitle;
    private Date     mDate;
    private URL      mLink;
    private int      mAuthor;
    private String   mContent;
    private String   mSnippet;
    private int      mImage;
    private boolean  sticky;
    private String[] tags;
    private String[] Categories;


    public Article(String date, long id, String link, String title, String content, int author, String excerpt, int featured_media, boolean sticky, String[] categories, String[] tags) {

    }


    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return null;  // todo
    }

    @Override
    public Image getImage() {
        return null;
    }

    @Override
    public Article getArticle() {
        return this;
    }

    @Override
    public void load() {

    }

    @Override
    public void setLoadCallback(LoadCallback callback) {

    }
}
