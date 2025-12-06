package fr.gwen.redis_stack.statemanager;

import fr.gwen.redis_stack.qb.internal.SearchCriteria;
import fr.gwen.redis_stack.model.Dossier;
import fr.gwen.redis_stack.repo.DossierRepository;
import fr.gwen.redis_stack.repo.TrucRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DossierStateManager implements StateManagerService<String, Dossier> {

  private final DossierRepository dossierRepository;
  private final TrucRepository trucRepository;

  @Override
  public Optional<Dossier> findById(String s) {
    return dossierRepository.findById(s);
  }

  @Override
  public void deleteById(String s) {

  }

  @Override
  public void create(Dossier state) {
    dossierRepository.save(state);
  }

  public void createAll(List<Dossier> doss) {
    dossierRepository.saveAll(doss);
  }

  @Override
  public List<Dossier> search(SearchCriteria<Dossier> criteria) {
    return dossierRepository.search(criteria).stream().toList();
  }
}
