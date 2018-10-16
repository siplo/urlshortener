package sk.siplo.url.shortener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.NonBlockingConnectionPool;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by siplo on 14/10/2018.
 */
@Configuration
public class ReactiveJdbcConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveJdbcConfiguration.class);

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Bean
    public Database databese() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl, user, password);
        } catch (SQLException e) {
            LOG.error("Could not create database connection");
        }
        NonBlockingConnectionPool pool = Pools.nonBlocking().maxPoolSize(Runtime.getRuntime().availableProcessors() * 5)
                .connectionProvider(ConnectionProvider.from(connection)).build();

        return Database.from(pool);
    }

}
