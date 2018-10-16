package sk.siplo.url.shortener.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlDaoService;
import sk.siplo.url.shortener.service.UrlService;

/**
 * Created by siplo on 11/10/2018.
 */
@Service("urlService")
public class UrlServiceImpl implements UrlService {

    private static final Logger LOG = LoggerFactory.getLogger(UrlServiceImpl.class);
    private static final String MD_5 = "MD5";

    @Value("${short.url.prefix}")
    private String SHORT_URL_PREFIX;

    @Value("${short.url.remove.url.ttl}")
    private Integer timeToLive;

    @Resource
    private UrlDaoService urlDaoService;

    public Mono<ShortUrl> createShortUrl(Mono<ShortUrl> input) {
        Mono<ShortUrl> createdUrl =
                input.map(url -> createShortUrlObj(url.getOriginalUrl())).switchIfEmpty(Mono.empty())
                        .flatMap(url -> urlDaoService.saveShortUrl(url).flatMap(result -> {
                            if (result) {
                                return Mono.justOrEmpty(url);
                            } else {
                                throw new RuntimeException("Can not insert url");
                            }
                        }).onErrorMap(SQLException.class,
                                e -> new IllegalArgumentException("Could not insert into DB")));

        return createdUrl;
    }

    @Override
    public Mono<ShortUrl> findUrlById(String id) {
        Mono<ShortUrl> record = urlDaoService.findShortUrl(id);
        return record.filter(result -> isOlderThen24hours(result));

    }

    private boolean isOlderThen24hours(ShortUrl result) {
        java.util.Date a = new Date(result.getCreatedAt().getTime());
        return !(Duration.between(a.toInstant(), Instant.now()).compareTo(Duration.ofSeconds(timeToLive)) > 0);
    }

    @Override
    public Flux<ShortUrl> findAllUrls() {
        Flux<ShortUrl> retVal = urlDaoService.findAll();
        return retVal;
    }

    @Override
    public Mono<ShortUrl> updateUrl(String id, Mono<ShortUrl> input) {
        Mono<ShortUrl> oldUrl = findUrlById(id);
        if (oldUrl == null) {
            return Mono.empty();
        }
        return oldUrl
                .flatMap(old -> input.flatMap(u -> urlDaoService.updateUrl(id, createShortUrlObj(u.getOriginalUrl()))))
                .switchIfEmpty(
                        Mono.defer(() -> Mono.error(new IllegalArgumentException("Not found any record for :" + id))));
    }

    private ShortUrl createShortUrlObj(String urlFromWeb) {
        if (StringUtils.isEmpty(urlFromWeb)) {
            throw new IllegalArgumentException("urlFromWeb is empty");
        }
        ShortUrl retVal = new ShortUrl();
        retVal.setCreatedAt(new Date());
        String shortUrlId = createUniqueUrl(urlFromWeb);
        String result = SHORT_URL_PREFIX + shortUrlId;
        retVal.setCreatedUrl(result);
        retVal.setOriginalUrl(urlFromWeb);
        LOG.debug("Result url :" + result);
        retVal.setUrlHash(shortUrlId);
        retVal.setValid(true);
        return retVal;
    }

    private String createUniqueUrl(String urlFromWeb) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(MD_5);
        } catch (NoSuchAlgorithmException e) {
            LOG.warn("Could not obtain MD5 message digest", e);
            throw new RuntimeException("Could not create unique url for : " + urlFromWeb);
        }
        digest.update(urlFromWeb.getBytes());
        return DatatypeConverter.printHexBinary(digest.digest());
    }

    @Override
    public Mono<Boolean> deleteOldUrls() {
        return urlDaoService.findAll().filter(t -> (t.isValid() && !isOlderThen24hours(
                t)))//in normal case there will be select with where condition valid=true and NOW - creaetdDate <24h
                .flatMap(t -> {
                    t.setValid(false);
                    LOG.info("remove url: {}", t);
                    return urlDaoService.updateUrl(t.getUrlHash(), t);
                }).map(x -> x.isValid()).collectList().map(t -> t.stream().allMatch(x -> !x.booleanValue()));

    }

}
