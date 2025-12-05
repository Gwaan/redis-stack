package fr.gwen.redis_stack.repo.config;

import com.redis.om.spring.search.stream.EntityStream;
import fr.gwen.redis_stack.qb.criteria.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// Post processor qui ajoute au proxy d'un repo redis la capacité de recherche avancée
// cela évite de devoir déclarer deux repositories, un pour rechercher et un autre avec le repo standard redis
// Un repo spécialisé a juste besoin d'étendre SearchableRedisRepository pour avoir accès aux méthodes
// du repo JPA + le report de recherche avancée
public class SearchableRepositoryProxyPostProcessor implements BeanPostProcessor {

  private final EntityStream entityStream;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    for (Class<?> iface : bean.getClass().getInterfaces()) {
      if (SearchableRedisRepository.class.isAssignableFrom(iface)) {
        log.debug("Wrapping repository with search capability: {}", beanName);
        return createSearchableProxy(bean);
      }
    }
    return bean;
  }

  private Object createSearchableProxy(Object target) {
    final var proxyFactory = new ProxyFactory(target);

    // ajoute l'interface searchable au proxy
    proxyFactory.addInterface(SearchableRepositoryFragment.class);

    // Ajoute l'intercepteur qui utilise l'implem générique
    proxyFactory.addAdvice(new SearchMethodInterceptor(entityStream));

    return proxyFactory.getProxy();
  }

  @RequiredArgsConstructor
  private static class SearchMethodInterceptor implements MethodInterceptor {

    private final EntityStream entityStream;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      if (invocation.getMethod().getDeclaringClass().equals(SearchableRepositoryFragment.class)) {
        SearchCriteria<AppState<?>> criteria = (SearchCriteria<AppState<?>>) invocation.getArguments()[0];

        // appel de l'implem générique
        var impl = new SearchableRepositoryFragmentImpl<>(entityStream);
        return impl.search(criteria);
      }

      // délègue toutes les autres méthodes au repo spring data redis
      return invocation.proceed();
    }
  }
}