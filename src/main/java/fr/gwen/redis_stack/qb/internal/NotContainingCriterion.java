package fr.gwen.redis_stack.qb.internal;

import com.redis.om.spring.metamodel.indexed.TextField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

record NotContainingCriterion<E>(TextField<E, String> field, String value) implements
    SearchCriterion<E, String> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return field.notContaining(value);
  }
}