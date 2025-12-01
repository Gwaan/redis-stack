package fr.gwen.redis_stack.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisIndexField {

  String name() default "";

  FieldType type();

  boolean sortable() default false;

  String jsonPath() default "";

  enum FieldType {
    TEXT, TAG, NUMERIC
  }
}
