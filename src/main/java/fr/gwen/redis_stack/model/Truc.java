package fr.gwen.redis_stack.model;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.Searchable;
import com.redis.om.spring.annotations.TextIndexed;
import fr.gwen.redis_stack.service.AppState;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
@Document(value = "truc", indexName = "idx:truc")
public class Truc implements AppState<Long> {

  @Id
  @Indexed
  private Long id;

  @TextIndexed
  private String nom;

}