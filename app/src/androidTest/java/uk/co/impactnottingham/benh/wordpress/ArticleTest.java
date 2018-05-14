package uk.co.impactnottingham.benh.wordpress;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;
import org.json.JSONArray;
import org.junit.*;
import org.junit.runner.RunWith;
import uk.co.impactnottingham.benh.impact.LoadCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by benh14 on 1/28/18.
 */
@RunWith(AndroidJUnit4.class)
public class ArticleTest {

    private static final String JSON_FILENAME = "posts_test_data_formatted.json";

    //Expected Values
    private static final String            EXPECTED_TITLE       = "Title";
    private static final GregorianCalendar EXPECTED_DATE        = new GregorianCalendar(2018, 1, 26, 10, 42, 25);
    private static final int               EXPECTED_ID          = 116742;
    private static final boolean           EXPECTED_STICKY      = false;
    private static final String            EXPECTED_SNIPPET     = "T";
    private static final int               EXPECTED_AUTHOR_CODE = 123;
    private static final String            EXPECTED_CONTENT     = "Test";
    private static final String            EXPECTED_URL         = "https://impactnottingham.com/2018/01/test/";
    private static final String[]          EXPECTED_CATEGORIES  = {"Comment", "Features", "Voices"};
    private static final String[]          EXPECTED_TAGS        = {"blue monday",
                                                                   "christmas",
                                                                   "comment",
                                                                   "depression",
                                                                   "features",
                                                                   "january",
                                                                   "january blues",
                                                                   "laura hanton",
                                                                   "mental health",
                                                                   "mind",
                                                                   "sad",
                                                                   "seasonal affective disorder",
                                                                   "winter"};


    private static JsonReader mArticleJson;
    private static Article    mArticle;

    @BeforeClass
    public static void setUp() throws Exception {
        //Copy file to device
        InputStream       stream       = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(JSON_FILENAME);
        InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
        mArticleJson = new JsonReader(streamReader);
        mArticle = new Article.Builder().parseJSON(mArticleJson);
        mArticleJson.close();

        CountDownLatch cdl = new CountDownLatch(1);

        mArticle.setLoadCallback(cdl::countDown);
        mArticle.loadResources();

        cdl.await(5, TimeUnit.SECONDS);
    }

    /*
     * JSON Parsing Tests
     */
    @Test
    public void testJsonParseTitle() {
        Assert.assertEquals(EXPECTED_TITLE, mArticle.getTitle());
    }

    @Test
    public void testJsonParseDate() {
        try {
            Assert.assertEquals(0, EXPECTED_DATE.compareTo(mArticle.getDate()));  // Returns 0 if they're equal
        } catch (NullPointerException notSet) {
            Assert.fail("Article Null Pointer Exception");
        }
    }

    @Test
    public void testJsonParseId() {
        Assert.assertEquals(EXPECTED_ID, mArticle.getId());
    }

    @Test
    public void testJsonParseLink() throws MalformedURLException {
        Assert.assertEquals(new URL(EXPECTED_URL), mArticle.getLink());
    }

    @Test
    public void testJsonParseContent() {
        Assert.assertEquals(EXPECTED_CONTENT, mArticle.getContent());
    }

    @Test
    public void testJsonParseAuthor() {
        Assert.assertEquals(EXPECTED_AUTHOR_CODE, mArticle.getAuthor());
    }

    @Test
    public void testJsonParseSnippet() {
        Assert.assertEquals(EXPECTED_SNIPPET, mArticle.getSnippet());
    }

    @Test
    public void testJsonParseImage() {
        Assert.assertNotNull(mArticle.getImage());
    }

    @Test
    public void testJsonParseSticky() {
        Assert.assertEquals(EXPECTED_STICKY, mArticle.isSticky());
    }

    @Test
    public void testJsonParseCategories() {
        Assert.assertArrayEquals(EXPECTED_CATEGORIES, mArticle.getCategories());
    }

    @Test
    public void testJsonParseTags() {
        Assert.assertArrayEquals(EXPECTED_TAGS, mArticle.getTags());
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }
}