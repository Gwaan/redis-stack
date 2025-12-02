package fr.gwen.redis_stack.statemanager;

import com.redis.om.spring.search.stream.EntityStream;
import fr.gwen.redis_stack.qb.criteria.CriteriaToPredicateConverter;
import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.qb.criteria.builder.SearchCriteriaBuilder;
import fr.gwen.redis_stack.repo.Dossier;
import fr.gwen.redis_stack.repo.Dossier$;
import java.util.stream.Collectors;
import javax.swing.text.html.parser.Entity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DossierStateManager implements StateManagerService<String, Dossier> {

  private final EntityStream es;

  @Override
  public void deleteById(String s) {

  }

  @Override
  public void create(Dossier state) {
  }

  @Override
  public Iterable<Dossier> search(SearchCriteria<Dossier> criteria) {
    return es.of(Dossier.class).filter(criteria.toSearchPredicate()).collect(Collectors.toList());
  }
}
