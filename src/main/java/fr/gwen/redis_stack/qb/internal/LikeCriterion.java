package fr.gwen.redis_stack.qb.internal;

import com.redis.om.spring.metamodel.indexed.TextField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

record LikeCriterion<E>(TextField<E, String> field, String pattern, LikeMode mode) implements
    SearchCriterion<E, String> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return switch (mode) {
      case STARTS_WITH -> field.startsWith(pattern);
      case ENDS_WITH -> field.endsWith(pattern);
      case LIKE -> field.like(pattern);
    };
  }

  public enum LikeMode {
    STARTS_WITH, ENDS_WITH, LIKE
  }
}