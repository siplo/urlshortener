package sk.siplo.url.shortener.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
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
        return urlService.findUrlById(urlId).flatMap(
                foundUrl -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromObject(foundUrl))).switchIfEmpty(
                ServerResponse.status(HttpStatus.NOT_FOUND)
                        .body(BodyInserters.fromObject(String.format("Url for id: %s not found", urlId))))
                .onErrorResume(Exception.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Mono.just(e.getMessage()), String.class));
    }

    public Mono<ServerResponse> findAllUrls(ServerRequest request) {
        Flux<ShortUrl> retVal = urlService.findAllUrls();
        return retVal.collectList().flatMap(list -> {
            list.forEach(t -> LOG.debug("url from storage: {}", t));
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(list))
                    .onErrorResume(Exception.class, (e) -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Mono.just(e.getMessage()), String.class));
        });

    }

    public Mono<ServerResponse> goToDestination(ServerRequest request) {
        String urlId = String.valueOf(request.pathVariable("id"));
        LOG.debug("URL id: {}", urlId);
        return urlService.findUrlById(urlId).flatMap(foundUrl -> {
            if (foundUrl.isValid()) {
                return ServerResponse.status(HttpStatus.TEMPORARY_REDIRECT)
                        .header(HttpHeaders.LOCATION, foundUrl.getOriginalUrl()).contentType(MediaType.TEXT_PLAIN)
                        .build();
            } else {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(String
                        .format("Url %s is not valid anymore. Destination %s", foundUrl.getCreatedUrl(),
                                foundUrl.getOriginalUrl())), String.class);
            }
        }).switchIfEmpty(ServerResponse.notFound().build()).onErrorResume(Exception.class,
                e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Mono.just(e.getMessage()), String.class));
    }

}
