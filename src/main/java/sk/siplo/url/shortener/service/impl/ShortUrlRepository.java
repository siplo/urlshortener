package sk.siplo.url.shortener.service.impl;

import org.springframework.data.repository.CrudRepository;
import sk.siplo.url.shortener.model.ShortUrl;

/**
 * Created by siplo on 13/10/2018.
 */
public interface ShortUrlRepository extends CrudRepository<ShortUrl, String> {

    ShortUrl findByurlHash(String urlHash);

}
