package fr.gwen.redis_stack.repo.config;

import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;
import java.util.Collection;

public interface SearchableRepositoryFragment<T extends AppState<?>> {

  Collection<T> search(SearchCriteria<T> criteria);
}