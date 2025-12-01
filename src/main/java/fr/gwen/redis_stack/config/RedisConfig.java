package fr.gwen.redis_stack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.lettucemod.RedisModulesClient;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

 /* @Bean
  public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
    StatefulRedisConnection<String, String> connection = redisClient.connect(StringCodec.UTF8);
    return connection;
  }

  @Bean
  public RedisClient redisClient() {
    RedisURI.Builder uriBuilder = RedisURI.builder().withHost("localhost").withPort(6379)
        .withTimeout(Duration.ofMillis(50000));

    RedisURI redisUri = uriBuilder.build();
    RedisClient client = RedisClient.create(redisUri);

    return client;
  }*/

  @Bean
  public StatefulRedisModulesConnection<String, String> redisModulesConnection(
      RedisModulesClient client) {
    StatefulRedisModulesConnection<String, String> connection = client.connect();
    return connection;
  }

  @Bean
  public RedisModulesClient redisModulesClient() {
    RedisURI.Builder uriBuilder = RedisURI.builder()
        .withHost("localhost")
        .withPort(6379)
        .withTimeout(Duration.ofMillis(50000));


    RedisURI redisUri = uriBuilder.build();
    RedisModulesClient client = RedisModulesClient.create(redisUri);

    return client;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();

    // Support pour LocalDateTime, LocalDate, etc.
    mapper.registerModule(new JavaTimeModule());

    // Ne pas écrire les dates comme timestamps
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // Ignorer les propriétés inconnues lors de la désérialisation
    mapper.configure(
        com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        false
    );

    return mapper;
  }


}
