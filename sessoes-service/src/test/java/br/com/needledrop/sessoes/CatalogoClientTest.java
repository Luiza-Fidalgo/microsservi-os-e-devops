package br.com.needledrop.sessoes;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CatalogoClientTest {
    @Test
    void deveReceberAlbumDoCatalogo() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 1L))
                .thenReturn(new AlbumInfo(1L, "Kind of Blue", 1959));
        CatalogoClient client = new CatalogoClient(restTemplate, "http://catalogo:8081");

        AlbumInfo album = client.buscarAlbum(1L);
        assertEquals("Kind of Blue", album.getTitulo());
    }

    @Test
    void deveRecusarAlbumInexistenteSemRepetir() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 999L))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        CatalogoClient client = new CatalogoClient(restTemplate, "http://catalogo:8081");

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> client.buscarAlbum(999L));
        assertEquals(400, erro.getStatusCode().value());
        verify(restTemplate, times(1)).getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 999L);
    }

    @Test
    void deveTentarDuasVezesQuandoCatalogoFalha() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 1L))
                .thenThrow(new ResourceAccessException("Sem conexao"));
        CatalogoClient client = new CatalogoClient(restTemplate, "http://catalogo:8081");

        ResponseStatusException erro = assertThrows(ResponseStatusException.class,
                () -> client.buscarAlbum(1L));
        assertEquals(503, erro.getStatusCode().value());
        verify(restTemplate, times(2)).getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 1L);
    }

    @Test
    void deveFuncionarNaSegundaTentativa() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 1L))
                .thenThrow(new ResourceAccessException("Falha temporaria"))
                .thenReturn(new AlbumInfo(1L, "Kind of Blue", 1959));
        CatalogoClient client = new CatalogoClient(restTemplate, "http://catalogo:8081");

        assertEquals("Kind of Blue", client.buscarAlbum(1L).getTitulo());
        verify(restTemplate, times(2)).getForObject("http://catalogo:8081/albuns/{id}", AlbumInfo.class, 1L);
    }
}
