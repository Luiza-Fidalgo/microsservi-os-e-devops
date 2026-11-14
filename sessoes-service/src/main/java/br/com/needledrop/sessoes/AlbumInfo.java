package br.com.needledrop.sessoes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumInfo {
    private Long id;
    private String titulo;
    private Integer ano;

    public AlbumInfo() {
    }

    public AlbumInfo(Long id, String titulo, Integer ano) {
        this.id = id;
        this.titulo = titulo;
        this.ano = ano;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
