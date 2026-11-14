package br.com.needledrop.sessoes;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SessaoService {
    private final SessaoRepository sessaoRepository;
    private final LocalRepository localRepository;

    public SessaoService(SessaoRepository sessaoRepository, LocalRepository localRepository) {
        this.sessaoRepository = sessaoRepository;
        this.localRepository = localRepository;
    }

    public List<Sessao> listar() {
        return sessaoRepository.findAll(Sort.by("id"));
    }

    public Optional<Sessao> buscarPorId(Long id) {
        return sessaoRepository.findById(id);
    }

    @Transactional
    public Sessao criar(NovaSessaoRequest dados) {
        if (dados.getAlbumId() == null || dados.getAlbumId() <= 0
                || dados.getLocalId() == null || dados.getLocalId() <= 0
                || dados.getData() == null || dados.getHorario() == null
                || dados.getCapacidade() == null || dados.getCapacidade() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da sessao invalidos");
        }

        Local local = localRepository.findById(dados.getLocalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Local nao encontrado"));

        Sessao sessao = new Sessao(dados.getAlbumId(), local, dados.getData(),
                dados.getHorario(), dados.getCapacidade());
        return sessaoRepository.save(sessao);
    }
}
