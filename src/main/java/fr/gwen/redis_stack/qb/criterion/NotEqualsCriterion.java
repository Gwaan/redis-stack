package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;

public record NotEqualsCriterion<E, T>(MetamodelField<E, T> field, T value) implements
    SearchCriterion<E, T> {

}