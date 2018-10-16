package sk.siplo.url.shortener;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * Created by siplo on 11/10/2018.
 */
@EnableScheduling
@EnableWebFlux
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Application.class, args);
    }

}
