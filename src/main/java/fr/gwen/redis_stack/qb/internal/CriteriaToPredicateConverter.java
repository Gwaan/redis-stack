package fr.gwen.redis_stack.qb.internal;


import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;
import fr.gwen.redis_stack.service.AppState;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
class CriteriaToPredicateConverter {

  public static <E extends AppState<?>> SearchFieldPredicate<E, Object> convert(
      SearchCriteria<E> criteria) {

    return (SearchFieldPredicate<E, Object>) switch (criteria) {
      case SimpleCriterion<E> simple -> simple.criterion().toPredicate();
      case GroupCriterion<E> group ->
          group.criteria().stream().map(CriteriaToPredicateConverter::convert).reduce(
              (acc, predicate) -> group.operator() == GroupCriterion.LogicalOperator.AND
                  ? (SearchFieldPredicate<E, Object>) acc.andAny(predicate)
                  : (SearchFieldPredicate<E, Object>) acc.orAny(predicate)).orElseThrow(
              () -> new IllegalStateException("At least one criteria must be specified"));
    };
  }

}