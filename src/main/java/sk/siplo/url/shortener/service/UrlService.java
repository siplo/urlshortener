package sk.siplo.url.shortener.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;

/**
 * Created by siplo on 11/10/2018.
 */
public interface UrlService {

    Mono<ShortUrl> createShortUrl(Mono<ShortUrl> longUrl);

    Mono<ShortUrl> findUrlById(String id);

    Flux<ShortUrl> findAllUrls();

    Mono<ShortUrl> updateUrl(String id, Mono<ShortUrl> newValue);

    Mono<Boolean> deleteOldUrls();

}
