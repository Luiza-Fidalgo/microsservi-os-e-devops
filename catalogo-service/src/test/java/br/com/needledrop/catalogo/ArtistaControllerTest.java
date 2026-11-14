package br.com.needledrop.catalogo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ArtistaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarArtistas() throws Exception {
        mockMvc.perform(get("/artistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Miles Davis"));
    }

    @Test
    void deveRetornar404ParaArtistaInexistente() throws Exception {
        mockMvc.perform(get("/artistas/999"))
                .andExpect(status().isNotFound());
    }
}
