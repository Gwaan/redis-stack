package fr.gwen.redis_stack.service;

import fr.gwen.redis_stack.repo.Dossier;
import fr.gwen.redis_stack.repo.DossierRepository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
@Slf4j
public class DossierService {

  private final DossierRepository repository;

  public Optional<Dossier> findByTagsTestRedisOm(String nom) {
    return repository.findOneByNom(nom);
  }
}
