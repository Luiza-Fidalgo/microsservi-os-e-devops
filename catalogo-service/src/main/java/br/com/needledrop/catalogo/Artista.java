package br.com.needledrop.catalogo;

public class Artista {
    private Long id;
    private String nome;

    public Artista(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
