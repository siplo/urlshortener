package sk.siplo.url.shortener.handler;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlService;

/**
 * Created by siplo on 11/10/2018.
 */
@Component public class CreateUrlHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CreateUrlHandler.class);
    public static final String URL_PREFIX = "/url/";

    private UrlService urlService;

    public CreateUrlHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ShortUrl> shortUrl = request.body(toMono(ShortUrl.class)).switchIfEmpty(Mono.empty());
        return urlService.createShortUrl(shortUrl).flatMap(createdUrl -> {
            LOG.info("Created url :", createdUrl);
            return ServerResponse.created(URI.create(URL_PREFIX + createdUrl.getUrlHash()))
                    .contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(createdUrl));
        }).switchIfEmpty(
                ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BodyInserters.fromObject("Empty input")))
                .onErrorResume(IllegalArgumentException.class, t -> {
                    LOG.error("Exception :", t);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(BodyInserters.fromObject(t.getMessage()));

                });
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String urlId = String.valueOf(request.pathVariable("id"));
        Mono<ShortUrl> shortUrl = request.body(toMono(ShortUrl.class)).switchIfEmpty(Mono.empty());
        return urlService.updateUrl(urlId, shortUrl).flatMap(updatedUrl -> {
            LOG.info("updatedUrl url :", updatedUrl);
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromObject(updatedUrl));
        }).switchIfEmpty(ServerResponse.notFound().build()).onErrorResume(IllegalArgumentException.class, e -> {
            LOG.error("Exception :", e);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Mono.just(e.getMessage()), String.class);
        });
    }

}
