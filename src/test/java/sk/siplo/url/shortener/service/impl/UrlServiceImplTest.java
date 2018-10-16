package sk.siplo.url.shortener.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlDaoService;
import sk.siplo.url.shortener.service.UrlService;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
@ContextConfiguration(name = "testContext", classes = {
        UrlServiceImpl.class})
public class UrlServiceImplTest {

    @MockBean(name = "urlDaoService")
    UrlDaoService urlDaoService;

    @Autowired
    UrlService urlService;

    private static final Date OLDER_THEN_24 = java.sql.Date.valueOf(LocalDate.of(2018, 10, 01));
    private ShortUrl LONG_URL;
    private ShortUrl LONG_AFTER_SAVE;
    private ShortUrl EMPTY_URL;
    private Mono<ShortUrl> MONO_LONG_URL;
    private Date createDate = new Date();

    @Before
    public void setUp() {
        //Date createDate = new Date();
        LONG_URL = new ShortUrl(createDate, UUID.randomUUID().toString(), true, "http://www.google.sk");
        LONG_AFTER_SAVE = new ShortUrl(createDate, UUID.randomUUID().toString(), true, "http://www.google.sk");
        ;
        LONG_AFTER_SAVE.setCreatedUrl("http://localhost:8888/resolve/DB514ED4AC4AE73AE9149D48BAC543FB");
        LONG_AFTER_SAVE.setUrlHash("DB514ED4AC4AE73AE9149D48BAC543FB");
        MONO_LONG_URL = Mono.just(LONG_URL);
        EMPTY_URL = new ShortUrl();
    }

    @Test
    public void createShortUrl_shouldReturnUrl() throws Exception {
        given(urlDaoService.saveShortUrl(any())).willReturn(Mono.just(Boolean.TRUE));
        StepVerifier.create(urlService.createShortUrl(MONO_LONG_URL)).expectNext(LONG_AFTER_SAVE).verifyComplete();

        verify(this.urlDaoService, times(1)).saveShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void createShortUrl_canNotInsert() throws Exception {
        given(urlDaoService.saveShortUrl(any())).willReturn(Mono.just(Boolean.FALSE));
        StepVerifier.create(urlService.createShortUrl(MONO_LONG_URL)).expectError(RuntimeException.class).verify();
        verify(this.urlDaoService, times(1)).saveShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);


    }

