package uk.co.impactnottingham.benh.wordpress;

import android.support.test.InstrumentationRegistry;
import android.util.JsonReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by bth on 14/05/2018.
 */
public class WordpressRESTTest {

    private static final String JSON_MEDIA_FILENAME       = "media_formatted.json";
    private static final String EXPECTED_MEDIUM_LARGE_URL = "https://impactnottingham.com/wp-content/uploads/2018/01/3322890252_1682826803_b-768x511.jpg";
    private static final String EXPECTED_THUMBNAIL_URL    = "https://i1.wp.com/impactnottingham.com/wp-content/uploads/2018/01/3322890252_1682826803_b.jpg?resize=150%2C150&ssl=1";

    private WordpressREST wprest;

    @Before
    public void setUp() {
        wprest = new WordpressREST();
    }


    @Test
    public void TestArticleFetchesSomething() throws IOException {
        Assert.assertNotEquals(0, wprest.getArticlesList(new RequestParameters()).size());
    }

    @Test
    public void testNumberOfArticlesFetched() throws IOException {
        RequestParameters params = new RequestParameters();
        params.addParameter("per_page", "4");
        Assert.assertEquals(4, wprest.getArticlesList(params).size());

        params = new RequestParameters();
        params.addParameter("per_page", "22");
        Assert.assertEquals(22, wprest.getArticlesList(params).size());
    }

    private JsonReader getMediaJson() throws IOException {
        InputStream       stream       = InstrumentationRegistry.getInstrumentation().getContext().getAssets().open(JSON_MEDIA_FILENAME);
        InputStreamReader streamReader = new InputStreamReader(stream, "UTF-8");
        return new JsonReader(streamReader);
    }

    @Test
    public void testParsingImageLink() throws IOException {
        JsonReader mediaJson = getMediaJson();

        Assert.assertEquals(new URL(EXPECTED_MEDIUM_LARGE_URL), new WordpressREST().getImageLinks(mediaJson).get(WordpressREST.IMAGE_SIZE_MEDIUM_LARGE));

        mediaJson.close();
        mediaJson = getMediaJson();

        Assert.assertEquals(new URL(EXPECTED_THUMBNAIL_URL), new WordpressREST().getImageLinks(mediaJson).get(WordpressREST.IMAGE_SIZE_THUMBNAIL));

        mediaJson.close();
    }

}