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
    private final CatalogoClient catalogoClient;

    public SessaoService(SessaoRepository sessaoRepository, LocalRepository localRepository,
                         CatalogoClient catalogoClient) {
        this.sessaoRepository = sessaoRepository;
        this.localRepository = localRepository;
        this.catalogoClient = catalogoClient;
    }

    public List<Sessao> listar() {
        return sessaoRepository.findAll(Sort.by("id"));
    }

    public Optional<Sessao> buscarPorId(Long id) {
        return sessaoRepository.findById(id);
    }

    public AlbumInfo consultarAlbum(Long sessaoId) {
        Sessao sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessao nao encontrada"));
        return catalogoClient.buscarAlbum(sessao.getAlbumId());
    }

    // Se o catalogo estiver fora, retorna a sessao sem os dados do album.
    public DetalhesSessao detalhes(Long id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessao nao encontrada"));
        try {
            AlbumInfo album = catalogoClient.buscarAlbum(sessao.getAlbumId());
            return new DetalhesSessao(sessao, album, null);
        } catch (ResponseStatusException erro) {
            if (erro.getStatusCode().value() == 503) {
                return new DetalhesSessao(sessao, null, "Dados do album indisponiveis no momento");
            }
            throw erro;
        }
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

        // Confere se o album existe no outro microsservico.
        catalogoClient.buscarAlbum(dados.getAlbumId());

        Sessao sessao = new Sessao(dados.getAlbumId(), local, dados.getData(),
                dados.getHorario(), dados.getCapacidade());
        return sessaoRepository.save(sessao);
    }
}
