package com.neto.bahiafardamentos.model;


import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class CategoriaFardamento {

    @Id
    @SequenceGenerator(
            name = "categoria_fardamento_id_sequence",
            sequenceName = "categoria_fardamento_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "categoria_fardamento_id_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tamanho;

    public CategoriaFardamento(Integer id, String nome, String tamanho) {
        this.id = id;
        this.nome = nome;
        this.tamanho = tamanho;
    }

    public CategoriaFardamento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaFardamento that = (CategoriaFardamento) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(tamanho, that.tamanho);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, tamanho);
    }

    @Override
    public String toString() {
        return "CategoriaFardamento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tamanho='" + tamanho + '\'' +
                '}';
    }
}
