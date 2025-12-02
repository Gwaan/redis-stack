package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;
import java.util.List;

public record NotInCriterion<E, T>(MetamodelField<E, T> field, List<T> values) implements
    SearchCriterion<E, T> {

}