package fr.gwen.redis_stack.qb.criteria;

import fr.gwen.redis_stack.service.AppState;
import java.util.List;

public record GroupCriterion<E extends AppState<?>>(List<SearchCriteria<E>> criteria,
                                                    LogicalOperator operator,
                                                    Class<E> entityClass) implements
    SearchCriteria<E> {

  @Override
  public Class<E> getEntityClass() {
    return entityClass;
  }

  public enum LogicalOperator {
    AND, OR
  }
}