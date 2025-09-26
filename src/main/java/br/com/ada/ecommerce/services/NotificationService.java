package br.com.ada.ecommerce.services;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.models.Pedido;

public class NotificationService {
    private final ExecutorService executorService;
    
    public NotificationService() {
        // Criar um pool de threads para notificaa?a?es assa?ncronas
        this.executorService = Executors.newFixedThreadPool(3);
    }
    
    // Ma?todo sa?ncrono (mantido para compatibilidade)
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
    
    // Ma?todo assa?ncrono usando threads
    public CompletableFuture<Void> notificarPedidoFinalizadoAsync(Cliente cliente, Pedido pedido) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Simular tempo de processamento (como envio de email/SMS)
                Thread.sleep(1000);
                System.out.println("\n[THREAD] Enviando notificacao de pedido finalizado...");
                
                System.out.println("\n" + "=".repeat(60));
                System.out.println("NOTIFICACAO ASSINCRONA - PEDIDO FINALIZADO");
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
                
                System.out.println("[THREAD] Notificacao enviada com sucesso!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[THREAD] Erro ao enviar notificacao: " + e.getMessage());
            }
        }, executorService);
    }

    // Ma?todo sa?ncrono (mantido para compatibilidade)
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
    
    // Ma?todo assa?ncrono usando threads
    public CompletableFuture<Void> notificarPagamentoConfirmadoAsync(Cliente cliente, Pedido pedido) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Simular tempo de processamento (como envio de email/SMS)
                Thread.sleep(1500);
                System.out.println("\n[THREAD] Enviando notificacao de pagamento confirmado...");
                
                System.out.println("\n" + "=".repeat(60));
                System.out.println("NOTIFICACAO ASSINCRONA - PAGAMENTO CONFIRMADO");
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
                
                System.out.println("[THREAD] Notificacao de pagamento enviada com sucesso!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[THREAD] Erro ao enviar notificacao: " + e.getMessage());
            }
        }, executorService);
    }

    // Ma?todo sa?ncrono (mantido para compatibilidade)
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
    
    // Ma?todo assa?ncrono usando threads
    public CompletableFuture<Void> notificarEntregaRealizadaAsync(Cliente cliente, Pedido pedido) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Simular tempo de processamento (como envio de email/SMS)
                Thread.sleep(2000);
                System.out.println("\n[THREAD] Enviando notificacao de entrega realizada...");
                
                System.out.println("\n" + "=".repeat(60));
                System.out.println("NOTIFICACAO ASSINCRONA - ENTREGA REALIZADA");
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
                
                System.out.println("[THREAD] Notificacao de entrega enviada com sucesso!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[THREAD] Erro ao enviar notificacao: " + e.getMessage());
            }
        }, executorService);
    }
    
    // Ma?todo para fechar o executor quando necessa?rio
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            System.out.println("[THREAD] NotificationService executor shutdown completed.");
        }
    }
}
