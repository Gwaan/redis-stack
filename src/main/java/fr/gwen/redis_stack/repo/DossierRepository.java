package fr.gwen.redis_stack.repo;

import fr.gwen.redis_stack.model.Dossier;
import fr.gwen.redis_stack.repo.config.SearchableRedisRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

public interface DossierRepository extends SearchableRedisRepository<Dossier, String> {

  Optional<Dossier> findOneByNom(String nom);

}
