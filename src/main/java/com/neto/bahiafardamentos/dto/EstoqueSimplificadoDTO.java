package com.neto.bahiafardamentos.dto;

import java.util.Objects;

public class EstoqueSimplificadoDTO {
    private String fardamento;
    private Integer fardamentoId;
    private String tamanho;
    private Integer tamanhoId;
    private Integer quantidade;

    public EstoqueSimplificadoDTO() {
    }

    public EstoqueSimplificadoDTO(String fardamento, Integer fardamentoId, String tamanho, Integer tamanhoId, Integer quantidade) {
        this.fardamento = fardamento;
        this.fardamentoId = fardamentoId;
        this.tamanho = tamanho;
        this.tamanhoId = tamanhoId;
        this.quantidade = quantidade;
    }

    public String getFardamento() {
        return fardamento;
    }

    public void setFardamento(String fardamento) {
        this.fardamento = fardamento;
    }

    public Integer getFardamentoId() {
        return fardamentoId;
    }

    public void setFardamentoId(Integer fardamentoId) {
        this.fardamentoId = fardamentoId;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public Integer getTamanhoId() {
        return tamanhoId;
    }

    public void setTamanhoId(Integer tamanhoId) {
        this.tamanhoId = tamanhoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstoqueSimplificadoDTO that = (EstoqueSimplificadoDTO) o;
        return Objects.equals(fardamento, that.fardamento) && Objects.equals(fardamentoId, that.fardamentoId) && Objects.equals(tamanho, that.tamanho) && Objects.equals(tamanhoId, that.tamanhoId) && Objects.equals(quantidade, that.quantidade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fardamento, fardamentoId, tamanho, tamanhoId, quantidade);
    }

    @Override
    public String toString() {
        return "EstoqueSimplificadoDTO{" +
                "fardamento='" + fardamento + '\'' +
                ", fardamentoId=" + fardamentoId +
                ", tamanho='" + tamanho + '\'' +
                ", tamanhoId=" + tamanhoId +
                ", quantidade=" + quantidade +
                '}';
    }
}
