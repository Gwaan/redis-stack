package fr.gwen.redis_stack.controller;

import fr.gwen.redis_stack.indexable.Dossier;
import fr.gwen.redis_stack.service.DossierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dossiers")
@Slf4j
public class DossierController {

  private final DossierService dossierService;
  private final ObjectMapper objectMapper;

  @PostMapping
  public ResponseEntity<Dossier> create(@RequestBody Dossier dossier) {
    log.info("Création dossier: {}", dossier.getNom());

    if (dossier.getId() == null || dossier.getId().isEmpty()) {
      dossier.setId(UUID.randomUUID().toString());
    }

    if (dossier.getCreatedAt() == null) {
      dossier.setCreatedAt(LocalDateTime.now());
    }

    Dossier saved = dossierService.save(dossier);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Dossier> getById(@PathVariable String id) {
    log.info("Recherche dossier par ID: {}", id);

    Dossier dossier = dossierService.findById(id);
    return dossier != null ? ResponseEntity.ok(dossier) : ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getAll(
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit
  ) {
    log.info("Récupération dossiers (offset={}, limit={})", offset, limit);

    List<Dossier> dossiers = dossierService.searchAll(offset, limit);
    long total = dossierService.countAll();

    Map<String, Object> response = new HashMap<>();
    response.put("data", dossiers);
    response.put("total", total);
    response.put("offset", offset);
    response.put("limit", limit);
    response.put("hasMore", offset + limit < total);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<Map<String, Object>> getByStatus(
      @PathVariable String status,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit
  ) {
    log.info("Recherche par status: {}", status);

    List<Dossier> dossiers = dossierService.searchByStatus(status, offset, limit);
    long total = dossierService.count("@status:{" + status + "}");

    return ResponseEntity.ok(
        Map.of(
            "data", dossiers,
            "total", total,
            "status", status
        )
    );
  }

  @GetMapping("/search")
  public ResponseEntity<Map<String, Object>> searchByNom(
      @RequestParam String nom,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit
  ) {
    log.info("Recherche par nom: {}", nom);

    List<Dossier> dossiers = dossierService.searchByNom(nom, offset, limit);

    return ResponseEntity.ok(
        Map.of(
            "data", dossiers,
            "query", nom,
            "count", dossiers.size()
        )
    );
  }

  @GetMapping("/montant")
  public ResponseEntity<Map<String, Object>> searchByMontant(
      @RequestParam double min,
      @RequestParam double max,
      @RequestParam(defaultValue = "0") int offset,
      @RequestParam(defaultValue = "10") int limit
  ) {
    log.info("Recherche par montant: [{} - {}]", min, max);

    List<Dossier> dossiers = dossierService.searchByMontantRange(min, max, offset, limit);

    return ResponseEntity.ok(
        Map.of(
            "data", dossiers,
            "min", min,
            "max", max,
            "count", dossiers.size()
        )
    );
  }

  @GetMapping("/search/advanced")
  public ResponseEntity<Map<String, Object>> searchAdvanced(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Double minMontant,
      @RequestParam(required = false) String nom
  ) {
    log.info("Recherche avancée - status={}, minMontant={}, nom={}", status, minMontant, nom);

    List<Dossier> dossiers = dossierService.searchComplex(status, minMontant, nom);

    return ResponseEntity.ok(
        Map.of(
            "data", dossiers,
            "filters", Map.of(
                "status", status != null ? status : "tous",
                "minMontant", minMontant != null ? minMontant : "aucun",
                "nom", nom != null ? nom : "tous"
            ),
            "count", dossiers.size()
        )
    );
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
    log.info("Suppression dossier: {}", id);

    boolean deleted = dossierService.delete(id);
    return deleted
        ? ResponseEntity.ok(Map.of("message", "Dossier supprimé avec succès", "id", id))
        : ResponseEntity.notFound().build();
  }

  @DeleteMapping
  public ResponseEntity<Map<String, Object>> deleteAll() {
    log.warn("Suppression de tous les dossiers");
    int deleted = dossierService.deleteAll();

    return ResponseEntity.ok(
        Map.of(
            "message", "Tous les dossiers ont été supprimés",
            "count", deleted
        )
    );
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
  public ResponseEntity<Void> exists(@PathVariable String id) {
    boolean exists = dossierService.exists(id);
    return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  @GetMapping("/count")
  public ResponseEntity<Map<String, Long>> count() {
    long count = dossierService.countAll();
    return ResponseEntity.ok(Map.of("count", count));
  }

  @PostMapping("/init-test-data")
  public ResponseEntity<Map<String, Object>> initTestData() {
    log.info("Initialisation des données de test");

    List<Dossier> dossiers = List.of(
        createDossier("1", "Dossier Alpha", "VALIDE", 1500.50),
        createDossier("2", "Dossier Beta", "EN_ATTENTE", 2500.00),
        createDossier("3", "Dossier Gamma", "VALIDE", 3200.75),
        createDossier("4", "Dossier Delta", "REJETE", 1000.00),
        createDossier("5", "Dossier Epsilon", "VALIDE", 5000.00),
        createDossier("6", "Dossier Zeta", "EN_ATTENTE", 1200.30),
        createDossier("7", "Dossier Eta", "VALIDE", 4500.25),
        createDossier("8", "Dossier Theta", "REJETE", 800.00),
        createDossier("9", "Dossier Iota", "VALIDE", 6000.00),
        createDossier("10", "Dossier Kappa", "EN_ATTENTE", 2200.50)
    );

    dossierService.saveAll(dossiers);

    return ResponseEntity.ok(
        Map.of(
            "message", "Données de test initialisées",
            "count", dossiers.size(),
            "dossiers", dossiers
        )
    );
  }

  @GetMapping("/debug/keys")
  public ResponseEntity<Map<String, Object>> debugKeys() {
    return ResponseEntity.ok(dossierService.debugAllKeys());
  }

  @GetMapping("/stats")
  public ResponseEntity<Map<String, Object>> getStats() {
    log.info("Récupération des statistiques");

    long total = dossierService.countAll();
    long valides = dossierService.count("@status:{VALIDE}");
    long enAttente = dossierService.count("@status:{EN_ATTENTE}");
    long rejetes = dossierService.count("@status:{REJETE}");

    return ResponseEntity.ok(
        Map.of(
            "total", total,
            "byStatus", Map.of(
                "VALIDE", valides,
                "EN_ATTENTE", enAttente,
                "REJETE", rejetes
            )
        )
    );
  }

  private Dossier createDossier(String id, String nom, String status, double montant) {
    Dossier dossier = new Dossier();
    dossier.setId(id);
    dossier.setNom(nom);
    dossier.setStatus(status);
    dossier.setMontant(montant);
    dossier.setDescription("Description du " + nom);
    dossier.setCreatedAt(LocalDateTime.now());
    return dossier;
  }
}
