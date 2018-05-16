package uk.co.impactnottingham.benh.impact;

import uk.co.impactnottingham.benh.wordpress.Article;

/**
 * Created by benh14 on 12/22/17.
 */
public interface Headline {
    String getTitle();
    String getSnippet();
    int getImageId();

    Article getArticle();

    void loadResources();
    void setLoadCallback(LoadCallback callback);
}
