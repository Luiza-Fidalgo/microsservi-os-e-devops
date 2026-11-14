package br.com.needledrop.sessoes;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {
    private final SessaoService sessaoService;

    public SessaoController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @GetMapping
    public List<Sessao> listar() {
        return sessaoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sessao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.of(sessaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Sessao> criar(@RequestBody NovaSessaoRequest dados) {
        Sessao sessao = sessaoService.criar(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessao);
    }
}
