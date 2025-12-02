package fr.gwen.redis_stack.qb.criteria;

import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;
import fr.gwen.redis_stack.qb.criterion.SearchCriterion;
import fr.gwen.redis_stack.service.AppState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record SearchCriteria<E extends AppState<?>>(List<SearchCriterion<E, ?>> criteria,
                                                    LogicalOperator operator) {

  public enum LogicalOperator {
    AND, OR
  }

  public SearchCriteria {
    criteria = criteria == null ? new ArrayList<>() : new ArrayList<>(criteria);
  }

  public static <E extends AppState<?>> SearchCriteria<E> empty() {
    return new SearchCriteria<>(Collections.emptyList(), LogicalOperator.AND);
  }

  public static <E extends AppState<?>> SearchCriteria<E> and() {
    return new SearchCriteria<>(new ArrayList<>(), LogicalOperator.AND);
  }

  public static <E extends AppState<?>> SearchCriteria<E> or() {
    return new SearchCriteria<>(new ArrayList<>(), LogicalOperator.OR);
  }

  public SearchFieldPredicate<E, ?> toSearchPredicate() {
    return CriteriaToPredicateConverter.convert(this);
  }
}