package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.NumericField;

public record LessThanCriterion<E, T extends Comparable<T>>(NumericField<E, T> field, T value,
                                                            boolean orEqual) implements
    SearchCriterion<E, T> {

}