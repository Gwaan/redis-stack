package fr.gwen.redis_stack.qb.internal;

import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

record GreaterThanCriterion<E, T extends Comparable<T>>(NumericField<E, T> field, T value,
                                                               boolean orEqual) implements
    SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return orEqual ? field.ge(value) : field.gt(value);
  }
}