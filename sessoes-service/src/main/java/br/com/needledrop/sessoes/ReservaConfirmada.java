package br.com.needledrop.sessoes;

public class ReservaConfirmada {
    private final Long reservaId;
    private final String nomeCliente;

    public ReservaConfirmada(Long reservaId, String nomeCliente) {
        this.reservaId = reservaId;
        this.nomeCliente = nomeCliente;
    }

    public Long getReservaId() {
        return reservaId;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }
}
