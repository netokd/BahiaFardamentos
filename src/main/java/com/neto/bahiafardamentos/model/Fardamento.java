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
    public Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne
    private CategoriaFardamento categoria;

    @ManyToOne
    private  Tamanho tamanho;

    @ManyToOne
    private Bandeira bandeira;

    public Fardamento(Integer id, String nome, Integer quantidade, CategoriaFardamento categoria, Tamanho tamanho, Bandeira bandeira) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.bandeira = bandeira;
    }

    public Fardamento() {
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public CategoriaFardamento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaFardamento categoria) {
        this.categoria = categoria;
    }

    public Tamanho getTamanho() {
        return tamanho;
    }

    public void setTamanho(Tamanho tamanho) {
        this.tamanho = tamanho;
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
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(quantidade, that.quantidade) && Objects.equals(categoria, that.categoria) && Objects.equals(tamanho, that.tamanho) && Objects.equals(bandeira, that.bandeira);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, quantidade, categoria, tamanho, bandeira);
    }

    @Override
    public String toString() {
        return "Fardamento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", quantidade=" + quantidade +
                ", categoria=" + categoria +
                ", tamanho=" + tamanho +
                ", bandeira=" + bandeira +
                '}';
    }
}
