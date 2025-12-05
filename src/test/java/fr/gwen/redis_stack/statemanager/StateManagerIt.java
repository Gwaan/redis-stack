package fr.gwen.redis_stack.statemanager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fr.gwen.redis_stack.qb.criteria.builder.SearchCriteriaBuilder;
import fr.gwen.redis_stack.repo.Dossier;
import fr.gwen.redis_stack.repo.Dossier$;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class StateManagerIt extends RedisTestBase {

  @Autowired
  private DossierStateManager dossierStateManager;

  @BeforeEach
  void setupForTest() {
    final var doss = List.of(Dossier.builder().id("tata").status("EN_ATTENTE").build(),
        Dossier.builder().id("tutu").status("EN_ATTENTE").build(),
        Dossier.builder().id("toto").status("RIEN").build());
    dossierStateManager.createAll(doss);
  }

  @Test
  void should_find_one() {
    // GIVEN
    dossierStateManager.create(Dossier.builder().id("toto").build());

    // WHEN
    final var result = dossierStateManager.findById("toto");

    // THEN
    assertThat(result.get().getId()).isEqualTo("toto");
  }

  @Test
  void should_find_multiple_statut_en_attente() {
    // GIVEN
    dossierStateManager.create(Dossier.builder().id("tata").status("EN_ATTENTE").build());
    dossierStateManager.create(Dossier.builder().id("tutu").status("EN_ATTENTE").build());
    dossierStateManager.create(Dossier.builder().id("toto").status("RIEN").build());

    // WHEN
    final var result = dossierStateManager.search(
        SearchCriteriaBuilder.<Dossier>builder().contains(Dossier$.STATUS, "ATTE").build());

    // THEN
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void should_find_multiple_statut_en_attente_and_Montant_Between_1_And_100() {
    // GIVEN
    dossierStateManager.create(
        Dossier.builder().id("tata").status("EN_ATTENTE").montant(101.0).build());
    dossierStateManager.create(
        Dossier.builder().id("tutu").status("EN_ATTENTE").montant(20.0).build());
    dossierStateManager.create(Dossier.builder().id("toto").status("RIEN").build());

    // WHEN
    final var result = dossierStateManager.search(
        SearchCriteriaBuilder.<Dossier>builder().contains(Dossier$.STATUS, "ATTE")
            .between(Dossier$.MONTANT, 1.0, 100.0).build());

    // THEN
    assertThat(result.size()).isEqualTo(1);
  }

}
