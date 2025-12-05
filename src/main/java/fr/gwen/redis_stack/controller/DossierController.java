package fr.gwen.redis_stack.controller;

import fr.gwen.redis_stack.qb.criteria.builder.SearchCriteriaBuilder;
import fr.gwen.redis_stack.repo.Dossier;
import fr.gwen.redis_stack.repo.Dossier$;
import fr.gwen.redis_stack.service.DossierService;
import fr.gwen.redis_stack.statemanager.DossierStateManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dossiers")
@Slf4j
public class DossierController {

  private final DossierService dossierService;
  private final DossierStateManager sm;

  @GetMapping("/test-redis-om")
  public Optional<Dossier> testRedisOm(@RequestParam String nom) {
    return dossierService.findByTagsTestRedisOm(nom);
  }

  @GetMapping("pred-test")
  public Iterable<Dossier> test() {
    final var criteriaBuilder = SearchCriteriaBuilder.of(Dossier.class).and()
        .contains(Dossier$.STATUS, "EN_").or().equals(Dossier$.ID, "2").build();

    return sm.search(criteriaBuilder);
  }
}
