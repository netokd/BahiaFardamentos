package com.neto.bahiafardamentos.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Tamanho {
    @Id
    @SequenceGenerator(
            name = "tamanho_id_sequence",
            sequenceName = "tamanho_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tamanho_id_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @ManyToMany(mappedBy = "tamanhos")
    @JsonIgnore
    private Set<CategoriaFardamento> categorias = new HashSet<>();



    public Tamanho(Integer id, String nome, Set<CategoriaFardamento> categorias) {
        this.id = id;
        this.nome = nome;
        this.categorias = categorias;
    }

    public Tamanho(String nome) {
        this.nome = nome;
    }

    public Tamanho() {
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

    public Set<CategoriaFardamento> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<CategoriaFardamento> categorias) {
        this.categorias = categorias;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tamanho tamanho = (Tamanho) o;
        return Objects.equals(id, tamanho.id) && Objects.equals(nome, tamanho.nome) && Objects.equals(categorias, tamanho.categorias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, categorias);
    }

    @Override
    public String toString() {
        return "Tamanho{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categorias=" + categorias.stream().map(CategoriaFardamento::getNome).collect(Collectors.toList()) +
                '}';
    }
}
