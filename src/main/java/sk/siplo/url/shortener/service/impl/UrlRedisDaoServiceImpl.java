package sk.siplo.url.shortener.service.impl;

import java.time.Duration;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlDaoService;

/**
 * Created by siplo on 11/10/2018.
 */
@Service
public class UrlRedisDaoServiceImpl implements UrlDaoService {

    @Resource(name = "redisOperations")
    ReactiveRedisOperations<String, ShortUrl> redisOperations;

    @Value("${short.url.remove.url.ttl}")
    private Integer timeToLive;

    private static final Logger LOG = LoggerFactory.getLogger(UrlRedisDaoServiceImpl.class);

    @Override
    public Mono<Boolean> saveShortUrl(ShortUrl url) {
        Mono<Boolean> retVal = redisOperations.opsForValue().set(url.getUrlHash(), url, Duration.ofSeconds(timeToLive));
        retVal.subscribe(result -> LOG.info("Save url  {} to DB: {}", result, url));
        return retVal;
    }

    @Override
    public Mono<ShortUrl> findShortUrl(String id) {
        return redisOperations.keys(id).flatMap(redisOperations.opsForValue()::get).single();

    }

    @Override
    public Mono<ShortUrl> updateUrl(String id, ShortUrl url) {
        Mono<Boolean> result = redisOperations.opsForValue().set(id, url);
        return result.flatMap(value -> {
            if (Boolean.FALSE.equals(value)) {
                return Mono.empty();
            }
            return Mono.just(url);
        });
    }

    @Override
    public Flux<ShortUrl> findAll() {
        return redisOperations.keys("*").flatMap(redisOperations.opsForValue()::get);
    }

}
