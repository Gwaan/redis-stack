package fr.gwen.redis_stack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;

@SpringBootApplication(
    scanBasePackages = "fr.gwen.redis_stack",
    exclude = DataRedisRepositoriesAutoConfiguration.class // <-- important
)
@EnableRedisDocumentRepositories(basePackages = "fr.gwen.redis_stack.repo")
public class RedisStackApplication {
  public static void main(String[] args) {
    SpringApplication.run(RedisStackApplication.class, args);
  }
}