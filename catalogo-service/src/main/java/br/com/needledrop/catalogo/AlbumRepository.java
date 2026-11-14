package br.com.needledrop.catalogo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByTituloContainingIgnoreCase(String titulo);
}
