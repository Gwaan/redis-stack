package fr.gwen.redis_stack.qb.internal;

import fr.gwen.redis_stack.service.AppState;

 record SimpleCriterion<E extends AppState<?>>(SearchCriterion<E, ?> criterion, Class<E> entityClass) implements
    SearchCriteria<E> {

  @Override
  public Class<E> getEntityClass() {
    return entityClass;
  }
}