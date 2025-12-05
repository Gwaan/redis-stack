package fr.gwen.redis_stack.repo.config;

import com.redis.om.spring.repository.RedisDocumentRepository;
import fr.gwen.redis_stack.service.AppState;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SearchableRedisRepository<T extends AppState<ID>, ID> extends
    RedisDocumentRepository<T, ID>, SearchableRepositoryFragment<T> {

}