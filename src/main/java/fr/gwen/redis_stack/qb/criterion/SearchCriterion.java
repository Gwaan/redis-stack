package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

public sealed interface SearchCriterion<E, T> permits EqualsCriterion, NotEqualsCriterion,
    InCriterion, NotInCriterion, BetweenCriterion, LikeCriterion, ContainingCriterion,
    NotContainingCriterion, GreaterThanCriterion, LessThanCriterion, IsMissingCriterion {

  SearchFieldPredicate<E, ?> toPredicate();
}