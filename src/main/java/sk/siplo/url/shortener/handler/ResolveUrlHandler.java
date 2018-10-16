package sk.siplo.url.shortener.handler;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlService;

@Component public class ResolveUrlHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResolveUrlHandler.class);
    private UrlService urlService;

    public ResolveUrlHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    public Mono<ServerResponse> resolve(ServerRequest request) {

        String urlId = String.valueOf(request.pathVariable("id"));
        LOG.debug("URL id: {}", urlId);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(new ShortUrl()));
    }

    public Mono<ServerResponse> getAllUrls(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(Arrays.asList(new ShortUrl(), new ShortUrl(), new ShortUrl())))
                .onErrorResume(Exception.class, (e) -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Mono.just(e.getMessage()), String.class));

    }

    public Mono<ServerResponse> goToDestination(ServerRequest request) {
        String urlId = String.valueOf(request.pathVariable("id"));
        LOG.debug("URL id: {}", urlId);
        return ServerResponse.status(HttpStatus.TEMPORARY_REDIRECT)
                .header(HttpHeaders.LOCATION, "https://stackoverflow.com/").contentType(MediaType.TEXT_PLAIN).build();
    }

}
