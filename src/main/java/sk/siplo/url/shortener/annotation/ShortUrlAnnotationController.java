package sk.siplo.url.shortener.annotation;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sk.siplo.url.shortener.model.ShortUrl;
import sk.siplo.url.shortener.service.UrlService;

@RestController
@RequestMapping
public class ShortUrlAnnotationController {

    @Autowired
    UrlService urlService;

    @PostMapping("/url_annotation")
    public Mono<ShortUrl> create(@RequestBody Publisher<ShortUrl> shortUrl) {
        return urlService.createShortUrl(Mono.from(shortUrl));
    }

    @PutMapping("/url_annotation/{id}")
    public Mono<ShortUrl> update(@PathVariable String id, @RequestBody Publisher<ShortUrl> shortUrl) {
        return urlService.updateUrl(id, Mono.from(shortUrl));
    }

    @GetMapping("/url_annotation")
    public Flux<ShortUrl> findAllUrls() {
        return urlService.findAllUrls();

    }

    @GetMapping("/url_annotation/{id}")
    public Mono<ShortUrl> resolve(@PathVariable String id) {
        return urlService.findUrlById(id);
    }
}
