package sk.siplo.url.shortener.handler;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;

import java.net.URI;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private UrlService urlService;

    public CreateUrlHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        Mono<ShortUrl> inputFromClient = request.body(toMono(ShortUrl.class)).switchIfEmpty(Mono.empty());
        return ServerResponse.created(URI.create("/url/" + "will_be_unique_id")).contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters
                        .fromObject(new ShortUrl(new Date(), "uniquehash", true, "http://short.sk/url/uniquehash")));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String urlId = String.valueOf(request.pathVariable("id"));
        Mono<ShortUrl> shortUrl = request.body(toMono(ShortUrl.class)).switchIfEmpty(Mono.empty());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(
                new ShortUrl(new Date(), "uniquehash", true, String.format("urlId: %s was updated", urlId))));
    }

}
