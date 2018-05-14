package uk.co.impactnottingham.benh.wordpress;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;
import org.json.JSONArray;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by benh14 on 1/28/18.
 */
@RunWith(AndroidJUnit4.class)
public class ArticleTest {

    private static final String JSON_FILENAME  = "posts_test_data_formatted.json";

    //Expected Values
    private static final String EXPECTED_TITLE = "Title";

    private static JsonReader mArticleJson;
    private static Article mArticle;

    @BeforeClass
    public static void setUp() throws Exception {
        //Copy file to device
        InputStream stream = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(JSON_FILENAME);
        InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
        mArticleJson = new JsonReader(streamReader);
        mArticle = new Article.Builder().parseJSON(mArticleJson);
    }

    @Test
    public void testJsonParseTitle() {
        Assert.assertEquals(EXPECTED_TITLE, mArticle.getTitle());
    }



    @Test
    public void setDate() {
        Assert.assertEquals(new GregorianCalendar(2018, 1, 26, 10, 42, 25),mArticle.getDate());
    }

    @Test
    public void setId() {
    }

    @Test
    public void setLink() {
    }

    @Test
    public void setTitle() {
    }

    @Test
    public void setContent() {
    }

    @Test
    public void setAuthor() {
    }

    @Test
    public void setExcerpt() {
    }

    @Test
    public void setFeatured_media() {
    }

    @Test
    public void setSticky() {
    }

    @Test
    public void setCategories() {
    }

    @Test
    public void setTags() {
    }

    @Test
    public void build() {
    }

    @Test
    public void parseJSON() {
    }

    @AfterClass
    public static void tearDown() throws Exception {
        mArticleJson.close();
    }
}