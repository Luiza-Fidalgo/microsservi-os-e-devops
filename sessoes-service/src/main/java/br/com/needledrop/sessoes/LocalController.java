package br.com.needledrop.sessoes;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/locais")
public class LocalController {
    private final LocalRepository localRepository;

    public LocalController(LocalRepository localRepository) {
        this.localRepository = localRepository;
    }

    @GetMapping
    public List<Local> listar() {
        return localRepository.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Local> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(localRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<Local> criar(@RequestBody NovoLocalRequest dados) {
        if (dados.getNome() == null || dados.getNome().isBlank()
                || dados.getEndereco() == null || dados.getEndereco().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe nome e endereco");
        }
        Local local = localRepository.save(new Local(dados.getNome(), dados.getEndereco()));
        return ResponseEntity.status(HttpStatus.CREATED).body(local);
    }
}
