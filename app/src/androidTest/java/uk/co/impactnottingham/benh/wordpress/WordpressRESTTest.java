package uk.co.impactnottingham.benh.wordpress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

}