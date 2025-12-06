package fr.gwen.redis_stack.qb.internal;

import com.redis.om.spring.metamodel.MetamodelField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

record IsMissingCriterion<E, T>(MetamodelField<E, T> field, boolean isMissing) implements
    SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return isMissing ? field.isMissing() : field.isMissing().negate();
  }
}