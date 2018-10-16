package sk.siplo.url.shortener.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.service.UrlService;

@RestController
@RequestMapping
public class ResolveUrlAnnotationHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResolveUrlAnnotationHandler.class);
    private UrlService urlService;

    public ResolveUrlAnnotationHandler(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/r/{id}")
    public Mono<Void> goToDestination(@PathVariable String id, ServerWebExchange exchange) {
        return urlService.findUrlById(id).flatMap(t -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.SEE_OTHER);
            response.getHeaders().add(HttpHeaders.LOCATION, t.getOriginalUrl());
            return response.setComplete();
        });

    }

}
