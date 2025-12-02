package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.NumericField;

public record BetweenCriterion<E, T extends Comparable<T>>(NumericField<E, T> field, T min,
                                                           T max) implements SearchCriterion<E, T> {

}