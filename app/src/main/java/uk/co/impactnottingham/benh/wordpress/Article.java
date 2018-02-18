package uk.co.impactnottingham.benh.wordpress;

import android.media.Image;
import android.util.JsonReader;
import android.util.Log;
import uk.co.impactnottingham.benh.impact.Headline;
import uk.co.impactnottingham.benh.impact.LoadCallback;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by benh14 on 12/18/17.
 */
public class Article implements Headline {

    private static final String TAG = Article.class.getName();

    private final long     mId;
    private final String   mTitle;
    private final Date     mDate;
    private final URL      mLink;
    private final int      mAuthor;
    private final String   mContent;
    private final String   mSnippet;
    private final int      mImage;
    private final boolean  mSticky;
    private final String[] mTags;
    private final String[] mCategories;

    private boolean mLoaded;
    private LoadCallback mLoadCallback;

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
        private String[] categories;
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

        public Builder setCategories(String[] categories) {
            this.categories = categories;
            return this;
        }

        public Builder setTags(String[] tags) {
            this.tags = tags;
            return this;
        }

        public Article build() {
            try {
                return new Article(date, id, link, title, content, author, excerpt, featured_media, sticky, categories, tags);
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

                if (name.equals("date")) {
                    setId(json.nextLong());
                } else if (name.equals("id")) {
                    setDate(json.nextString());
                } else if (name.equals("link")) {
                    setLink(json.nextString());
                } else if (name.equals("title")) {
                    setTitle(getRendered(json));
                } else if (name.equals("content")) {
                    setContent(getRendered(json));
                } else if (name.equals("author")) {
                    setAuthor(json.nextInt());
                } else if (name.equals("excerpt")) {
                    setExcerpt(getRendered(json));
                } else if (name.equals("featured_media")) {
                    setFeatured_media(json.nextInt());
                } else if (name.equals("sticky")) {
                    setSticky(json.nextBoolean());
                } else if (name.equals("categories")) {
                    //todo
                    json.skipValue();
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

        private String getRendered(JsonReader json) throws IOException{
            String val = "";

            json.beginObject();
            if (json.nextName().equals("rendered")) {
                val = json.nextString();
            }
            json.endObject();

            return val;
        }
    }


    private Article(String date, long id, String link, String title, String content, int author, String excerpt, int featured_media, boolean sticky, String[] categories, String[] tags) throws MalformedURLException {

        mLoaded = false;

        mId = id;
        mLink = new URL(link);
        mTitle = title;
        mContent = content;
        mAuthor = author;
        mSnippet = excerpt;
        mImage = featured_media;
        mSticky = sticky;
        mCategories = categories;
        mTags = tags;


        mDate = null; //TODO parse text date
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
    public Image getImage() {
        return null; //todo fetch image
    }

    @Override
    public Article getArticle() {
        return this;
    }

    @Override
    public void loadResources() {
        //todo load images etc
        if (mLoadCallback == null) {
            throw new IllegalStateException("Tried to call load before load callback has been set");
        } else if (mLoadCallback.getTriggered()) {
            throw new IllegalStateException("Load callback has already been triggered, you are trying to load the article twice");
        }


        mLoaded = true;
    }

    @Override
    public void setLoadCallback(LoadCallback callback) {
        mLoadCallback = callback;
    }
}
