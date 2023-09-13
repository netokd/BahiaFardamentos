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


    @ManyToOne
    @JoinColumn
    private CategoriaFardamento categoriaFardamento;

    public Gastos(Integer id, Double valor, LocalDate dataEntrada, CategoriaFardamento categoriaFardamento) {
        this.id = id;
        this.valor = valor;
        this.dataEntrada = dataEntrada;
        this.categoriaFardamento = categoriaFardamento;
    }

    public Gastos() {
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

    public CategoriaFardamento getCategoriaFardamento() {
        return categoriaFardamento;
    }

    public void setCategoriaFardamento(CategoriaFardamento categoriaFardamento) {
        this.categoriaFardamento = categoriaFardamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gastos gastos = (Gastos) o;
        return Objects.equals(id, gastos.id) && Objects.equals(valor, gastos.valor) && Objects.equals(dataEntrada, gastos.dataEntrada) && Objects.equals(categoriaFardamento, gastos.categoriaFardamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, valor, dataEntrada, categoriaFardamento);
    }

    @Override
    public String toString() {
        return "Gastos{" +
                "id=" + id +
                ", valor=" + valor +
                ", dataEntrada=" + dataEntrada +
                ", categoriaFardamento=" + categoriaFardamento +
                '}';
    }
}
