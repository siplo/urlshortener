package sk.siplo.url.shortener.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;

/**
 * Created by siplo on 11/10/2018.
 */
public interface UrlDaoService {

    Mono<Boolean> saveShortUrl(ShortUrl url);

    Mono<ShortUrl> findShortUrl(String id);

    Mono<ShortUrl> updateUrl(String id, ShortUrl url);

    Flux<ShortUrl> findAll();
}
