package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;
import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.metamodel.indexed.TagField;
import com.redis.om.spring.metamodel.indexed.TextField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;
import java.util.List;

public record NotInCriterion<E, T>(MetamodelField<E, T> field, List<T> values) implements
    SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    var inPredicate = switch (field) {
      case TextField<E, ?> textField -> ((TextField<E, Object>) textField).in(values.toArray());
      case TagField<E, ?> tagField -> tagField.in(values.toArray());
      case NumericField<E, ?> numericField ->
          ((NumericField<E, Comparable>) numericField).in(values.toArray(new Comparable[0]));
      default -> throw new IllegalArgumentException("Unsupported field type for notIn");
    };
    return inPredicate.negate();
  }
}