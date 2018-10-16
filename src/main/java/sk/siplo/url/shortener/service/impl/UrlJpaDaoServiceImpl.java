package sk.siplo.url.shortener.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlDaoService;

/**
 * Created by siplo on 13/10/2018.
 */

@Service
public class UrlJpaDaoServiceImpl implements UrlDaoService {

    @Autowired
    ShortUrlRepository repository;

    @Override
    public Mono<Boolean> saveShortUrl(ShortUrl url) {
        Mono<Boolean> retVal = null;
        ShortUrl exists = repository.findByurlHash(url.getUrlHash());
        if (exists != null) {
            retVal = Mono.just(Boolean.FALSE);
        } else {
            ShortUrl result = repository.save(url);
            if (result != null) {
                retVal = Mono.just(Boolean.TRUE);
            }
        }
        return retVal;
    }

    @Override
    public Mono<ShortUrl> findShortUrl(String id) {
        return Mono.just(repository.findByurlHash(id));
    }

    @Override
    public Mono<ShortUrl> updateUrl(String id, ShortUrl url) {
        if (repository.existsById(id)) {
            return Mono.just(repository.save(url));
        }
        return Mono.error(new RuntimeException("Not foud"));
    }

    @Override
    public Flux<ShortUrl> findAll() {
        return Flux.fromIterable(repository.findAll());
    }

}
