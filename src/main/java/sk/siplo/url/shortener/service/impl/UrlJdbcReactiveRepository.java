package sk.siplo.url.shortener.service.impl;

import io.reactivex.Flowable;
import javax.annotation.Resource;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.ResultSetMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlDaoService;

/**
 * Created by siplo on 14/10/2018.
 */
@Service
public class UrlJdbcReactiveRepository implements UrlDaoService {

    @Resource
    private Database db;

    @Override
    public Mono<Boolean> saveShortUrl(ShortUrl url) {
        String createSql =
                "INSERT INTO short_url (id,unique_id,original_url,url,is_valid,created_at) VALUES (?, ?, ?, ?, ?, ?)";
        Flowable<Integer> a = db.update(createSql)
                .parameters(System.currentTimeMillis(), url.getUrlHash(), url.getOriginalUrl(), url.getCreatedUrl(),
                        url.isValid(), new java.sql.Timestamp(url.getCreatedAt().getTime())).counts();

        Flowable<Boolean> x = a.map(v -> {
            if (v > 0) {
                return true;
            }
            return false;
        });

        return Mono.from(x);
    }

    @Override
    public Mono<ShortUrl> findShortUrl(String uniqueId) {
        String sql = "SELECT * FROM short_url " + "WHERE unique_id = ? ";

        Flowable<ShortUrl> shortUrlFlowable = db.select(sql).parameters(uniqueId).get(extractFromResultSet());

        return Mono.from(shortUrlFlowable);
    }

    private ResultSetMapper<ShortUrl> extractFromResultSet() {
        return rs -> {
            ShortUrl url = new ShortUrl();
            url.setId(rs.getLong("id"));
            url.setCreatedUrl(rs.getString("url"));
            url.setUrlHash(rs.getString("unique_id"));
            url.setOriginalUrl(rs.getString("original_url"));
            url.setValid(rs.getBoolean("is_valid"));
            url.setCreatedAt(rs.getTimestamp("created_at"));

            return url;
        };
    }

    @Override
    public Mono<ShortUrl> updateUrl(String id, ShortUrl url) {
        String createSql =
                "UPDATE short_url set unique_id=?,original_url=?,url=?,is_valid=?,created_at=? where unique_id=?";
        Flowable<Integer> a = db.update(createSql)
                .parameters(url.getUrlHash(), url.getOriginalUrl(), url.getCreatedUrl(), url.isValid(),
                        url.getCreatedAt(), url.getUrlHash()).counts();

        Flowable<ShortUrl> x = a.map(v -> {
            if (v > 0) {
                return url;
            }
            return null;
        });

        return Mono.from(x);
    }

    @Override
    public Flux<ShortUrl> findAll() {
        String sql = "SELECT * FROM short_url";

        Flowable<ShortUrl> urlFlowable = db.select(sql).get(extractFromResultSet());
        return Flux.from(urlFlowable);
    }
}
