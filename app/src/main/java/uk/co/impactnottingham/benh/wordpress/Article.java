package uk.co.impactnottingham.benh.wordpress;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import uk.co.impactnottingham.benh.impact.Category;
import uk.co.impactnottingham.benh.impact.Headline;
import uk.co.impactnottingham.benh.impact.LoadCallback;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by benh14 on 12/18/17.
 */
public class Article implements Headline, Serializable {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MS_IN_SECOND      = 1000;
    private static final int MINUTES_IN_HOUR   = 60;
    private static final int HOURS_IN_DAY      = 24;
    private static final int DAYS_IN_MONTH     = 30;

    private static final String TAG = Article.class.getName();

    private static final int BREAKING_CATEGORY_ID = 19171;

    private static final boolean PRELOAD_IMAGE_LINKS = true;

    private final long              mId;
    private final String            mTitle;
    private final GregorianCalendar mDate;
    private final URL               mLink;
    private final int               mAuthor;
    private final String            mContent;
    private final String            mSnippet;
    private final boolean           mSticky;
    private final boolean           mPodcast;
    private final boolean           mBreaking;
    private final String[]          mTags;
    private final Category          mCategory;

    private final int               mImageId;
    private       Map<Integer, URL> mImageLinks;
    private       LoadCallback      mLoadCallback;

    private boolean mLoadingImageLink;  // I could use an atomic reference but it causes issues with serializing

    /**
     * All fields in this class are mandatory.
     */
    public static class Builder {
        private String   date;
        private long     id;
        private String   link;
        private String   title;
        private String   content;
        private int      author;
        private String   excerpt;
        private int      featured_media;
        private boolean  sticky;
        private boolean  breaking;
        private boolean  podcast  = false;
        private Category category = Category.DEFAULT;
        private String[] tags;

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContent(String content) {
            this.content = content;
            return this;
        }

        public Builder setAuthor(int author) {
            this.author = author;
            return this;
        }

        public Builder setExcerpt(String excerpt) {
            this.excerpt = excerpt;
            return this;
        }

        public Builder setFeatured_media(int featured_media) {
            this.featured_media = featured_media;
            return this;
        }

        public Builder setSticky(boolean sticky) {
            this.sticky = sticky;
            return this;
        }

        public Builder setCategory(JsonReader json) throws IOException {
            json.beginArray();
            while (json.hasNext()) {
                int categoryId = json.nextInt();
                for (Category c : Category.values()) {
                    if (categoryId == c.getId()) {
                        if (c == Category.PODCAST) {
                            podcast = true;  // We don't ever want a category to be set to podcast
                        } else {
                            category = c;
                        }
                    }
                }
                if (categoryId == BREAKING_CATEGORY_ID) {
                    breaking = true;
                }
            }
            json.endArray();
            return this;
        }

        public Builder setPodcast(boolean podcast) {
            this.podcast = true;
            return this;
        }

        public Builder setTags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public Article build() {
            try {
                return new Article(date, id, link, title, content, author, excerpt, featured_media, sticky, breaking, podcast, category, tags);
            } catch (MalformedURLException e) {
                Log.wtf(TAG, "Malformed URL in article build");
                e.printStackTrace();
            }
            return null;
        }

        public Article parseJSON(JsonReader json) throws IOException {

            json.beginObject();

            while (json.hasNext()) {
                String name = json.nextName();

                if (name.equals("id")) {
                    setId(json.nextLong());
                } else if (name.equals("date")) {
                    setDate(json.nextString());
                } else if (name.equals("link")) {
                    setLink(json.nextString());
                } else if (name.equals("title")) {
                    setTitle(fromHTML(getRendered(json)));
                } else if (name.equals("content")) {
                    setContent(getRendered(json));  // todo do we need to convert from html?
                } else if (name.equals("author")) {
                    setAuthor(json.nextInt());
                } else if (name.equals("excerpt")) {
                    setExcerpt(fromHTML(getRendered(json)));
                } else if (name.equals("featured_media")) {
                    setFeatured_media(json.nextInt());
                } else if (name.equals("sticky")) {
                    setSticky(json.nextBoolean());
                } else if (name.equals("categories")) {
                    setCategory(json);
                } else if (name.equals("tags")) {
                    //todo
                    json.skipValue();
                } else {
                    json.skipValue();
                }
            }

            json.endObject();

            return build();

        }

