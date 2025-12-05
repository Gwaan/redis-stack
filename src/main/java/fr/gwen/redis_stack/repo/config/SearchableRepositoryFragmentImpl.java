package fr.gwen.redis_stack.repo.config;

import com.redis.om.spring.search.stream.EntityStream;
import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchableRepositoryFragmentImpl<T extends AppState<?>> implements
    SearchableRepositoryFragment<T> {

  private final EntityStream entityStream;

  @Override
  public Collection<T> search(SearchCriteria<T> criteria) {
    return entityStream.of(criteria.getEntityClass()).filter(criteria.toSearchPredicate())
        .collect(Collectors.toList());
  }
}
