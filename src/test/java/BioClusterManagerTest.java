import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BioClusterManagerTest {

    private BioClusterManager bioClusterManager;
    private BioService bioService;

    @BeforeEach
    void setUp() {
        bioClusterManager = new BioClusterManager();
        bioService = new BioService();
    }

    @Test
    void testeConexaoMCDC() {
        assertAll(
            () -> assertTrue(
                bioService.validarConexao(10.0, 10.0, "Lobo", "Lobo", false, 80, 50, false, false)),
            () -> assertFalse(
                bioService.validarConexao(15.0, 10.0, "Lobo", "Lobo", false, 80, 50, false, false)),
            () -> assertFalse(
                bioService.validarConexao(5.0, 10.0, "Lobo", "Onca", false, 80, 50, false, false)),
            () -> assertTrue(
                bioService.validarConexao(5.0, 10.0, "Lobo", "Onca", true, 80, 50, false, false)),
            () -> assertFalse(
                bioService.validarConexao(5.0, 10.0, "Lobo", "Lobo", false, 30, 50, false, false)),
            () -> assertFalse(
                bioService.validarConexao(5.0, 10.0, "Lobo", "Lobo", false, 80, 50, true, true))
        );
    }

        @Test
        void ct01DeveConectarNoInPointDaDistancia() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, false),
                observation(2, 10, 3, 4, 80, false)),
            5.0,
            60.0,
            false,
            10);

        assertEquals(1, conexoes.size());
        assertEquals("Cluster:1-2", conexoes.get(0));
        }

        @Test
        void ct02DeveConectarEmModoEcologicoAmplo() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, false),
                observation(2, 20, 3, 3, 80, false)),
            5.0,
            60.0,
            true,
            10);

        assertEquals(1, conexoes.size());
        }

        @Test
        void ct03NaoDeveConectarForaDoLimiteDeDistancia() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, false),
                observation(2, 10, 3, 4.1, 80, false)),
            5.0,
            60.0,
            false,
            10);

        assertTrue(conexoes.isEmpty());
        }

        @Test
        void ct04DeveConectarComSaudeExatamenteNoPatamar() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 60, false),
                observation(2, 10, 2, 2, 60, false)),
            5.0,
            60.0,
            false,
            10);

        assertEquals(1, conexoes.size());
        }

        @Test
        void ct05NaoDeveConectarQuandoUmIndividuoEstaAbaixoDoPatamar() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, false),
                observation(2, 10, 2, 2, 59, false)),
            5.0,
            60.0,
            false,
            10);

        assertTrue(conexoes.isEmpty());
        }

        @Test
        void ct06NaoDeveFormarNucleoComDuasEspeciesInvasoras() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, true),
                observation(2, 10, 2, 2, 80, true)),
            5.0,
            60.0,
            false,
            10);

        assertTrue(conexoes.isEmpty());
        }

        @Test
        void ct07DeveRespeitarOLimiteOperacional() {
        List<String> conexoes = bioClusterManager.processarClusters(
            Arrays.asList(
                observation(1, 10, 0, 0, 80, false),
                observation(2, 10, 1, 1, 80, false),
                observation(3, 10, 2, 2, 80, false)),
            10.0,
            60.0,
            false,
            1);

        assertEquals(1, conexoes.size());
        }

        @Test
        void ct08DeveTratarListaNulaSemEncerramentoAbrupto() {
        try {
            List<String> conexoes = bioClusterManager.processarClusters(null, 10.0, 60.0, false, 10);

            assertNotNull(conexoes);
            assertTrue(conexoes.isEmpty());
        } catch (Exception exception) {
            assertFalse(exception instanceof NullPointerException);
        }
        }

        private Observation observation(int id, int especieId, double x, double y, double saude, boolean invasora) {
        return new Observation(id, especieId, x, y, saude, invasora);
        }

        private class BioService {
            boolean validarConexao(double distancia, double limite, String especieA, String especieB,
                                   boolean modoAmplo, double saude, double patamar,
                                   boolean invasoraA, boolean invasoraB) {
                int especieIdA = 1;
                int especieIdB = especieA.equals(especieB) ? 1 : 2;

                List<Observation> observacoes = Arrays.asList(
                    new Observation(1, especieIdA, 0, 0, saude, invasoraA),
                    new Observation(2, especieIdB, distancia, 0, saude, invasoraB)
                );

                List<String> conexoes = bioClusterManager.processarClusters(
                    observacoes, limite, patamar, modoAmplo, 100
                );

                return !conexoes.isEmpty();
            }
        }
}