        private String getRendered(JsonReader json) throws IOException {
            String val = null;

            json.beginObject();
            while (json.hasNext()) {
                if (json.nextName().equals("rendered")) {
                    val = json.nextString();
                } else {
                    json.skipValue();
                }
            }
            json.endObject();

            if (val != null) {
                return val;
            } else {
                Log.e(TAG, "JSON data handed to getRendered() did not contain a name that equals \"rendered\"");
                return "ERROR NAME NOT FOUND";
            }
        }

        private String fromHTML(@NonNull String html) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return String.valueOf(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
            } else {
                //noinspection deprecation
                return String.valueOf(Html.fromHtml(html));
            }
        }
    }


    private Article(String date, long id, String link, String title, String content, int author, String excerpt, int featured_media, boolean sticky, boolean breaking, boolean podcast, Category category, String[] tags) throws MalformedURLException {
        mId = id;
        mLink = new URL(link);
        mTitle = title;
        mContent = content;
        mAuthor = author;
        mSnippet = excerpt;
        mImageId = featured_media;
        mSticky = sticky;
        mBreaking = breaking;
        mPodcast = podcast;
        mCategory = category;
        mTags = tags;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);
        mDate = new GregorianCalendar();
        try {
            mDate.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mLoadingImageLink = false;

        if (PRELOAD_IMAGE_LINKS) {
            loadImageLink();
        }
    }

    /**
     * Generates a string of how long ago this article was published, eg 5 mins
     *
     * @return a string of how many minutes, hours, days or months ago this was published
     */
    public String getTimeFromNow(GregorianCalendar date) {
        long timeFromNow = System.currentTimeMillis() - date.getTimeInMillis();

        timeFromNow /= MS_IN_SECOND;  // Into Seconds
        if (timeFromNow < SECONDS_IN_MINUTE) {
            return "just now";
        }

        timeFromNow /= SECONDS_IN_MINUTE;  // Into Minutes
        if (timeFromNow < MINUTES_IN_HOUR) {
            return timeFromNow + (timeFromNow == 1 ? " minute" : " minutes");
        }

        timeFromNow /= MINUTES_IN_HOUR;  // Into Hours
        if (timeFromNow < HOURS_IN_DAY) {
            return timeFromNow + (timeFromNow == 1 ? " hour" : " hours");
        }

        timeFromNow /= HOURS_IN_DAY;  // Into days
        if (timeFromNow < DAYS_IN_MONTH) {
            return timeFromNow + (timeFromNow == 1 ? " day" : " days");
        }

        timeFromNow /= DAYS_IN_MONTH;  // Roughly months
        return timeFromNow + (timeFromNow == 1 ? " month" : " months");
    }

    public String getTimeFromNow() {
        return getTimeFromNow(getDate());
    }

    public long getId() {
        return mId;
    }

    public GregorianCalendar getDate() {
        return mDate;
    }

    public URL getLink() {
        return mLink;
    }

    public int getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public boolean isSticky() {
        return mSticky;
    }

    public boolean isBreaking() {
        return mBreaking;
    }

    public boolean isPodcast() {
        return mPodcast;
    }

    public String[] getTags() {
        return mTags;
    }

    public Category getCategory() {
        return mCategory;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    @Override
    public int getImageId() {
        return mImageId;
    }

    @Override
    public Article getArticle() {
        return this;
    }

    public URL getImageLink(int size) {
        Log.i(TAG, "getImageLink: mImagelinks size = " + mImageLinks.size());
        if (mImageLinks.containsKey(size)) {
            return mImageLinks.get(size);
        } else {
            Log.w(TAG, "getImageLink: No correct size img link found, returning any size", null);
            return (URL) mImageLinks.values().toArray()[0];
        }
    }

    public boolean hasLink() {
        if (mImageLinks == null) {
            return false;
        }
        return mImageLinks.size() > 0;
    }

    public boolean isLoadingImageLink() {
        return mLoadingImageLink;
    }
    public void setLoadCallback(LoadCallback callback) {
        mLoadCallback = callback;
    }

    @Override
    public void loadImageLink(@Nullable LoadCallback callback) {
        if (callback != null) {
            mLoadCallback = callback;
        }
            mLoadingImageLink = true;

        new GetImageLinkTask((Map<Integer, URL> urls) -> {
            this.mImageLinks = urls;

            if (mLoadCallback != null) {
                mLoadCallback.onLoad();
            }
            mLoadingImageLink = false;
        }).execute(getImageId());
    }

    public void loadImageLink() {
        loadImageLink(null);
    }
}
