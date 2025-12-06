package fr.gwen.redis_stack.statemanager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import fr.gwen.redis_stack.model.Dossier$;
import fr.gwen.redis_stack.qb.internal.SearchCriteriaBuilder;
import fr.gwen.redis_stack.model.Dossier;
import java.util.List;
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
    final var doss = List.of(
        Dossier.builder().id("tata").nom("machin").montant(10.0).status("EN_ATTENTE").build(),
        Dossier.builder().id("tutu").status("EN_ATTENTE").build(),
        Dossier.builder().id("toto").status("RIEN").montant(99.0).build());
    dossierStateManager.createAll(doss);
  }

  @Test
  void should_find_one() {
    // GIVEN

    // WHEN
    final var result = dossierStateManager.findById("toto");

    // THEN
    assertThat(result.get().getId()).isEqualTo("toto");
  }

  @Test
  void should_find_multiple_statut_en_attente() {
    // GIVEN

    // WHEN
    final var result = dossierStateManager.search(
        SearchCriteriaBuilder.of(Dossier.class).contains(Dossier$.STATUS, "ATTE").build());

    // THEN
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void should_find_multiple_statut_en_attente_and_Montant_Between_1_And_100() {
    // GIVEN

    // WHEN
    final var result = dossierStateManager.search(
        SearchCriteriaBuilder.of(Dossier.class).and().contains(Dossier$.STATUS, "ATTE")
            .between(Dossier$.MONTANT, 1.0, 100.0).build());

    // THEN
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  void test_nested_conditions_builder() {
    // GIVEN
    // évalué comme: status CONTAINS "DSKLDSKLDKLSQK" OR (montant BETWEEN 1.0 .. 100.0 AND status CONTAINS "RI")
    // requête redis sous jacente -> "FT.SEARCH" "idx:dossier" "(( @status:*DSKLDSKLDKLSQK*)|(( @montant:[1.0 100.0]) ( @status:*RI*)))" "LIMIT" "0" "10000" "DIALECT" "2"
    final var sc = SearchCriteriaBuilder.of(Dossier.class).or()
        .contains(Dossier$.STATUS, "DSKLDSKLDKLSQK")
        .nested(
            ac -> ac.and()
                .between(Dossier$.MONTANT, 1.0, 100.0)
                .contains(Dossier$.STATUS, "RI"))
        .build();

    // WHEN
    final var result = dossierStateManager.search(sc);

    // THEN
    assertThat(result.size()).isEqualTo(1);
  }

}
