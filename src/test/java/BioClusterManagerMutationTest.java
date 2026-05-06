import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BioClusterManagerMutationTest {

    private BioClusterManager manager;

    @BeforeEach
    void setUp() {
        manager = new BioClusterManager();
    }

    @Test
    void deveConectarQuandoDentroDoRaioMesmaEspecieESaudeAcima() {
        List<String> conexoes = manager.processarClusters(
            Arrays.asList(
                obs(1, 1, 0, 0, 80, false),
                obs(2, 1, 3, 3, 80, false)
            ),
            5.0,
            60.0,
            false,
            10
        );

        assertEquals(1, conexoes.size());
    }

    @Test
    void deveConectarComModoInterLigadoMesmoComEspeciesDiferentes() {
        List<String> conexoes = manager.processarClusters(
            Arrays.asList(
                obs(1, 1, 0, 0, 90, false),
                obs(2, 2, 2, 2, 90, false)
            ),
            5.0,
            60.0,
            true,
            10
        );

        assertEquals(1, conexoes.size());
    }

    @Test
    void naoDeveConectarQuandoForaDoRaio() {
        List<String> conexoes = manager.processarClusters(
            Arrays.asList(
                obs(1, 1, 0, 0, 80, false),
                obs(2, 1, 8, 8, 80, false)
            ),
            5.0,
            60.0,
            false,
            10
        );

        assertTrue(conexoes.isEmpty());
    }

    @Test
    void naoDeveConectarQuandoAmbasInvasoras() {
        List<String> conexoes = manager.processarClusters(
            Arrays.asList(
                obs(1, 1, 0, 0, 80, true),
                obs(2, 1, 2, 2, 80, true)
            ),
            5.0,
            60.0,
            false,
            10
        );

        assertTrue(conexoes.isEmpty());
    }

    @Test
    void deveInterromperNoLimiteDeSeguranca() {
        List<String> conexoes = manager.processarClusters(
            Arrays.asList(
                obs(1, 1, 0, 0, 80, false),
                obs(2, 1, 1, 1, 80, false),
                obs(3, 1, 2, 2, 80, false)
            ),
            10.0,
            60.0,
            false,
            1
        );

        assertEquals(1, conexoes.size());
    }

    private Observation obs(int id, int especieId, double x, double y, double saude, boolean invasora) {
        return new Observation(id, especieId, x, y, saude, invasora);
    }
}
