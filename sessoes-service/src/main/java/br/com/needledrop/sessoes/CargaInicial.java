package br.com.needledrop.sessoes;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {
    private final LocalRepository localRepository;
    private final SessaoRepository sessaoRepository;

    public CargaInicial(LocalRepository localRepository, SessaoRepository sessaoRepository) {
        this.localRepository = localRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @Override
    public void run(String... args) {
        if (localRepository.count() == 0) {
            localRepository.save(new Local("Casa Sonora", "Rua da Musica, 100"));
            localRepository.save(new Local("Espaco Vinil", "Avenida Central, 20"));
        }

        if (sessaoRepository.count() == 0) {
            Local primeiroLocal = localRepository.findAll(Sort.by("id")).get(0);
            sessaoRepository.save(new Sessao(1L, primeiroLocal,
                    LocalDate.of(2026, 11, 25), LocalTime.of(20, 0), 30));
        }
    }
}
