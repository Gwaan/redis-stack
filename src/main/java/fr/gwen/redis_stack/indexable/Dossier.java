package fr.gwen.redis_stack.indexable;

import fr.gwen.redis_stack.annotations.RedisIndexField;
import fr.gwen.redis_stack.annotations.RedisIndexable;
import lombok.Data;

@Data
@RedisIndexable(keyPrefix = "dossier:", dataType = RedisIndexable.DataType.JSON)
public class Dossier {

  @RedisIndexField(type = RedisIndexField.FieldType.TAG)
  private String id;

  @RedisIndexField(type = RedisIndexField.FieldType.TEXT, sortable = true)
  private String nom;

  @RedisIndexField(type = RedisIndexField.FieldType.TAG)
  private String status;

  @RedisIndexField(type = RedisIndexField.FieldType.NUMERIC, sortable = true)
  private Double montant;

  private String description;
  private java.time.LocalDateTime createdAt;
}