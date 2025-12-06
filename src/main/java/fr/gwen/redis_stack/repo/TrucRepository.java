package fr.gwen.redis_stack.repo;

import fr.gwen.redis_stack.model.Truc;
import fr.gwen.redis_stack.repo.config.SearchableRedisRepository;
import org.springframework.stereotype.Repository;

public interface TrucRepository extends SearchableRedisRepository<Truc, Long> {

}
