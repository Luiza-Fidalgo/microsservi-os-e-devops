package br.com.needledrop.sessoes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SessaoApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarLocais() throws Exception {
        mockMvc.perform(get("/locais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Casa Sonora"));
    }

    @Test
    void deveConsultarSessao() throws Exception {
        mockMvc.perform(get("/sessoes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albumId").value(1))
                .andExpect(jsonPath("$.capacidade").value(30))
                .andExpect(jsonPath("$.vagasDisponiveis").value(30));
    }

    @Test
    void deveCriarSessaoComVagasIniciais() throws Exception {
        String json = """
                {"albumId":1,"localId":1,"data":"2026-11-27","horario":"18:30","capacidade":5}
                """;
        mockMvc.perform(post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.capacidade").value(5))
                .andExpect(jsonPath("$.vagasDisponiveis").value(5));
    }

    @Test
    void deveRecusarCapacidadeZero() throws Exception {
        String json = """
                {"albumId":1,"localId":1,"data":"2026-11-27","horario":"18:30","capacidade":0}
                """;
        mockMvc.perform(post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRecusarLocalInexistente() throws Exception {
        String json = """
                {"albumId":1,"localId":999,"data":"2026-11-27","horario":"18:30","capacidade":5}
                """;
        mockMvc.perform(post("/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar404ParaSessaoInexistente() throws Exception {
        mockMvc.perform(get("/sessoes/999"))
                .andExpect(status().isNotFound());
    }
}
