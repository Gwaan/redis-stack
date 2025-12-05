package fr.gwen.redis_stack.statemanager;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

public class RedisTestBase {

  private static GenericContainer<?> redisContainer;

  @BeforeAll
  static void startRedis() {
    redisContainer = new GenericContainer<>("redis/redis-stack:latest").withExposedPorts(6379);
    redisContainer.start();
  }

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redisContainer::getHost);
    registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
  }


  @AfterAll
  static void stopRedis() {
    if (redisContainer != null) {
      redisContainer.stop();
    }
  }

}
