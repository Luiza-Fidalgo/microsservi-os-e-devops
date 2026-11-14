package br.com.needledrop.sessoes;

public class DetalhesSessao {
    private final Sessao sessao;
    private final AlbumInfo album;
    private final String aviso;

    public DetalhesSessao(Sessao sessao, AlbumInfo album, String aviso) {
        this.sessao = sessao;
        this.album = album;
        this.aviso = aviso;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public AlbumInfo getAlbum() {
        return album;
    }

    public String getAviso() {
        return aviso;
    }
}
