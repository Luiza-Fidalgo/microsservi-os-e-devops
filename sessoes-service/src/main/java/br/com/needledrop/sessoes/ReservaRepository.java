package br.com.needledrop.sessoes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findBySessao_IdOrderByIdAsc(Long sessaoId);

    long countBySessao_Id(Long sessaoId);
}
