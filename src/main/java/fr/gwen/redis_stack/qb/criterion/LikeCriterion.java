package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.TextField;

public record LikeCriterion<E>(TextField<E, String> field, String pattern, LikeMode mode) implements
    SearchCriterion<E, String> {

  public enum LikeMode {
    STARTS_WITH, ENDS_WITH, LIKE
  }
}