package fr.gwen.redis_stack.repo;

import com.redis.om.spring.annotations.Document;
import com.redis.om.spring.annotations.Indexed;
import com.redis.om.spring.annotations.TextIndexed;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Document(value = "dossier", indexName = "idx:dossier")
public class Dossier {

  @Id
  @Indexed
  private String id;

  @TextIndexed
  private String nom;

  @Indexed
  private String status;

  @Indexed
  private Double montant;

  private String description;
  private LocalDateTime createdAt;
}