package br.com.needledrop.sessoes;

import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessoes")
public class SessaoController {
    // As sessoes ainda serao implementadas no proximo bloco.
    @GetMapping
    public List<String> listar() {
        return Collections.emptyList();
    }
}
