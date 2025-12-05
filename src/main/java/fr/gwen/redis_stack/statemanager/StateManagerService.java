package fr.gwen.redis_stack.statemanager;

import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;
import java.util.Optional;

public interface StateManagerService<ID, S extends AppState<ID>> {

  Optional<S> findById(ID id);

  void deleteById(ID id);

  void create(S state);

  Iterable<S> search(SearchCriteria<S> criteria);
}
