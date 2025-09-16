package br.com.ada.ecommerce.models;

public enum StatusPedido {
    ABERTO("Aberto"),
    AGUARDANDO_PAGAMENTO("Aguardando pagamento"),
    PAGO("Pago"),
    FINALIZADO("Finalizado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}