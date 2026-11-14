package br.com.needledrop.catalogo;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/albuns")
public class AlbumController {
    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<Album> listar(@RequestParam(required = false) String titulo) {
        return albumService.listar(titulo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(albumService.buscarPorId(id));
    }
}
