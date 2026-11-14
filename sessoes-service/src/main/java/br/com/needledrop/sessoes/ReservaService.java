package br.com.needledrop.sessoes;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservaService {
    private final SessaoRepository sessaoRepository;
    private final ReservaRepository reservaRepository;

    public ReservaService(SessaoRepository sessaoRepository, ReservaRepository reservaRepository) {
        this.sessaoRepository = sessaoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Reserva reservar(Long sessaoId, NovaReservaRequest dados) {
        if (dados == null || dados.getNomeCliente() == null || dados.getNomeCliente().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome do cliente");
        }

        // Bloqueia a sessao ate a reserva ser concluida.
        Sessao sessao = sessaoRepository.buscarParaReserva(sessaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessao nao encontrada"));

        if (sessao.getVagasDisponiveis() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sessao sem vagas");
        }

        sessao.diminuirVaga();
        sessaoRepository.save(sessao);

        Reserva reserva = new Reserva(dados.getNomeCliente().trim(), sessao);
        return reservaRepository.save(reserva);
    }

    public List<Reserva> listar(Long sessaoId) {
        if (!sessaoRepository.existsById(sessaoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessao nao encontrada");
        }
        return reservaRepository.findBySessao_IdOrderByIdAsc(sessaoId);
    }
}
