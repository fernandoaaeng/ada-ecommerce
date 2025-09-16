package br.com.ada.ecommerce.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {
    private Long id;
    private Long clienteId;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataFinalizacao;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataEntrega;

    public Pedido() {
        this.status = StatusPedido.ABERTO;
        this.dataCriacao = LocalDateTime.now();
        this.valorTotal = BigDecimal.ZERO;
    }

    public Pedido(Long clienteId) {
        this();
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", status=" + status +
                ", valorTotal=" + valorTotal +
                ", dataCriacao=" + dataCriacao +
                ", dataFinalizacao=" + dataFinalizacao +
                ", dataPagamento=" + dataPagamento +
                ", dataEntrega=" + dataEntrega +
                '}';
    }
}