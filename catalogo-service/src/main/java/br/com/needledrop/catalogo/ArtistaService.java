package br.com.needledrop.catalogo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ArtistaService {
    private final ArtistaRepository artistaRepository;

    public ArtistaService(ArtistaRepository artistaRepository) {
        this.artistaRepository = artistaRepository;
    }

    public List<Artista> listar() {
        return artistaRepository.findAll(Sort.by("id"));
    }

    public Optional<Artista> buscarPorId(Long id) {
        return artistaRepository.findById(id);
    }
}
