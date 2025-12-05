package fr.gwen.redis_stack.qb.criteria.builder;

import com.redis.om.spring.metamodel.MetamodelField;
import com.redis.om.spring.metamodel.indexed.NumericField;
import com.redis.om.spring.metamodel.indexed.TextField;
import fr.gwen.redis_stack.qb.criteria.GroupCriterion;
import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.qb.criteria.SimpleCriterion;
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
import fr.gwen.redis_stack.service.AppState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SearchCriteriaBuilder<E extends AppState<?>> {

  private final List<SearchCriteria<E>> criteria = new ArrayList<>();
  private GroupCriterion.LogicalOperator operator = GroupCriterion.LogicalOperator.AND;
  private final Class<E> entityClass;

  private SearchCriteriaBuilder(Class<E> entityClass) {
    this.entityClass = entityClass;
  }

  public static <E extends AppState<?>> SearchCriteriaBuilder<E> of(Class<E> entityClass) {
    return new SearchCriteriaBuilder<>(entityClass);
  }

  public SearchCriteriaBuilder<E> and() {
    this.operator = GroupCriterion.LogicalOperator.AND;
    return this;
  }

  public SearchCriteriaBuilder<E> or() {
    this.operator = GroupCriterion.LogicalOperator.OR;
    return this;
  }

  public SearchCriteriaBuilder<E> nested(Consumer<SearchCriteriaBuilder<E>> nestedBuilder) {
    var subBuilder = new SearchCriteriaBuilder<>(entityClass);
    nestedBuilder.accept(subBuilder);
    criteria.add(subBuilder.build());
    return this;
  }

  public <T> SearchCriteriaBuilder<E> equals(MetamodelField<E, T> field, T value) {
    criteria.add(new SimpleCriterion<>(new EqualsCriterion<>(field, value), entityClass));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> notEquals(MetamodelField<E, T> field, T value) {
    criteria.add(new SimpleCriterion<>(new NotEqualsCriterion<>(field, value), entityClass));
    return this;
  }

  @SafeVarargs
  public final <T> SearchCriteriaBuilder<E> in(MetamodelField<E, T> field, T... values) {
    criteria.add(
        new SimpleCriterion<>(new InCriterion<>(field, Arrays.asList(values)), entityClass));
    return this;
  }


  public <T> SearchCriteriaBuilder<E> in(MetamodelField<E, T> field, List<T> values) {
    criteria.add(new SimpleCriterion<>(new InCriterion<>(field, values), entityClass));
    return this;
  }

  @SafeVarargs
  public final <T> SearchCriteriaBuilder<E> notIn(MetamodelField<E, T> field, T... values) {
    criteria.add(
        new SimpleCriterion<>(new NotInCriterion<>(field, Arrays.asList(values)), entityClass));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> notIn(MetamodelField<E, T> field, List<T> values) {
    criteria.add(new SimpleCriterion<>(new NotInCriterion<>(field, values), entityClass));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> between(NumericField<E, T> field, T min,
      T max) {
    criteria.add(new SimpleCriterion<>(new BetweenCriterion<>(field, min, max), entityClass));
    return this;
  }

  public SearchCriteriaBuilder<E> contains(TextField<E, String> field, String pattern) {
    criteria.add(new SimpleCriterion<>(new ContainingCriterion<>(field, pattern), entityClass));
    return this;
  }

  public SearchCriteriaBuilder<E> notContains(TextField<E, String> field, String pattern) {
    criteria.add(new SimpleCriterion<>(new NotContainingCriterion<>(field, pattern), entityClass));
    return this;
  }

  public SearchCriteriaBuilder<E> startsWith(TextField<E, String> field, String pattern) {
    criteria.add(new SimpleCriterion<>(
        new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.STARTS_WITH), entityClass));
    return this;
  }

  public SearchCriteriaBuilder<E> endsWith(TextField<E, String> field, String pattern) {
    criteria.add(
        new SimpleCriterion<>(new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.ENDS_WITH),
            entityClass));
    return this;
  }

  public SearchCriteriaBuilder<E> like(TextField<E, String> field, String pattern) {
    criteria.add(
        new SimpleCriterion<>(new LikeCriterion<>(field, pattern, LikeCriterion.LikeMode.LIKE),
            entityClass));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> greaterThan(NumericField<E, T> field,
      T value) {
    criteria.add(
        new SimpleCriterion<>(new GreaterThanCriterion<>(field, value, false), entityClass));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> greaterThanOrEqual(
      NumericField<E, T> field, T value) {
    criteria.add(
        new SimpleCriterion<>(new GreaterThanCriterion<>(field, value, true), entityClass));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> lessThan(NumericField<E, T> field,
      T value) {
    criteria.add(new SimpleCriterion<>(new LessThanCriterion<>(field, value, false), entityClass));
    return this;
  }

  public <T extends Comparable<T>> SearchCriteriaBuilder<E> lessThanOrEqual(
      NumericField<E, T> field, T value) {
    criteria.add(new SimpleCriterion<>(new LessThanCriterion<>(field, value, true), entityClass));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> isMissing(MetamodelField<E, T> field) {
    criteria.add(new SimpleCriterion<>(new IsMissingCriterion<>(field, true), entityClass));
    return this;
  }

  public <T> SearchCriteriaBuilder<E> exists(MetamodelField<E, T> field) {
    criteria.add(new SimpleCriterion<>(new IsMissingCriterion<>(field, false), entityClass));
    return this;
  }

  public GroupCriterion<E> build() {
    return new GroupCriterion<>(criteria, operator, entityClass);
  }
}