    @Test
    public void createShortUrl_shouldReturnIllegalArgument() throws Exception {

        given(urlDaoService.saveShortUrl(any())).willReturn(Mono.just(Boolean.TRUE));
        StepVerifier.create(urlService.createShortUrl(Mono.just(EMPTY_URL))).expectError(IllegalArgumentException.class)
                .verify();

        verify(this.urlDaoService, never()).saveShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void createShortUrl_shouldReturnEmpty() throws Exception {

        given(urlDaoService.saveShortUrl(any())).willReturn(Mono.empty());
        StepVerifier.create(urlService.createShortUrl(MONO_LONG_URL)).expectNextCount(0).expectComplete().verify();

        verify(this.urlDaoService, times(1)).saveShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void findAllUrls() throws Exception {
        ShortUrl[] data = new ShortUrl[] {new ShortUrl(), new ShortUrl()};
        given(urlDaoService.findAll()).willReturn(Flux.fromArray(new ShortUrl[] {new ShortUrl(), new ShortUrl()}));
        StepVerifier.create(urlService.findAllUrls()).expectNext(data).verifyComplete();
        verify(this.urlDaoService, times(1)).findAll();
        verifyNoMoreInteractions(this.urlDaoService);

    }

    @Test
    public void getShortUrlById() throws Exception {
        given(urlDaoService.findShortUrl(any())).willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.findUrlById(anyString())).expectComplete();

        verify(this.urlDaoService, times(1)).findShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void updateUrl() throws Exception {
        LONG_URL.setOriginalUrl("Original url");
        given(urlDaoService.findShortUrl(anyString())).willReturn(Mono.just(LONG_URL));
        given(urlDaoService.updateUrl(anyString(), any(ShortUrl.class))).willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.updateUrl(anyString(), MONO_LONG_URL)).expectNext(LONG_URL).verifyComplete();
        verify(this.urlDaoService, times(1)).findShortUrl(anyString());
        verify(this.urlDaoService, times(1)).updateUrl(anyString(), any(ShortUrl.class));
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void updateUrl_shouldNotFoundRecord() throws Exception {
        given(urlDaoService.findShortUrl(anyString())).willAnswer(invocation -> Mono.empty());
        given(urlDaoService.updateUrl(anyString(), any(ShortUrl.class))).willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.updateUrl(anyString(), MONO_LONG_URL))
                .expectError(IllegalArgumentException.class).verify();
        verify(this.urlDaoService, never()).updateUrl(any(), any());
        verify(this.urlDaoService, times(1)).findShortUrl(any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void removeOldUrls() throws Exception {
        LONG_URL.setValid(false);
        Flux<ShortUrl> allUrl =
                Flux.range(1, 4).map(t -> new ShortUrl(OLDER_THEN_24, UUID.randomUUID().toString(), t % 2 == 0, "url"));
        given(urlDaoService.findAll()).willReturn(allUrl);
        given(urlDaoService.updateUrl(anyString(), isA(ShortUrl.class))).willReturn(Mono.just(LONG_URL))
                .willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.deleteOldUrls()).expectNext(Boolean.TRUE).verifyComplete();
        verify(this.urlDaoService, times(1)).findAll();
        verify(this.urlDaoService, times(2)).updateUrl(anyString(), any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void removeOldUrls_notOlderThen24h() throws Exception {
        LONG_URL.setValid(false);
        Flux<ShortUrl> allUrl =
                Flux.range(1, 4).map(t -> new ShortUrl(new Date(), UUID.randomUUID().toString(), true, "url"));
        given(urlDaoService.findAll()).willReturn(allUrl);
        given(urlDaoService.updateUrl(anyString(), isA(ShortUrl.class))).willReturn(Mono.just(LONG_URL))
                .willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.deleteOldUrls()).expectNext(Boolean.TRUE).verifyComplete();
        verify(this.urlDaoService, times(1)).findAll();
        verify(this.urlDaoService, times(0)).updateUrl(any(), any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void removeOldUrls_alreadyMarkedAsNotValid() throws Exception {
        LONG_URL.setValid(false);
        Flux<ShortUrl> allUrl =
                Flux.range(1, 4).map(t -> new ShortUrl(OLDER_THEN_24, UUID.randomUUID().toString(), false, "url"));
        given(urlDaoService.findAll()).willReturn(allUrl);
        given(urlDaoService.updateUrl(anyString(), isA(ShortUrl.class))).willReturn(Mono.just(LONG_URL))
                .willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.deleteOldUrls()).expectNext(Boolean.TRUE).verifyComplete();
        verify(this.urlDaoService, times(1)).findAll();
        verify(this.urlDaoService, times(0)).updateUrl(any(), any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

    @Test
    public void removeOldUrls_somethingIsWrongWithUpdate() throws Exception {
        LONG_URL.setValid(true);
        Flux<ShortUrl> allUrl =
                Flux.range(1, 4).map(t -> new ShortUrl(OLDER_THEN_24, UUID.randomUUID().toString(), true, "url"));
        given(urlDaoService.findAll()).willReturn(allUrl);
        given(urlDaoService.updateUrl(anyString(), isA(ShortUrl.class))).willReturn(Mono.just(LONG_URL))
                .willReturn(Mono.just(LONG_URL));
        StepVerifier.create(urlService.deleteOldUrls()).expectNext(Boolean.FALSE).verifyComplete();
        verify(this.urlDaoService, times(1)).findAll();
        verify(this.urlDaoService, times(4)).updateUrl(any(), any());
        verifyNoMoreInteractions(this.urlDaoService);
    }

}
