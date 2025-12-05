package fr.gwen.redis_stack.qb.criterion;

import com.redis.om.spring.metamodel.MetamodelField;
import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.metamodel.indexed.TagField;
import com.redis.om.spring.metamodel.indexed.TextField;
import com.redis.om.spring.search.stream.predicates.SearchFieldPredicate;

public record EqualsCriterion<E, T>(MetamodelField<E, T> field, T value) implements
    SearchCriterion<E, T> {

  @Override
  public SearchFieldPredicate<E, ?> toPredicate() {
    return switch (field) {
      case TextField<E, ?> textField -> ((TextField<E, Object>) textField).eq(value);
      case TagField<E, ?> tagField -> ((TagField<E, Object>) tagField).eq(value);
      case NumericField<E, ?> numericField ->
          ((NumericField<E, Comparable>) numericField).eq((Comparable) value);
      default -> throw new IllegalArgumentException(
          "Unsupported field type for equals: " + field.getClass());
    };
  }
}