package br.com.needledrop.sessoes;

import java.time.LocalDate;
import java.time.LocalTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Sessao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long albumId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "local_id")
    private Local local;

    private LocalDate data;
    private LocalTime horario;
    private Integer capacidade;
    private Integer vagasDisponiveis;

    protected Sessao() {
    }

    public Sessao(Long albumId, Local local, LocalDate data, LocalTime horario, Integer capacidade) {
        this.albumId = albumId;
        this.local = local;
        this.data = data;
        this.horario = horario;
        this.capacidade = capacidade;
        this.vagasDisponiveis = capacidade;
    }

    public Long getId() {
        return id;
    }

    public Long getAlbumId() {
        return albumId;
    }

    public Local getLocal() {
        return local;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void diminuirVaga() {
        vagasDisponiveis = vagasDisponiveis - 1;
    }
}
