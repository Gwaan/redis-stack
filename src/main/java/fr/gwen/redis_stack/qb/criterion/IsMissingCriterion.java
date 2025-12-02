package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;

public record IsMissingCriterion<E, T>(MetamodelField<E, T> field, boolean isMissing) implements
    SearchCriterion<E, T> {

}