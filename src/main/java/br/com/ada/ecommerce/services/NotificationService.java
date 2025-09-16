package br.com.ada.ecommerce.services;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.models.Pedido;

public class NotificationService {
    
    public void notificarPedidoFinalizado(Cliente cliente, Pedido pedido) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("NOTIFICACAO PARA O CLIENTE");
        System.out.println("=".repeat(60));
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
        System.out.println("\nPEDIDO FINALIZADO");
        System.out.println("Pedido #" + pedido.getId() + " foi finalizado com sucesso!");
        System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
        System.out.println("Status: " + pedido.getStatus().getDescricao());
        System.out.println("Data: " + pedido.getDataFinalizacao());
        System.out.println("\nAGUARDANDO PAGAMENTO");
        System.out.println("Por favor, realize o pagamento para que possamos processar seu pedido.");
        System.out.println("=".repeat(60));
    }

    public void notificarPagamentoConfirmado(Cliente cliente, Pedido pedido) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("NOTIFICACAO PARA O CLIENTE");
        System.out.println("=".repeat(60));
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
        System.out.println("\nPAGAMENTO CONFIRMADO");
        System.out.println("Pedido #" + pedido.getId() + " - Pagamento confirmado!");
        System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
        System.out.println("Status: " + pedido.getStatus().getDescricao());
        System.out.println("Data do Pagamento: " + pedido.getDataPagamento());
        System.out.println("\nPREPARANDO ENTREGA");
        System.out.println("Seu pedido esta sendo preparado para entrega.");
        System.out.println("=".repeat(60));
    }

    public void notificarEntregaRealizada(Cliente cliente, Pedido pedido) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("NOTIFICACAO PARA O CLIENTE");
        System.out.println("=".repeat(60));
        System.out.println("Cliente: " + cliente.getNome());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
        System.out.println("\nENTREGA REALIZADA");
        System.out.println("Pedido #" + pedido.getId() + " - Entrega realizada com sucesso!");
        System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
        System.out.println("Status: " + pedido.getStatus().getDescricao());
        System.out.println("Data da Entrega: " + pedido.getDataEntrega());
        System.out.println("\nOBRIGADO PELA PREFERENCIA!");
        System.out.println("Esperamos que tenha gostado da sua compra.");
        System.out.println("Volte sempre!");
        System.out.println("=".repeat(60));
    }
}