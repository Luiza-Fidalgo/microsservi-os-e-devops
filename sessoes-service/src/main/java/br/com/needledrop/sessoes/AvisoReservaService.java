package br.com.needledrop.sessoes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class AvisoReservaService {
    private static final Logger logger = LoggerFactory.getLogger(AvisoReservaService.class);

    // Executa depois que a reserva foi salva, sem bloquear a resposta HTTP.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void registrarAviso(ReservaConfirmada evento) {
        logger.info("Reserva {} confirmada para {}", evento.getReservaId(), evento.getNomeCliente());
    }
}
