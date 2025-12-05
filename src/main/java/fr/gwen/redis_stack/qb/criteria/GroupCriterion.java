package fr.gwen.redis_stack.qb.criteria;

import fr.gwen.redis_stack.service.AppState;
import java.util.List;

public record GroupCriterion<E extends AppState<?>>(List<SearchCriteria<E>> criteria,
                                                    LogicalOperator operator) implements
    SearchCriteria<E> {

  public enum LogicalOperator {
    AND, OR
  }
}