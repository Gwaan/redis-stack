package fr.gwen.redis_stack.repo.config;

import fr.gwen.redis_stack.qb.internal.SearchCriteria;
import fr.gwen.redis_stack.service.AppState;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
// Post processor qui ajoute au proxy d'un repo redis la capacité de recherche avancée
// cela évite de devoir déclarer deux repositories, un pour rechercher et un autre avec le repo spring data redis
// Un repo a juste besoin d'étendre SearchableRedisRepository pour avoir accès aux méthodes
// du repo JPA + le repo de recherche
public class SearchableRepositoryProxyPostProcessor implements BeanPostProcessor {

  private final ObjectProvider<SearchableRepositoryFragmentImpl<AppState<?>>> searchableImplProvider;

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
    proxyFactory.addInterface(SearchableRepositoryFragment.class);
    proxyFactory.addAdvice(new SearchMethodInterceptor(searchableImplProvider));
    return proxyFactory.getProxy();
  }

  private static class SearchMethodInterceptor implements MethodInterceptor {

    private final ObjectProvider<SearchableRepositoryFragmentImpl<AppState<?>>> searchableImplProvider;

    SearchMethodInterceptor(
        ObjectProvider<SearchableRepositoryFragmentImpl<AppState<?>>> searchableImplProvider) {
      this.searchableImplProvider = searchableImplProvider;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      if (invocation.getMethod().getDeclaringClass().equals(SearchableRepositoryFragment.class)) {
        final SearchCriteria<AppState<?>> criteria = (SearchCriteria<AppState<?>>) invocation.getArguments()[0];
        return searchableImplProvider.getObject().search(criteria);
      }

      return invocation.proceed();
    }
  }
}