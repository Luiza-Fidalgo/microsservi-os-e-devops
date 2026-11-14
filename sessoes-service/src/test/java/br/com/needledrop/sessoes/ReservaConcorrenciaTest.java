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
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ReservaConcorrenciaTest {
    @Autowired
    private ReservaService reservaService;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

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

    @Test
    void apenasUmaReservaDevePassarQuandoRestaUmaVaga() throws Exception {
        Local local = localRepository.save(new Local("Local da concorrencia", "Rua Teste, 2"));
        Sessao sessao = sessaoRepository.save(new Sessao(1L, local,
                LocalDate.of(2026, 11, 29), LocalTime.of(20, 0), 1));
        Long id = sessao.getId();

        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> primeira = executor.submit(() -> tentarReservar(id, "Ana", inicio));
            Future<Boolean> segunda = executor.submit(() -> tentarReservar(id, "Bruno", inicio));
            inicio.countDown();

            int confirmadas = (primeira.get(10, TimeUnit.SECONDS) ? 1 : 0)
                    + (segunda.get(10, TimeUnit.SECONDS) ? 1 : 0);

            assertEquals(1, confirmadas);
            assertEquals(0, sessaoRepository.findById(id).orElseThrow().getVagasDisponiveis());
            assertEquals(1, reservaRepository.countBySessao_Id(id));
        } finally {
            executor.shutdownNow();
        }
    }
}
