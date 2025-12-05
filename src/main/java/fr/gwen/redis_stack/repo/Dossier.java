package fr.gwen.redis_stack.repo;

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
@Document(value = "dossier", indexName = "idx:dossier")
public class Dossier implements AppState<String> {

  @Id
  @Indexed
  private String id;

  @TextIndexed
  private String nom;

  @Searchable
  private String status;

  @Indexed
  private Double montant;

  private String description;
  private LocalDateTime createdAt;
}