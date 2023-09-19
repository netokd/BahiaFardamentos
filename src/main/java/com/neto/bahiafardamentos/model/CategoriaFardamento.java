package com.neto.bahiafardamentos.model;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name = "categoria_tamanho", // Nome da tabela de associação
            joinColumns = @JoinColumn(name = "categoria_id"), // Chave estrangeira para CategoriaFardamento
            inverseJoinColumns = @JoinColumn(name = "tamanho_id") // Chave estrangeira para Tamanho
    )

    private Set<Tamanho> tamanhos = new HashSet<>();

    public CategoriaFardamento(Integer id, String nome, Set<Tamanho> tamanhos) {
        this.id = id;
        this.nome = nome;
        this.tamanhos = tamanhos;
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

    public Set<Tamanho> getTamanhos() {
        return tamanhos;
    }

    public void setTamanhos(Set<Tamanho> tamanhos) {
        this.tamanhos = tamanhos;
    }

    @Override
    public String toString() {
        return "CategoriaFardamento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", tamanhos=" + tamanhos +
                '}';
    }
}
