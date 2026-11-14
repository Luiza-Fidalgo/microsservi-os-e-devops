package br.com.needledrop.catalogo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {
    private final ArtistaRepository artistaRepository;
    private final GeneroRepository generoRepository;
    private final AlbumRepository albumRepository;

    public CargaInicial(ArtistaRepository artistaRepository, GeneroRepository generoRepository,
                        AlbumRepository albumRepository) {
        this.artistaRepository = artistaRepository;
        this.generoRepository = generoRepository;
        this.albumRepository = albumRepository;
    }

    @Override
    public void run(String... args) {
        if (artistaRepository.count() > 0) {
            return;
        }

        Artista miles = artistaRepository.save(new Artista("Miles Davis"));
        Artista nina = artistaRepository.save(new Artista("Nina Simone"));

        Genero jazz = generoRepository.save(new Genero("Jazz"));
        Genero soul = generoRepository.save(new Genero("Soul"));

        albumRepository.save(new Album("Kind of Blue", 1959, miles, jazz));
        albumRepository.save(new Album("I Put a Spell on You", 1965, nina, soul));
    }
}
