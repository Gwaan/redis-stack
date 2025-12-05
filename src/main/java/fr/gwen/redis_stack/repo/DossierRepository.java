package fr.gwen.redis_stack.repo;

import fr.gwen.redis_stack.repo.config.SearchableRedisRepository;
import java.util.Optional;

public interface DossierRepository extends SearchableRedisRepository<Dossier, String> {

  Optional<Dossier> findOneByNom(String nom);

}
