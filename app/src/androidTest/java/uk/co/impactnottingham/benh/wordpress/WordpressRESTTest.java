package uk.co.impactnottingham.benh.wordpress;

import android.util.JsonReader;
import android.util.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bth on 14/05/2018.
 */
public class WordpressRESTTest {

    private WordpressREST wprest;

    @Before
    public void setUp() {
        wprest = new WordpressREST();
    }


    @Test
    public void TestArticleFetchesSomething() {
        RequestParameters params = new RequestParameters();
        JsonReader json = wprest.getPostsJson(new RequestParameters());

        Assert.assertNotNull(json);

        List<Article> l = new ArrayList<>();

        try {
            l = wprest.parseArticles(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Assert.assertNotEquals(0, l.size());
    }

}