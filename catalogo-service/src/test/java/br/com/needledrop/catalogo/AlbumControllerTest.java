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
class AlbumControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarAlbuns() throws Exception {
        mockMvc.perform(get("/albuns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Kind of Blue"));
    }

    @Test
    void deveBuscarAlbumPorTitulo() throws Exception {
        mockMvc.perform(get("/albuns").param("titulo", "kind"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Kind of Blue"));
    }

    @Test
    void deveRetornar404ParaAlbumInexistente() throws Exception {
        mockMvc.perform(get("/albuns/999"))
                .andExpect(status().isNotFound());
    }
}
