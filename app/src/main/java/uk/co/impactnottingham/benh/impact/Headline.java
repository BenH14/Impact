package uk.co.impactnottingham.benh.impact;

import android.media.Image;
import uk.co.impactnottingham.benh.wordpress.Article;

/**
 * Created by benh14 on 12/22/17.
 */
public interface Headline {
    String getTitle();
    String getSnippet();
    Image getImage();

    Article getArticle();

    void load();
    void setLoadCallback(LoadCallback callback);
}
