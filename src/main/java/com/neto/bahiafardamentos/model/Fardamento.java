package com.neto.bahiafardamentos.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Fardamento {
    @Id
    @SequenceGenerator(
            name = "fardamento_id_sequence",
            sequenceName = "fardamento_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fardamento_id_sequence"
    )
    private Integer id;

    @Column(nullable = false)
    private String nome;


    @ManyToOne
    private CategoriaFardamento categoria;

    @ManyToOne
    private Bandeira bandeira;

    public Fardamento() {
    }

    public Fardamento(Integer id, String nome, CategoriaFardamento categoria, Bandeira bandeira) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.bandeira = bandeira;
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

    public CategoriaFardamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFardamento categoria) {
        this.categoria = categoria;
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
        Fardamento that = (Fardamento) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(categoria, that.categoria) && Objects.equals(bandeira, that.bandeira);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, categoria, bandeira);
    }

    @Override
    public String toString() {
        return "Fardamento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", categoria=" + categoria +
                ", bandeira=" + bandeira +
                '}';
    }
}
