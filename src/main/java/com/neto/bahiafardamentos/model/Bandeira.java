package com.neto.bahiafardamentos.model;


import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class  Bandeira {
    @Id
    @SequenceGenerator(
            name = "bandeira_id_sequence",
            sequenceName = "bandeira_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "bandeira_id_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String nome;

    public Bandeira(Integer id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Bandeira() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bandeira bandeira = (Bandeira) o;
        return Objects.equals(id, bandeira.id) && Objects.equals(nome, bandeira.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Bandeira{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
