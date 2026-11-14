package br.com.needledrop.sessoes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CatalogoClient {
    private final RestTemplate restTemplate;
    private final String catalogoUrl;

    public CatalogoClient(RestTemplate restTemplate, @Value("${catalogo.url}") String catalogoUrl) {
        this.restTemplate = restTemplate;
        this.catalogoUrl = catalogoUrl;
    }

    public AlbumInfo buscarAlbum(Long id) {
        // Tenta uma segunda vez se houver falha temporaria na chamada HTTP.
        for (int tentativa = 1; tentativa <= 2; tentativa++) {
            try {
                AlbumInfo album = restTemplate.getForObject(
                        catalogoUrl + "/albuns/{id}", AlbumInfo.class, id);
                if (album == null || album.getId() == null) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Resposta invalida do catalogo");
                }
                return album;
            } catch (HttpClientErrorException erro) {
                if (erro.getStatusCode().value() == 404) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Album nao encontrado no catalogo");
                }
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Falha na consulta ao catalogo");
            } catch (RestClientException erro) {
                if (tentativa == 2) {
                    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalogo indisponivel");
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Catalogo indisponivel");
    }
}
