package sk.siplo.url.shortener.handler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import reactor.core.publisher.Flux;
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

@WebFluxTest(controllers = {CreateUrlHandler.class, ResolveUrlHandler.class}) public class ResolveUrlHandlerTest {

    private ShortUrl OK_URL;

    public static final ShortUrl EMPTY_URL = new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "");
    public static final String URL_ID = "urlId";

    @Autowired ApplicationContext context;

    @MockBean UrlService service;

    private WebTestClient webTestClient;

    @Before public void setUp() {

        webTestClient = WebTestClient.bindToApplicationContext(context).build();
        OK_URL = new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "http://www.google.sk");
    }

    @MockBean(name = "urlService") UrlService urlService;

    @Test public void getAllUrls() throws Exception {

        List<ShortUrl> a = Arrays.asList(OK_URL, OK_URL, OK_URL);
        given(urlService.findAllUrls()).willReturn(Flux.fromIterable(a));
        webTestClient.get().uri("/resolve").accept(APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBodyList(ShortUrl.class).contains(OK_URL).contains(OK_URL).contains(OK_URL);

    }

    @Test public void findUrlById() throws Exception {
        given(urlService.findUrlById(anyString())).willReturn(Mono.just(OK_URL));
        webTestClient.get().uri("/resolve/{id}", "urlId").accept(APPLICATION_JSON).exchange().expectStatus().isOk()
                .expectBody(ShortUrl.class).isEqualTo(OK_URL);

    }

    @Test public void findUrlById_urlNotFound() throws Exception {
        given(urlService.findUrlById(anyString())).willReturn(Mono.empty());
        webTestClient.get().uri("/resolve/{id}", URL_ID).accept(APPLICATION_JSON).exchange().expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo(String.format("Url for id: %s not found", URL_ID));

    }

    @Test public void go() throws Exception {
        given(urlService.findUrlById(anyString())).willReturn(Mono.just(OK_URL));
        webTestClient.get().uri("/resolve/{id}", URL_ID).accept(TEXT_PLAIN).exchange().expectStatus()
                .isPermanentRedirect().expectHeader().valueMatches(HttpHeaders.LOCATION, OK_URL.getOriginalUrl());

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
