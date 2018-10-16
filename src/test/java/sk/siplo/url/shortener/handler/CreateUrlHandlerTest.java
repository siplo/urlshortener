package sk.siplo.url.shortener.handler;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import sk.siplo.url.shortener.router.UrlShortenerlRoutereConfig;
import sk.siplo.url.shortener.service.UrlService;
import sk.siplo.url.shortener.service.impl.UrlServiceImpl;

/**
 * Created by siplo on 13/10/2018.
 */
@Import(UrlShortenerlRoutereConfig.class) @RunWith(SpringRunner.class)

@ContextConfiguration(name = "testContext", classes = {
        UrlShortenerlRoutereConfig.class, CreateUrlHandler.class, UrlServiceImpl.class, ResolveUrlHandler.class})

@WebFluxTest(controllers = {CreateUrlHandler.class, ResolveUrlHandler.class}) public class CreateUrlHandlerTest {

    @Autowired ApplicationContext context;

    private WebTestClient webTestClient;

    public CreateUrlHandlerTest() {

    }

    @MockBean(name = "urlService") UrlService urlService;

    @Test public void createShortUrl() throws Exception {
        fail();
    }

    @Test public void createWithError() throws Exception {
        fail();
    }

    @Test public void resolveNotFound() throws Exception {
        fail();

    }

    @Test public void resolve() throws Exception {
        fail();

    }
}
