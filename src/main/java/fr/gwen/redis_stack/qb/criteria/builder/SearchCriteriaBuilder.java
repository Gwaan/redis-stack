package fr.gwen.redis_stack.qb.criteria.builder;

import com.redis.om.spring.metamodel.MetamodelField;
import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.metamodel.indexed.TextField;
import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchCriteriaBuilder<E extends AppState<?>> {

  private final List<SearchCriterion<E, ?>> criteria = new ArrayList<>();
  private SearchCriteria.LogicalOperator operator = SearchCriteria.LogicalOperator.AND;

  private SearchCriteriaBuilder() {
  }

  public static <E extends AppState<?>> SearchCriteriaBuilder<E> builder() {
    return new SearchCriteriaBuilder<>();
  }

  public SearchCriteriaBuilder<E> and() {
    this.operator = SearchCriteria.LogicalOperator.AND;
    return this;
  }

  public SearchCriteriaBuilder<E> or() {
    this.operator = SearchCriteria.LogicalOperator.OR;
    return this;
  }

  public <T> SearchCriteriaBuilder<E> equals(MetamodelField<E, T> field, T value) {
    criteria.add(new EqualsCriterion<>(field, value));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> notEquals(MetamodelField<E, T> field, T value) {
    criteria.add(new NotEqualsCriterion<>(field, value));
    return this;
  }

  @SafeVarargs
  public final <T> SearchCriteriaBuilder<E> in(MetamodelField<E, T> field, T... values) {
    criteria.add(new InCriterion<>(field, Arrays.asList(values)));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> in(MetamodelField<E, T> field, List<T> values) {
    criteria.add(new InCriterion<>(field, values));
    return this;
  }

  @SafeVarargs
  public final <T> SearchCriteriaBuilder<E> notIn(MetamodelField<E, T> field, T... values) {
    criteria.add(new NotInCriterion<>(field, Arrays.asList(values)));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> notIn(MetamodelField<E, T> field, List<T> values) {
    criteria.add(new NotInCriterion<>(field, values));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> between(NumericField<E, T> field, T min,
      T max) {
    criteria.add(new BetweenCriterion<>(field, min, max));
    return this;
  }

  public SearchCriteriaBuilder<E> contains(TextField<E, String> field, String pattern) {
    criteria.add(new ContainingCriterion<>(field, pattern));
    return this;
  }

  public SearchCriteriaBuilder<E> notContains(TextField<E, String> field, String pattern) {
    criteria.add(new NotContainingCriterion<>(field, pattern));
    return this;
  }

  public SearchCriteriaBuilder<E> startsWith(TextField<E, String> field, String pattern) {
    criteria.add(new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.STARTS_WITH));
    return this;
  }

  public SearchCriteriaBuilder<E> endsWith(TextField<E, String> field, String pattern) {
    criteria.add(new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.ENDS_WITH));
    return this;
  }

  public SearchCriteriaBuilder<E> like(TextField<E, String> field, String pattern) {
    criteria.add(new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.LIKE));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> greaterThan(NumericField<E, T> field,
      T value) {
    criteria.add(new GreaterThanCriterion<>(field, value, false));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> greaterThanOrEqual(
      NumericField<E, T> field, T value) {
    criteria.add(new GreaterThanCriterion<>(field, value, true));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> lessThan(NumericField<E, T> field,
      T value) {
    criteria.add(new LessThanCriterion<>(field, value, false));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> lessThanOrEqual(
      NumericField<E, T> field, T value) {
    criteria.add(new LessThanCriterion<>(field, value, true));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> isMissing(MetamodelField<E, T> field) {
    criteria.add(new IsMissingCriterion<>(field, true));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> exists(MetamodelField<E, T> field) {
    criteria.add(new IsMissingCriterion<>(field, false));
    return this;
  }

  public SearchCriteria<E> build() {
    return new SearchCriteria<>(criteria, operator);
  }
}
