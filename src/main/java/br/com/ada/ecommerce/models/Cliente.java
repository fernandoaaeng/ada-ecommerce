package br.com.ada.ecommerce.models;

import java.time.LocalDateTime;

public class Cliente {
    private Long id;
    private String nome;
    private String documento;
    private String email;
    private String telefone;
    private LocalDateTime dataCadastro;

    public Cliente() {
        this.dataCadastro = LocalDateTime.now();
    }

    public Cliente(String nome, String documento, String email, String telefone) {
        this();
        this.nome = nome;
        this.documento = documento;
        this.email = email;
        this.telefone = telefone;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", documento='" + documento + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}