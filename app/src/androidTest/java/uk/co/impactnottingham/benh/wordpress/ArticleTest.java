package uk.co.impactnottingham.benh.wordpress;

import android.util.JsonReader;
import org.json.JSONArray;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by benh14 on 1/28/18.
 */
public class ArticleTest {

    private static final String JSON_FILENAME  = "posts_test_data_formatted.json";

    //Expected Values
    private static final String EXPECTED_TITLE = "Title";

    private JsonReader mArticleJson;
    private Article mArticle;

    @Before
    public void setUp() throws Exception {
        InputStream stream = new FileInputStream(new File(JSON_FILENAME));
        InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
        mArticleJson = new JsonReader(streamReader);
        mArticle = new Article.Builder().parseJSON(mArticleJson);
    }

    @Test
    public void testJsonParseTitle() {
        Assert.assertEquals(EXPECTED_TITLE, mArticle.getTitle());
    }

    @After
    public void tearDown() throws Exception {
        mArticleJson.close();
    }

}