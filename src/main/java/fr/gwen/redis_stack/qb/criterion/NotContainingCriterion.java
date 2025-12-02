package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.indexed.TextField;

public record NotContainingCriterion<E>(TextField<E, String> field, String value) implements
    SearchCriterion<E, String> {

}