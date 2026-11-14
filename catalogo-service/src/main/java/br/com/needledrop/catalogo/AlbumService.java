package br.com.needledrop.catalogo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class AlbumService {
    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<Album> listar(String titulo) {
        if (titulo != null && !titulo.isBlank()) {
            return albumRepository.findByTituloContainingIgnoreCase(titulo);
        }
        return albumRepository.findAll(Sort.by("id"));
    }

    public Optional<Album> buscarPorId(Long id) {
        return albumRepository.findById(id);
    }
}
