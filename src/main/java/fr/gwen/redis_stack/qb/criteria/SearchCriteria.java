package fr.gwen.redis_stack.qb.criteria;

import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;
import fr.gwen.redis_stack.service.AppState;

public sealed interface SearchCriteria<E extends AppState<?>> permits SimpleCriterion,
    GroupCriterion {

  Class<E> getEntityClass();

  default SearchFieldPredicate<E, Object> toSearchPredicate() {
    return CriteriaToPredicateConverter.convert(this);
  }

}