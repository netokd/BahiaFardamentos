package com.neto.bahiafardamentos.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Gastos {
    @Id
    @SequenceGenerator(
            name = "colaborador_id_sequence",
            sequenceName = "colaborador_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "colaborador_id_sequence"
    )
    private Integer id;

    private Double valor;
    private LocalDate dataEntrada;
    private String descricao;

    public Gastos() {
    }

    public Gastos(Integer id, Double valor, LocalDate dataEntrada, String descricao) {
        this.id = id;
        this.valor = valor;
        this.dataEntrada = dataEntrada;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gastos gastos = (Gastos) o;
        return Objects.equals(id, gastos.id) && Objects.equals(valor, gastos.valor) && Objects.equals(dataEntrada, gastos.dataEntrada) && Objects.equals(descricao, gastos.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, dataEntrada, descricao);
    }

    @Override
    public String toString() {
        return "Gastos{" +
                "id=" + id +
                ", valor=" + valor +
                ", dataEntrada=" + dataEntrada +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
