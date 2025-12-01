package fr.gwen.redis_stack.repo;

import com.redis.om.spring.annotations.Query;
import com.redis.om.spring.repository.RedisDocumentRepository;
import java.util.Optional;

public interface DossierRepository extends RedisDocumentRepository<Dossier, String> {

  Optional<Dossier> findOneByNom(String nom);

}
