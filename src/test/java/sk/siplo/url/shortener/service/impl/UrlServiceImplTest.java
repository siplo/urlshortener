package sk.siplo.url.shortener.service.impl;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import sk.siplo.url.shortener.handler.CreateUrlHandler;
import sk.siplo.url.shortener.handler.ResolveUrlHandler;
import sk.siplo.url.shortener.router.UrlShortenerlRoutereConfig;

/**
 * Created by siplo on 14/10/2018.
 */
@RunWith(SpringRunner.class) @ContextConfiguration(name = "testContext", classes = {
        UrlShortenerlRoutereConfig.class, CreateUrlHandler.class, UrlServiceImpl.class, ResolveUrlHandler.class})
public class UrlServiceImplTest {

    @Before public void setUp() {

    }

    @Test public void createShortUrl() throws Exception {
        fail();
    }

    @Test public void findAllUrls() throws Exception {
        fail();

    }

    @Test public void getShortUrlById() throws Exception {
        fail();
    }

    @Test public void updateUrl() throws Exception {
        fail();
    }

    @Test public void removeOldUrls() throws Exception {
        fail();
    }

}
