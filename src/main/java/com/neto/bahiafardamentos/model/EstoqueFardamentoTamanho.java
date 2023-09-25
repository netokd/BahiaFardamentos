package com.neto.bahiafardamentos.model;


import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class EstoqueFardamentoTamanho {

    @Id
    @SequenceGenerator(
            name = "estoque_id_sequence",
            sequenceName = "estoque_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "estoque_id_sequence"
    )
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "fardamento_id", referencedColumnName = "id")
    private Fardamento fardamento;
    @ManyToOne
    @JoinColumn(name = "tamanho_id", referencedColumnName = "id")
    private Tamanho tamanho;

    private Integer quantidade;


    public EstoqueFardamentoTamanho() {
    }

    public EstoqueFardamentoTamanho(Integer id, Fardamento fardamento, Tamanho tamanho, Integer quantidade) {
        this.id = id;
        this.fardamento = fardamento;
        this.tamanho = tamanho;
        this.quantidade = quantidade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fardamento getFardamento() {
        return fardamento;
    }

    public void setFardamento(Fardamento fardamento) {
        this.fardamento = fardamento;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public void setTamanho(Tamanho tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstoqueFardamentoTamanho that = (EstoqueFardamentoTamanho) o;
        return Objects.equals(id, that.id) && Objects.equals(fardamento, that.fardamento) && Objects.equals(tamanho, that.tamanho) && Objects.equals(quantidade, that.quantidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fardamento, tamanho, quantidade);
    }

    @Override
    public String toString() {
        return "EstoqueFardamentoTamanho{" +
                "id=" + id +
                ", fardamento=" + fardamento +
                ", tamanho=" + tamanho +
                ", quantidade=" + quantidade +
                '}';
    }
}
