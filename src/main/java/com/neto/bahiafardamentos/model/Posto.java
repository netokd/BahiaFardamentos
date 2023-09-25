package com.neto.bahiafardamentos.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Posto {

    @Id
    @SequenceGenerator(
            name = "posto_id_sequence",
            sequenceName = "posto_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "posto_id_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @ManyToOne
    private Bandeira bandeira;

    public Posto(Integer id, String nome, String endereco, Bandeira bandeira) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.bandeira = bandeira;
    }

    public Posto() {
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



    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public void setBandeira(Bandeira bandeira) {
        this.bandeira = bandeira;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posto posto = (Posto) o;
        return Objects.equals(id, posto.id) && Objects.equals(nome, posto.nome) && Objects.equals(endereco, posto.endereco) && Objects.equals(bandeira, posto.bandeira);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, endereco, bandeira);
    }

    @Override
    public String toString() {
        return "posto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", bandeira=" + bandeira +
                '}';
    }
}
