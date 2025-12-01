package fr.gwen.redis_stack.controller;

import fr.gwen.redis_stack.repo.Dossier;
import fr.gwen.redis_stack.service.DossierService;
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

  @GetMapping("/test-redis-om")
  public Optional<Dossier> testRedisOm(@RequestParam String nom) {
    return dossierService.findByTagsTestRedisOm(nom);
  }
}
