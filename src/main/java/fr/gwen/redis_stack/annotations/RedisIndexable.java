package fr.gwen.redis_stack.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisIndexable {

  String indexName() default "";

  String keyPrefix();

  DataType dataType() default DataType.JSON;

  boolean autoCreate() default true;

  enum DataType {
    HASH, JSON
  }

}
