package sk.siplo.url.shortener.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import sk.siplo.url.shortener.handler.ResolveUrlHandler;
import sk.siplo.url.shortener.handler.ShortUrlHandler;

/**
 * Created by siplo on 11/10/2018.
 */
@Configuration public class UrlShortenerlRoutereConfig {

    private static Logger LOG = LoggerFactory.getLogger(UrlShortenerlRoutereConfig.class);

    @Bean
    public RouterFunction<ServerResponse> route(ShortUrlHandler shortUrlHandler, ResolveUrlHandler resolveUrlHandler)
            throws NoSuchAlgorithmException {

        return RouterFunctions.route(POST("/url").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                shortUrlHandler::create)
                .andRoute(PUT("/url/{id}").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        shortUrlHandler::update)
                .andRoute(GET("/url/{id}").and(accept(APPLICATION_JSON)), shortUrlHandler::resolve)
                .andRoute(GET("/url").and(accept(APPLICATION_JSON)), shortUrlHandler::findAllUrls)
                .andRoute(GET("/resolve/{id}"), resolveUrlHandler::goToDestination);
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> wrongArgument() {
        return (request, next) -> next.handle(request).onErrorResume(IllegalArgumentException.class, t -> {
            LOG.error("Exception :", t);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BodyInserters.fromObject(t.getMessage()));

        });
    }

}
