package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

public record LessThanCriterion<E, T extends Comparable<T>>(NumericField<E, T> field, T value,
                                                            boolean orEqual) implements
    SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return orEqual ? field.le(value) : field.lt(value);
  }
}