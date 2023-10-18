package com.neto.bahiafardamentos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@Entity
public class Colaborador {

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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "A data de contratação não pode ser nula")
    @Past(message = "A data de contratação deve estar no passado")
    private Date dataContratacao;

    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dataUltimoKitEnviado;
    @Column(nullable = false)
    private String cargo;

    @ManyToOne
    private Posto posto;




    public Colaborador() {
    }

    public Colaborador(Integer id, String nome, Date dataContratacao, Date dataUltimoKitEnviado, String cargo, Posto posto) {
        this.id = id;
        this.nome = nome;
        this.dataContratacao = dataContratacao;
        this.dataUltimoKitEnviado = dataUltimoKitEnviado;
        this.cargo = cargo;
        this.posto = posto;
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Posto getPosto() {
        return posto;
    }

    public void setPosto(Posto posto) {
        this.posto = posto;
    }

    public String getDataContratacaoString() {
        if (this.dataContratacao != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(this.dataContratacao);
        }
        return null;
    }

    public void setDataContratacaoString(String dataContratacaoString) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.dataContratacao = dateFormat.parse(dataContratacaoString);
    }

    public String getDataUltimoKitEnviadoString() {
        if (this.dataUltimoKitEnviado != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(this.dataUltimoKitEnviado);
        }
        return null;
    }

    public String getDataUltimoKitEnviadoStringFormatted() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return (this.dataUltimoKitEnviado != null) ? dateFormat.format(this.dataUltimoKitEnviado) : null;
    }

    public void setDataUltimoKitEnviadoString(String dataUltimoKitEnviadoString) throws Exception {
        if (dataUltimoKitEnviadoString != null && !dataUltimoKitEnviadoString.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.dataUltimoKitEnviado = dateFormat.parse(dataUltimoKitEnviadoString);
        } else {
            this.dataUltimoKitEnviado = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Colaborador that = (Colaborador) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(dataContratacao, that.dataContratacao) && Objects.equals(dataUltimoKitEnviado, that.dataUltimoKitEnviado) && Objects.equals(cargo, that.cargo) && Objects.equals(posto, that.posto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, dataContratacao, dataUltimoKitEnviado, cargo, posto);
    }

    @Override
    public String toString() {
        return "Colaborador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataContratacao=" + dataContratacao +
                ", dataUltimoKitEnviado=" + dataUltimoKitEnviado +
                ", cargo='" + cargo + '\'' +
                ", posto=" + posto +
                '}';
    }
}
