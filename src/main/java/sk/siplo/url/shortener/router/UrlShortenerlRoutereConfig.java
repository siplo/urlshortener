package sk.siplo.url.shortener.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
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
import sk.siplo.url.shortener.handler.CreateUrlHandler;
import sk.siplo.url.shortener.handler.ResolveUrlHandler;

/**
 * Created by siplo on 11/10/2018.
 */
@Configuration public class UrlShortenerlRoutereConfig {

    private static Logger LOG = LoggerFactory.getLogger(UrlShortenerlRoutereConfig.class);

    @Bean
    public RouterFunction<ServerResponse> route(CreateUrlHandler createUrlHandler, ResolveUrlHandler resolveUrlHandler)
            throws NoSuchAlgorithmException {

        return RouterFunctions.route(POST("/url").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                createUrlHandler::create)
                .andRoute(PUT("/url/{id}").and(accept(APPLICATION_JSON)).and(contentType(APPLICATION_JSON)),
                        createUrlHandler::update)
                .andRoute(GET("/resolve/{id}").and(accept(APPLICATION_JSON)), resolveUrlHandler::resolve)
                .andRoute(GET("/resolve").and(accept(APPLICATION_JSON)), resolveUrlHandler::findAllUrls)
                .andRoute(GET("/resolve/{id}").and(accept(TEXT_PLAIN)), resolveUrlHandler::goToDestination)
                .filter(wrongArgument());
    }

    private HandlerFilterFunction<ServerResponse, ServerResponse> wrongArgument() {
        return (request, next) -> next.handle(request).onErrorResume(IllegalArgumentException.class, t -> {
            LOG.error("Exception :", t);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BodyInserters.fromObject(t.getMessage()));

        });
    }
}
