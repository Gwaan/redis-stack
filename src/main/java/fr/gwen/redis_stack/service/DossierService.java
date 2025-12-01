package fr.gwen.redis_stack.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisModulesCommands;
import fr.gwen.redis_stack.indexable.Dossier;
import io.lettuce.core.json.JsonPath;
import io.lettuce.core.json.JsonValue;
import io.lettuce.core.search.SearchReply;
import io.lettuce.core.search.SearchReply.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class DossierService {

  private final StatefulRedisModulesConnection<String, String> connection;
  private final ObjectMapper objectMapper;

  public Dossier save(Dossier dossier) {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();
      String key = "dossier:" + dossier.getId();
      String json = objectMapper.writeValueAsString(dossier);

      commands.jsonSet(key, JsonPath.of("$"), json);

      log.info("Dossier sauvegardé: {}", key);
      return dossier;
    } catch (Exception e) {
      log.error("Erreur sauvegarde: {}", e.getMessage(), e);
      throw new RuntimeException("Erreur sauvegarde", e);
    }
  }

  public Dossier findById(String id) {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();
      String key = "dossier:" + id;

      List<JsonValue> jsonValues = commands.jsonGet(key, JsonPath.of("$"));
      if (jsonValues == null || jsonValues.isEmpty()) {
        return null;
      }

      String json = jsonValues.get(0).toString();
      return objectMapper.readValue(json, Dossier.class);

    } catch (Exception e) {
      log.error("Erreur lecture: {}", e.getMessage(), e);
      return null;
    }
  }

  public List<Dossier> searchByStatus(String status, int offset, int limit) {
    return search("@status:{" + status + "}", offset, limit);
  }

  public List<Dossier> searchByNom(String nom, int offset, int limit) {
    return search("@nom:" + nom + "*", offset, limit);
  }

  public List<Dossier> searchByMontantRange(double min, double max, int offset, int limit) {
    return search("@montant:[" + min + " " + max + "]", offset, limit);
  }

  public List<Dossier> searchComplex(String status, Double minMontant, String nomContient) {
    StringBuilder query = new StringBuilder();

    if (status != null && !status.isEmpty()) {
      query.append("@status:{").append(status).append("}");
    }
    if (minMontant != null) {
      if (query.length() > 0) {
        query.append(" ");
      }
      query.append("@montant:[").append(minMontant).append(" +inf]");
    }
    if (nomContient != null && !nomContient.isEmpty()) {
      if (query.length() > 0) {
        query.append(" ");
      }
      query.append("@nom:").append(nomContient).append("*");
    }

    if (query.length() == 0) {
      query.append("*");
    }

    return search(query.toString(), 0, 100);
  }

  private List<Dossier> search(String query, int offset, int limit) {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();

      SearchReply<String, String> results = commands.ftSearch("idx:dossier", query);
      log.debug("Résultats trouvés: {} sur {}", results.size(), results.getCount());

      List<SearchResult<String, String>> searchResults = results.getResults();
      List<Dossier> dossiers = new ArrayList<>();

      int start = Math.min(offset, searchResults.size());
      int end = Math.min(offset + limit, searchResults.size());

      for (int i = start; i < end; i++) {
        SearchResult<String, String> result = searchResults.get(i);
        try {
          String jsonStr = result.getFields().get("$");
          if (jsonStr != null && !jsonStr.isEmpty()) {
            dossiers.add(objectMapper.readValue(jsonStr, Dossier.class));
          }
        } catch (Exception e) {
          log.warn("Erreur parsing document {}: {}", result.getId(), e.getMessage());
        }
      }

      return dossiers;

    } catch (Exception e) {
      log.error("Erreur recherche '{}': {}", query, e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  public long count(String query) {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();
      SearchReply<String, String> results = commands.ftSearch("idx:dossier", query);
      long count = results.getCount();

      log.debug("Count retourné: {}", count);
      return count;
    } catch (Exception e) {
      log.error("Erreur count pour query '{}': {}", query, e.getMessage(), e);
      return 0;
    }
  }

  public boolean delete(String id) {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();
      Long deleted = commands.del("dossier:" + id);

      if (deleted > 0) {
        log.info("Dossier supprimé: dossier:{}", id);
        return true;
      }
      return false;

    } catch (Exception e) {
      log.error("Erreur suppression: {}", e.getMessage(), e);
      return false;
    }
  }

  public Map<String, Object> debugAllKeys() {
    try {
      RedisModulesCommands<String, String> commands = connection.sync();
      List<String> keys = commands.keys("dossier:*");

      log.info("Debug: {} clés trouvées", keys.size());

      List<Map<String, Object>> data = new ArrayList<>();

      for (String key : keys) {
        try {
          List<JsonValue> jsonValues = commands.jsonGet(key, JsonPath.of("$"));
          if (jsonValues != null && !jsonValues.isEmpty()) {
            String jsonArray = jsonValues.get(0).toString();

            if (jsonArray.startsWith("[") && jsonArray.endsWith("]")) {
              jsonArray = jsonArray.substring(1, jsonArray.length() - 1);
            }

            Dossier dossier = objectMapper.readValue(jsonArray, Dossier.class);
            data.add(Map.of("key", key, "value", dossier));
          }
        } catch (Exception e) {
          log.warn("Erreur lecture clé {}: {}", key, e.getMessage());

          try {
            List<JsonValue> jsonValues = commands.jsonGet(key, JsonPath.of("$"));
            Object raw =
                (jsonValues != null && !jsonValues.isEmpty()) ? jsonValues.get(0).toString() : null;

            data.add(Map.of("key", key, "rawValue", raw, "error", e.getMessage()));
          } catch (Exception ignored) {
            data.add(Map.of("key", key, "error", e.getMessage()));
          }
        }
      }

      return Map.of("totalKeys", keys.size(), "keys", keys, "data", data);

    } catch (Exception e) {
      log.error("Erreur debug keys: {}", e.getMessage(), e);
      return Map.of("error", e.getMessage(), "totalKeys", 0, "keys", List.of(), "data", List.of());
    }
  }

  public List<Dossier> saveAll(List<Dossier> dossiers) {
    dossiers.forEach(this::save);
    log.info("{} dossiers sauvegardés", dossiers.size());
    return dossiers;
  }

  public List<Dossier> searchAll(int offset, int limit) {
    return search("*", offset, limit);
  }

  public long countAll() {
    RedisModulesCommands<String, String> commands = connection.sync();
    return commands.keys("dossier:*").size();
  }

  public int deleteAll() {
    RedisModulesCommands<String, String> commands = connection.sync();
    List<String> keys = commands.keys("dossier:*");

    if (keys.isEmpty()) {
      log.info("Aucun dossier à supprimer");
      return 0;
    }

    Long deleted = commands.del(keys.toArray(new String[0]));
    log.info("{} dossiers supprimés", deleted);
    return deleted.intValue();
  }

  public boolean exists(String id) {
    RedisModulesCommands<String, String> commands = connection.sync();
    return commands.exists("dossier:" + id) > 0;
  }
}
