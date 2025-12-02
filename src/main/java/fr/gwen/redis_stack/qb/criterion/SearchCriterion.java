package fr.gwen.redis_stack.qb.criterion;

public sealed interface SearchCriterion<E, T>
    permits EqualsCriterion, NotEqualsCriterion, InCriterion, NotInCriterion,
    BetweenCriterion, LikeCriterion, ContainingCriterion, NotContainingCriterion,
    GreaterThanCriterion, LessThanCriterion, IsMissingCriterion {
}