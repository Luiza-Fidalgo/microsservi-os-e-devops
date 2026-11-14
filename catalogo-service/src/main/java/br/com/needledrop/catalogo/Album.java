package br.com.needledrop.catalogo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Integer ano;

    @ManyToOne
    @JoinColumn(name = "artista_id")
    private Artista artista;

    @ManyToOne
    @JoinColumn(name = "genero_id")
    private Genero genero;

    protected Album() {
    }

    public Album(String titulo, Integer ano, Artista artista, Genero genero) {
        this.titulo = titulo;
        this.ano = ano;
        this.artista = artista;
        this.genero = genero;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public Integer getAno() {
        return ano;
    }

    public Artista getArtista() {
        return artista;
    }

    public Genero getGenero() {
        return genero;
    }
}
