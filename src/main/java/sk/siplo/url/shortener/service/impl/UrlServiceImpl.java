package sk.siplo.url.shortener.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlService;

/**
 * Created by siplo on 11/10/2018.
 */
@Service public class UrlServiceImpl implements UrlService {

    private static final Logger LOG = LoggerFactory.getLogger(UrlServiceImpl.class);

    public Mono<ShortUrl> createShortUrl(Mono<ShortUrl> input) {
        return Mono.just(new ShortUrl());
    }

    @Override public Mono<ShortUrl> findUrlById(String id) {
        return Mono.just(new ShortUrl());

    }

    @Override public Flux<ShortUrl> findAllUrls() {
        return Flux.fromArray(new ShortUrl[] {new ShortUrl(), new ShortUrl(), new ShortUrl()});
    }

    @Override public Mono<ShortUrl> updateUrl(String id, Mono<ShortUrl> input) {
        return Mono.just(new ShortUrl());
    }

    @Override public Mono<Boolean> deleteOldUrls() {
        return Mono.just(Boolean.TRUE);

    }

}
