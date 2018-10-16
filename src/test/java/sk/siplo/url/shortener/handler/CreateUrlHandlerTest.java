package sk.siplo.url.shortener.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
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
        UrlShortenerlRoutereConfig.class, CreateUrlHandler.class, UrlServiceImpl.class, ResolveUrlHandler.class})

@WebFluxTest(controllers = {CreateUrlHandler.class, ResolveUrlHandler.class}) public class CreateUrlHandlerTest {

    public static final String MISSING_URL = "missing url";
    @Autowired ApplicationContext context;

    @MockBean UrlService service;

    private WebTestClient webTestClient;
    public static final ShortUrl CREATE_URL =
            new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "http://www.google.sk");

    public static final ShortUrl EMPTY_URL = new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "");

    @Before public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @MockBean(name = "urlService") UrlService urlService;

    @Test public void createShortUrl() throws Exception {
        given(urlService.createShortUrl(any())).willReturn(Mono.just(CREATE_URL));
        webTestClient.post().uri("/url").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(CREATE_URL)).exchange().expectStatus().isCreated()
                .expectBody(ShortUrl.class).isEqualTo(CREATE_URL);
    }

    @Test public void createShortUrl_missingUrlValue() throws Exception {
        given(urlService.createShortUrl(any())).willReturn(Mono.error(new IllegalArgumentException(MISSING_URL)));
        webTestClient.post().uri("/url").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(EMPTY_URL)).exchange().expectStatus().is5xxServerError()
                .expectBody(String.class).isEqualTo(MISSING_URL);
    }

    @Test public void createShortUrl_notUpdated() throws Exception {
        given(urlService.createShortUrl(any())).willReturn(Mono.empty());
        webTestClient.post().uri("/url").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(EMPTY_URL)).exchange().expectStatus().is5xxServerError()
                .expectBody(String.class).isEqualTo("Empty input");
    }

    @Test public void update() throws Exception {
        given(urlService.updateUrl(anyString(), any())).willReturn(Mono.just(CREATE_URL));
        webTestClient.put().uri("/url/{id}", "uniqueUrlHash").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(CREATE_URL)).exchange().expectStatus().isOk().expectBody(ShortUrl.class)
                .isEqualTo(CREATE_URL);
    }

    @Test public void update_() throws Exception {
        given(urlService.updateUrl(anyString(), any())).willReturn(Mono.empty());
        webTestClient.put().uri("/url/{id}", "uniqueUrlHash").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(CREATE_URL)).exchange().expectStatus().isOk().expectBody(ShortUrl.class)
                .isEqualTo(CREATE_URL);
    }


}
