package fr.gwen.redis_stack.statemanager;

import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;

public interface StateManagerService<ID, S extends AppState<ID>> {

  void deleteById(ID id);

  void create(S state);

  Iterable<S> search(SearchCriteria<S> criteria);
}
