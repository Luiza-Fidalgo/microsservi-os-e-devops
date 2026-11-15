package br.com.needledrop.sessoes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Este teste inicia um PostgreSQL separado, para nao depender do banco local.
@Testcontainers
@SpringBootTest
class ReservaPostgresIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configurarBanco(DynamicPropertyRegistry propriedades) {
        propriedades.add("spring.datasource.url", postgres::getJdbcUrl);
        propriedades.add("spring.datasource.username", postgres::getUsername);
        propriedades.add("spring.datasource.password", postgres::getPassword);
        propriedades.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        propriedades.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Test
    void deveSalvarReservaNoPostgres() {
        Sessao sessao = criarSessao(2);
        NovaReservaRequest dados = new NovaReservaRequest();
        dados.setNomeCliente("Ana");

        Reserva reserva = reservaService.reservar(sessao.getId(), dados);

        assertEquals("Ana", reservaRepository.findById(reserva.getId()).orElseThrow().getNomeCliente());
        assertEquals(1, sessaoRepository.findById(sessao.getId()).orElseThrow().getVagasDisponiveis());
    }

    @Test
    void naoDeveUltrapassarCapacidadeComDuasReservasAoMesmoTempo() throws Exception {
        Sessao sessao = criarSessao(1);
        Long sessaoId = sessao.getId();
        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<Boolean> ana = executor.submit(() -> tentarReservar(sessaoId, "Ana", inicio));
            Future<Boolean> bruno = executor.submit(() -> tentarReservar(sessaoId, "Bruno", inicio));
            inicio.countDown();

            int confirmadas = (ana.get(20, TimeUnit.SECONDS) ? 1 : 0)
                    + (bruno.get(20, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, confirmadas);
            assertEquals(1, reservaRepository.countBySessao_Id(sessaoId));
            assertEquals(0, sessaoRepository.findById(sessaoId).orElseThrow().getVagasDisponiveis());
        } finally {
            executor.shutdownNow();
        }
    }

    private Sessao criarSessao(int capacidade) {
        Local local = localRepository.save(new Local("Local de teste", "Rua Teste, 1"));
        return sessaoRepository.save(new Sessao(1L, local,
                LocalDate.of(2026, 11, 30), LocalTime.of(20, 0), capacidade));
    }

    private boolean tentarReservar(Long sessaoId, String nome, CountDownLatch inicio) throws Exception {
        inicio.await();
        NovaReservaRequest dados = new NovaReservaRequest();
        dados.setNomeCliente(nome);
        try {
            reservaService.reservar(sessaoId, dados);
            return true;
        } catch (ResponseStatusException erro) {
            if (erro.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                return false;
            }
            throw erro;
        }
    }
}
