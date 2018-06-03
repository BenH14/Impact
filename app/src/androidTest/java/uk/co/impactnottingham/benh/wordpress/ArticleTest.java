package uk.co.impactnottingham.benh.wordpress;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.JsonReader;
import org.junit.*;
import org.junit.runner.RunWith;
import uk.co.impactnottingham.benh.impact.Category;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by benh14 on 1/28/18.
 */
@RunWith(AndroidJUnit4.class)
public class ArticleTest {

    private static final String JSON_FILENAME = "posts_test_data_formatted.json";

    //Expected Values
    private static final String            EXPECTED_TITLE       = "Title";
    private static final GregorianCalendar EXPECTED_DATE        = new GregorianCalendar(2018, 0, 26, 10, 42, 25);  // Months are 0 indexed
    private static final int               EXPECTED_ID          = 116742;
    private static final boolean           EXPECTED_STICKY      = false;
    private static final String            EXPECTED_SNIPPET     = "T";
    private static final int               EXPECTED_AUTHOR_CODE = 123;
    private static final String            EXPECTED_CONTENT     = "Test";
    private static final String            EXPECTED_URL         = "https://impactnottingham.com/2018/01/test/";
    private static final boolean           EXPECTED_BREAKING    = true;
    private static final Category          EXPECTED_CATEGORIES  = Category.FEATURES;
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


    private static Article mArticle;

    @BeforeClass
    public static void setUp() throws Exception {
        //Copy file to device
        InputStream       stream       = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(JSON_FILENAME);
        InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
        JsonReader        articleJson  = new JsonReader(streamReader);
        mArticle = new Article.Builder().parseJSON(articleJson);
        articleJson.close();

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
            Assert.assertEquals(EXPECTED_DATE.getTimeInMillis(), mArticle.getDate().getTimeInMillis());  // Returns 0 if they're equal
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
        Assert.assertEquals(1234, mArticle.getImageId());
    }

    @Test
    public void testJsonParseSticky() {
        Assert.assertEquals(EXPECTED_STICKY, mArticle.isSticky());
    }

    @Test
    public void testJsonParseCategories() {
        Assert.assertEquals(EXPECTED_CATEGORIES, mArticle.getCategory());
        Assert.assertEquals(EXPECTED_BREAKING, mArticle.isBreaking());
    }

    @Test
    public void testJsonParseTags() {
        Assert.assertArrayEquals(EXPECTED_TAGS, mArticle.getTags());
    }

    @AfterClass
    public static void tearDown() throws Exception {
    }
}