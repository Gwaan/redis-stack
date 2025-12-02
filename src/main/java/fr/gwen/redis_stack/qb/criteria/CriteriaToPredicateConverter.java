package fr.gwen.redis_stack.qb.criteria;

import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.metamodel.indexed.TagField;
import com.redis.om.spring.metamodel.indexed.TextField;
import com.redis.om.spring.search.stream.predicates.AndPredicate;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;
import fr.gwen.redis_stack.qb.criterion.BetweenCriterion;
import fr.gwen.redis_stack.qb.criterion.ContainingCriterion;
import fr.gwen.redis_stack.qb.criterion.EqualsCriterion;
import fr.gwen.redis_stack.qb.criterion.GreaterThanCriterion;
import fr.gwen.redis_stack.qb.criterion.InCriterion;
import fr.gwen.redis_stack.qb.criterion.IsMissingCriterion;
import fr.gwen.redis_stack.qb.criterion.LessThanCriterion;
import fr.gwen.redis_stack.qb.criterion.LikeCriterion;
import fr.gwen.redis_stack.qb.criterion.NotContainingCriterion;
import fr.gwen.redis_stack.qb.criterion.NotEqualsCriterion;
import fr.gwen.redis_stack.qb.criterion.NotInCriterion;
import fr.gwen.redis_stack.qb.criterion.SearchCriterion;
import fr.gwen.redis_stack.service.AppState;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class CriteriaToPredicateConverter {

  public static <E extends AppState<?>> SearchFieldPredicate<E, ?> convert(
      SearchCriteria<E> criteria) {

    BinaryOperator<SearchFieldPredicate<E, ?>> combiner =
        criteria.operator() == SearchCriteria.LogicalOperator.AND
            ? (acc, predicate) -> (SearchFieldPredicate<E, ?>) acc.andAny(predicate)
            : (acc, predicate) -> (SearchFieldPredicate<E, ?>) acc.orAny(predicate);

    return criteria.criteria().stream().map(CriteriaToPredicateConverter::convertSingleCriterion)
        .<SearchFieldPredicate<E, ?>>map(
            Function.identity()) // Bricolage pour forcer la capture de la wildcard
        .reduce(combiner).orElseThrow(() -> new IllegalStateException(
            "At least one criteria must be specified")); // peut être un peu violent
  }

  private static <E extends AppState<?>> SearchFieldPredicate<E, ?> convertSingleCriterion(
      SearchCriterion<E, ?> criterion) {

    return switch (criterion) {
      case EqualsCriterion<E, ?> eq -> switch (eq.field()) {
        case TextField<E, ?> textField -> ((TextField<E, Object>) textField).eq(eq.value());
        case TagField<E, ?> tagField -> ((TagField<E, Object>) tagField).eq(eq.value());
        case NumericField<E, ?> numericField ->
            ((NumericField<E, Comparable>) numericField).eq((Comparable) eq.value());
        // ce serait bien qsu'on ne compile pas su jamais on essaie de positionner un contains()
        // sur un index de type TAG (dc non searchable), là on le sait qu'au runtime, pas bon
        // a voir si c'est faisable
        default -> throw new IllegalArgumentException("Unsupported field type for equals");
      };

      case NotEqualsCriterion<E, ?> notEq -> switch (notEq.field()) {
        case TextField<E, ?> textField -> ((TextField<E, Object>) textField).notEq(notEq.value());
        case TagField<E, ?> tagField -> ((TagField<E, Object>) tagField).notEq(notEq.value());
        case NumericField<E, ?> numericField ->
            ((NumericField<E, Comparable>) numericField).notEq((Comparable) notEq.value());
        default -> throw new IllegalArgumentException("Unsupported field type for notEquals");
      };

      case InCriterion<E, ?> in -> switch (in.field()) {
        case TextField<E, ?> textField ->
            ((TextField<E, Object>) textField).in(in.values().toArray());
        case TagField<E, ?> tagField -> tagField.in(in.values().toArray());
        case NumericField<E, ?> numericField ->
            ((NumericField<E, Comparable>) numericField).in(in.values().toArray(new Comparable[0]));
        default -> throw new IllegalArgumentException("Unsupported field type for in");
      };

      case NotInCriterion<E, ?> notIn -> (switch (notIn.field()) {
        case TextField<E, ?> textField ->
            ((TextField<E, Object>) textField).in(notIn.values().toArray());
        case TagField<E, ?> tagField -> tagField.in(notIn.values().toArray());
        case NumericField<E, ?> numericField -> ((NumericField<E, Comparable>) numericField).in(
            notIn.values().toArray(new Comparable[0]));
        default -> throw new IllegalArgumentException("Unsupported field type for notIn");
      }).negate();

      case BetweenCriterion between -> between.field().between(between.min(), between.max());

      case ContainingCriterion containing -> containing.field().containing(containing.value());

      case NotContainingCriterion notContaining ->
          notContaining.field().notContaining(notContaining.value());

      case LikeCriterion like -> switch (like.mode()) {
        case STARTS_WITH -> like.field().startsWith(like.pattern());
        case ENDS_WITH -> like.field().endsWith(like.pattern());
        case LIKE -> like.field().like(like.pattern());
      };

      case GreaterThanCriterion gt ->
          gt.orEqual() ? gt.field().ge(gt.value()) : gt.field().gt(gt.value());

      case LessThanCriterion lt ->
          lt.orEqual() ? lt.field().le(lt.value()) : lt.field().lt(lt.value());

      case IsMissingCriterion isMissing -> isMissing.isMissing() ? isMissing.field().isMissing()
          : (SearchFieldPredicate<E, ?>) isMissing.field().isMissing().negate();
    };
  }
}