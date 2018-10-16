package sk.siplo.url.shortener.handler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.router.UrlShortenerlRoutereConfig;
import sk.siplo.url.shortener.service.UrlService;
import sk.siplo.url.shortener.service.impl.UrlServiceImpl;

/**
 * Created by siplo on 13/10/2018.
 */
@Import(UrlShortenerlRoutereConfig.class) @RunWith(SpringRunner.class)

@ContextConfiguration(name = "testContext", classes = {
        UrlShortenerlRoutereConfig.class, ShortUrlHandler.class, UrlServiceImpl.class, ResolveUrlHandler.class})

@WebFluxTest(controllers = {ShortUrlHandler.class, ResolveUrlHandler.class})
public class ResolveUrlHandlerTest {

    private ShortUrl OK_URL;

    public static final String URL_ID = "urlId";

    @Autowired ApplicationContext context;

    @MockBean UrlService service;

    private WebTestClient webTestClient;

    @Before public void setUp() {

        webTestClient = WebTestClient.bindToApplicationContext(context).build();
        OK_URL = new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "http://www.google.sk");
    }

    @MockBean(name = "urlService") UrlService urlService;



    @Test public void go() throws Exception {
        given(urlService.findUrlById(anyString())).willReturn(Mono.just(OK_URL));
        webTestClient.get().uri("/resolve/{id}", URL_ID).accept(TEXT_PLAIN).exchange().expectStatus()
                .isTemporaryRedirect().expectHeader().valueMatches(HttpHeaders.LOCATION, OK_URL.getOriginalUrl());

    }

    @Test public void go_url_isNotValid() throws Exception {
        OK_URL.setValid(false);
        given(urlService.findUrlById(anyString())).willReturn(Mono.just(OK_URL));
        webTestClient.get().uri("/resolve/{id}", URL_ID).accept(TEXT_PLAIN).exchange().expectStatus().is5xxServerError()
                .expectBody(String.class).isEqualTo(
                String.format("Url %s is not valid anymore. Destination %s", OK_URL.getCreatedUrl(),
                        OK_URL.getOriginalUrl()));

    }
}
