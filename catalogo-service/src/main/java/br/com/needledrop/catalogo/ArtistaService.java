package br.com.needledrop.catalogo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ArtistaService {
    private final List<Artista> artistas = Arrays.asList(
        new Artista(1L, "Miles Davis"),
        new Artista(2L, "Nina Simone")
    );

    public List<Artista> listar() {
        return artistas;
    }

    public Optional<Artista> buscarPorId(Long id) {
        return artistas.stream()
                .filter(artista -> artista.getId().equals(id))
                .findFirst();
    }
}
