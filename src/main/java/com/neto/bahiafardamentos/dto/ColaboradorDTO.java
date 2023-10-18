package com.neto.bahiafardamentos.dto;

import java.util.Date;

public class ColaboradorDTO {

    private String nome;
    private String cargo;
    private Date dataContratacao;
    private Date dataUltimoKitEnviado;
    private Integer postoId;

    // Construtores, getters e setters

    // Construtor vazio
    public ColaboradorDTO() {
    }

    // Construtor com todos os campos
    public ColaboradorDTO(String nome, String cargo, Date dataContratacao, Date dataUltimoKitEnviado, Integer postoId) {
        this.nome = nome;
        this.cargo = cargo;
        this.dataContratacao = dataContratacao;
        this.dataUltimoKitEnviado = dataUltimoKitEnviado;
        this.postoId = postoId;
    }

    // Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Date getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(Date dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public Date getDataUltimoKitEnviado() {
        return dataUltimoKitEnviado;
    }

    public void setDataUltimoKitEnviado(Date dataUltimoKitEnviado) {
        this.dataUltimoKitEnviado = dataUltimoKitEnviado;
    }

    public Integer getPostoId() {
        return postoId;
    }

    public void setPostoId(Integer postoId) {
        this.postoId = postoId;
    }
}
