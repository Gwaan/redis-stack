package fr.gwen.redis_stack.processor;

import fr.gwen.redis_stack.annotations.RedisIndexField;
import fr.gwen.redis_stack.annotations.RedisIndexable;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import java.lang.reflect.Field;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
public class RedisIndexProcessor implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RedisIndexProcessor.class);
  private static final String BASE_PACKAGE = "fr.gwen";

  private final StatefulRedisConnection<String, String> connection;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    LOGGER.info("Scanning for @RedisIndexable classes in package: {}", BASE_PACKAGE);

    final var created = scanCandidates().map(this::loadClass).flatMap(Optional::stream)
        .filter(this::processClass).count();

    LOGGER.info("{} index Redis créés/vérifiés", created);
  }

  private Stream<BeanDefinition> scanCandidates() {
    final var scanner = new ClassPathScanningCandidateComponentProvider(false);
    scanner.addIncludeFilter(new AnnotationTypeFilter(RedisIndexable.class));
    return scanner.findCandidateComponents(BASE_PACKAGE).stream();
  }

  private Optional<Class<?>> loadClass(BeanDefinition beanDef) {
    try {
      return Optional.of(Class.forName(beanDef.getBeanClassName()));
    } catch (ClassNotFoundException e) {
      LOGGER.error(" Classe non trouvée: {}", beanDef.getBeanClassName());
      return Optional.empty();
    }
  }

  private boolean processClass(Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(RedisIndexable.class))
        .filter(RedisIndexable::autoCreate).map(annotation -> {
          final var indexName = resolveIndexName(clazz, annotation);
          final var success = ensureIndexExists(indexName, annotation, clazz);

          if (success) {
            LOGGER.info("Index '{}' pour {}", indexName, clazz.getSimpleName());
          }

          return success;
        }).orElse(false);
  }

  private String resolveIndexName(Class<?> clazz, RedisIndexable annotation) {
    return annotation.indexName().isEmpty() ? "idx:" + clazz.getSimpleName().toLowerCase()
        : annotation.indexName();
  }

  private boolean ensureIndexExists(String indexName, RedisIndexable annotation, Class<?> clazz) {
    final var commands = connection.sync();

    if (indexAlreadyExists(commands, indexName)) {
      LOGGER.debug("Index '{}' existe déjà", indexName);
      return true;
    }

    return createIndex(commands, indexName, annotation, clazz);
  }

  private boolean indexAlreadyExists(RedisCommands<String, String> commands, String indexName) {
    try {
      final var script = "return redis.call('FT.INFO', KEYS[1])";
      commands.eval(script, ScriptOutputType.MULTI, indexName);
      return true;

    } catch (Exception e) {
      final var errorMsg = Optional.ofNullable(e.getMessage()).map(String::toLowerCase).orElse("");

      if (errorMsg.contains("module") || errorMsg.contains("unknown command")) {
        throw new RuntimeException("RediSearch module required. Please use Redis Stack.", e);
      }

      return false;
    }
  }

  private boolean createIndex(RedisCommands<String, String> commands, String indexName,
      RedisIndexable annotation, Class<?> clazz) {

    final var fields = extractIndexFields(clazz, annotation.dataType());

    if (fields.isEmpty()) {
      LOGGER.warn("    Aucun champ @RedisIndexField trouvé dans {}", clazz.getSimpleName());
      return false;
    }

    final var args = buildCreateCommand(indexName, annotation, fields);

    try {
      final var luaScript = new StringBuilder("return redis.call('FT.CREATE'");
      for (final var arg : args) {
        luaScript.append(", '").append(arg.replace("'", "\\'")).append("'");
      }
      luaScript.append(")");

      commands.eval(luaScript.toString(), ScriptOutputType.STATUS);

      LOGGER.info("Index '{}' créé avec {} champs", indexName, countFields(fields));
      return true;

    } catch (Exception e) {
      LOGGER.error("Erreur FT.CREATE pour '{}': {}", indexName, e.getMessage());
      return false;
    }
  }

  private List<String> extractIndexFields(Class<?> clazz, RedisIndexable.DataType dataType) {
    return Arrays.stream(clazz.getDeclaredFields()).map(
            field -> Optional.ofNullable(field.getAnnotation(RedisIndexField.class))
                .map(annotation -> buildFieldDefinition(field, annotation, dataType)))
        .flatMap(Optional::stream).flatMap(List::stream).toList();
  }

  private List<String> buildFieldDefinition(Field field, RedisIndexField annotation,
      RedisIndexable.DataType dataType) {

    final var fieldName = annotation.name().isEmpty() ? field.getName() : annotation.name();

    final var fieldPath = computeFieldPath(fieldName, annotation, dataType);

    final var args = new ArrayList<String>();
    args.add(fieldPath);

    if (dataType == RedisIndexable.DataType.JSON) {
      args.add("AS");
      args.add(fieldName);
    }

    args.add(annotation.type().name());

    if (shouldAddSortable(annotation)) {
      args.add("SORTABLE");
    }

    return args;
  }

  private String computeFieldPath(String fieldName, RedisIndexField annotation,
      RedisIndexable.DataType dataType) {
    if (dataType != RedisIndexable.DataType.JSON) {
      return fieldName;
    }

    return annotation.jsonPath().isEmpty() ? "$." + fieldName : annotation.jsonPath();
  }

  private boolean shouldAddSortable(RedisIndexField annotation) {
    return annotation.sortable() && (annotation.type() == RedisIndexField.FieldType.TEXT
        || annotation.type() == RedisIndexField.FieldType.NUMERIC);
  }

  private List<String> buildCreateCommand(String indexName, RedisIndexable annotation,
      List<String> fields) {

    final var header = List.of(indexName, "ON",
        annotation.dataType() == RedisIndexable.DataType.JSON ? "JSON" : "HASH", "PREFIX", "1",
        annotation.keyPrefix(), "SCHEMA");

    return Stream.concat(header.stream(), fields.stream())
        .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }


  private long countFields(List<String> fields) {
    return fields.stream().filter(arg -> arg.startsWith("$") || arg.matches("[a-z].*")).count();
  }
}
