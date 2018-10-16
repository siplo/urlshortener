package sk.siplo.url.shortener;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import sk.siplo.url.shortener.model.ShortUrl;

@Configuration
public class RedisConfiguration {

    @Bean
    ReactiveRedisOperations<String, ShortUrl> redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<ShortUrl> serializer = new Jackson2JsonRedisSerializer<>(ShortUrl.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, ShortUrl> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, ShortUrl> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisConnectionFactory lettuceConnectionFactory() {

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder().commandTimeout(Duration.ofSeconds(2))
                        .shutdownTimeout(Duration.ZERO).build();

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379), clientConfig);
    }
}
