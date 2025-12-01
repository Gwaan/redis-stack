package fr.gwen.redis_stack.indexable;

import fr.gwen.redis_stack.annotations.RedisIndexField;
import fr.gwen.redis_stack.annotations.RedisIndexField.FieldType;
import fr.gwen.redis_stack.annotations.RedisIndexable;
import fr.gwen.redis_stack.annotations.RedisIndexable.DataType;
import java.util.UUID;

@RedisIndexable(keyPrefix = "machin:", dataType = DataType.JSON)
public class Machin {

  @RedisIndexField(type = FieldType.TAG)
  private UUID truc;

}
