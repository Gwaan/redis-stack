package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;
import java.util.Collection;
import java.util.List;

public record InCriterion<E, T>(MetamodelField<E, T> field, Collection<T> values) implements
    SearchCriterion<E, T> {

}