package fr.gwen.redis_stack.qb.internal;

import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

 sealed interface SearchCriterion<E, T> permits EqualsCriterion, NotEqualsCriterion,
    InCriterion, NotInCriterion, BetweenCriterion, LikeCriterion, ContainingCriterion,
    NotContainingCriterion, GreaterThanCriterion, LessThanCriterion, IsMissingCriterion {

  SearchFieldPredicate<E, ?> toPredicate();
}