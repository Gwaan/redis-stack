package fr.gwen.redis_stack.qb.criteria;

import fr.gwen.redis_stack.qb.criterion.SearchCriterion;
import fr.gwen.redis_stack.service.AppState;

public record SimpleCriterion<E extends AppState<?>>(SearchCriterion<E, ?> criterion, Class<E> entityClass) implements
    SearchCriteria<E> {

  @Override
  public Class<E> getEntityClass() {
    return entityClass;
  }
}