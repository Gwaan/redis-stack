package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

public record BetweenCriterion<E, T extends Comparable<T>>(NumericField<E, T> field, T min,
                                                           T max) implements SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return field.between(min, max);
  }
}