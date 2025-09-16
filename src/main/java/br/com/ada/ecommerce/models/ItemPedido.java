package br.com.ada.ecommerce.models;

import java.math.BigDecimal;

public class ItemPedido {
    private Long id;
    private Long pedidoId;
    private Long produtoId;
    private String nomeProduto;
    private Integer quantidade;
    private BigDecimal precoVenda;

    public ItemPedido() {}

    public ItemPedido(Long pedidoId, Long produtoId, String nomeProduto, Integer quantidade, BigDecimal precoVenda) {
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public BigDecimal getSubtotal() {
        return precoVenda.multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public String toString() {
        return "ItemPedido{" +
                "id=" + id +
                ", pedidoId=" + pedidoId +
                ", produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoVenda=" + precoVenda +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}