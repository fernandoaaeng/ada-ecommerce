package br.com.ada.ecommerce.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valorPadrao;
    private LocalDateTime dataCadastro;

    public Produto() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Produto(String nome, String descricao, BigDecimal valorPadrao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.valorPadrao = valorPadrao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(BigDecimal valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", valorPadrao=" + valorPadrao +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}