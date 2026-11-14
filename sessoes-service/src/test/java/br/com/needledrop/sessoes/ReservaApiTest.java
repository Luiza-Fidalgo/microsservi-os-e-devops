package br.com.needledrop.sessoes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocalRepository localRepository;

    @Autowired
    private SessaoRepository sessaoRepository;

    private Sessao criarSessao(int vagas) {
        Local local = localRepository.save(new Local("Local do teste", "Rua Teste, 1"));
        return sessaoRepository.save(new Sessao(1L, local,
                LocalDate.of(2026, 11, 28), LocalTime.of(19, 0), vagas));
    }

    @Test
    void deveCriarReservaEDiminuirVaga() throws Exception {
        Sessao sessao = criarSessao(2);

        mockMvc.perform(post("/sessoes/" + sessao.getId() + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCliente\":\"Ana\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCliente").value("Ana"));

        mockMvc.perform(get("/sessoes/" + sessao.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vagasDisponiveis").value(1));

        mockMvc.perform(get("/sessoes/" + sessao.getId() + "/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCliente").value("Ana"));
    }

    @Test
    void deveRecusarReservaQuandoNaoHaVagas() throws Exception {
        Sessao sessao = criarSessao(1);
        String json = "{\"nomeCliente\":\"Ana\"}";
        mockMvc.perform(post("/sessoes/" + sessao.getId() + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/sessoes/" + sessao.getId() + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRecusarNomeVazio() throws Exception {
        Sessao sessao = criarSessao(2);
        mockMvc.perform(post("/sessoes/" + sessao.getId() + "/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCliente\":\" \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404ParaSessaoInexistente() throws Exception {
        mockMvc.perform(post("/sessoes/999999/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCliente\":\"Ana\"}"))
                .andExpect(status().isNotFound());
    }
}
