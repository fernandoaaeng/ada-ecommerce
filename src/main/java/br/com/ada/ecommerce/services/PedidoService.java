package br.com.ada.ecommerce.services;

import br.com.ada.ecommerce.models.*;
import br.com.ada.ecommerce.repositories.ItemPedidoRepository;
import br.com.ada.ecommerce.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final NotificationService notificationService;
    private final ExecutorService backgroundExecutor;

    public PedidoService() {
        this.pedidoRepository = new PedidoRepository();
        this.itemPedidoRepository = new ItemPedidoRepository();
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
        this.notificationService = new NotificationService();
        // Pool de threads para processamento em background
        this.backgroundExecutor = Executors.newFixedThreadPool(2);
    }

    public Pedido criarPedido(Long clienteId) {
        Optional<Cliente> cliente = clienteService.buscarClientePorId(clienteId);
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }

        Pedido pedido = new Pedido(clienteId);
        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPedidoPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> buscarPedidosPorCliente(Long clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public List<Pedido> buscarPedidosPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    public List<Pedido> buscarPedidosPorValorTotalMaiorQue(BigDecimal valor) {
        return pedidoRepository.findByValorTotalMaiorQue(valor);
    }

    public List<Pedido> buscarPedidosPorValorTotalMenorQue(BigDecimal valor) {
        return pedidoRepository.findByValorTotalMenorQue(valor);
    }

    public List<Pedido> buscarPedidosPorValorTotalEntre(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return pedidoRepository.findByValorTotalEntre(valorMinimo, valorMaximo);
    }

    public ItemPedido adicionarItemAoPedido(Long pedidoId, Long produtoId, Integer quantidade, BigDecimal precoVenda) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido nao encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new IllegalArgumentException("Apenas pedidos com status 'Aberto' podem ter itens adicionados.");
        }

        Optional<Produto> produto = produtoService.buscarProdutoPorId(produtoId);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto nao encontrado.");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preco de venda deve ser maior ou igual a zero.");
        }

        ItemPedido item = new ItemPedido(pedidoId, produtoId, produto.get().getNome(), quantidade, precoVenda);
        return itemPedidoRepository.save(item);
    }

    public boolean removerItemDoPedido(Long itemId) {
        Optional<ItemPedido> itemOpt = itemPedidoRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            return false;
        }

        ItemPedido item = itemOpt.get();
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(item.getPedidoId());
        if (pedidoOpt.isEmpty() || pedidoOpt.get().getStatus() != StatusPedido.ABERTO) {
            throw new IllegalArgumentException("Apenas itens de pedidos com status 'Aberto' podem ser removidos.");
        }

        return itemPedidoRepository.delete(itemId);
    }

    public ItemPedido alterarQuantidadeItem(Long itemId, Integer novaQuantidade) {
        Optional<ItemPedido> itemOpt = itemPedidoRepository.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Item nao encontrado.");
        }

        ItemPedido item = itemOpt.get();
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(item.getPedidoId());
        if (pedidoOpt.isEmpty() || pedidoOpt.get().getStatus() != StatusPedido.ABERTO) {
            throw new IllegalArgumentException("Apenas itens de pedidos com status 'Aberto' podem ser alterados.");
        }

        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }

        item.setQuantidade(novaQuantidade);
        return itemPedidoRepository.update(item);
    }

    public Pedido finalizarPedido(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido nao encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            throw new IllegalArgumentException("Apenas pedidos com status 'Aberto' podem ser finalizados.");
        }

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedidoId);
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve conter pelo menos um item para ser finalizado.");
        }

        BigDecimal valorTotal = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor total deve ser maior que zero.");
        }

        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setValorTotal(valorTotal);
        pedido.setDataFinalizacao(LocalDateTime.now());
        pedidoRepository.update(pedido);

        Cliente cliente = clienteService.buscarClientePorId(pedido.getClienteId()).get();
        
        // Notificacao sincrona (para feedback imediato)
        notificationService.notificarPedidoFinalizado(cliente, pedido);
        
        // Notificacao assincrona (para processamento em background)
        notificationService.notificarPedidoFinalizadoAsync(cliente, pedido)
            .thenRun(() -> System.out.println("[SISTEMA] Notificacao de pedido finalizado processada em background"));

        return pedido;
    }

    public Pedido realizarPagamento(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido nao encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalArgumentException("Apenas pedidos com status 'Aguardando pagamento' podem ter pagamento realizado.");
        }

        pedido.setStatus(StatusPedido.PAGO);
        pedido.setDataPagamento(LocalDateTime.now());
        pedidoRepository.update(pedido);

        Cliente cliente = clienteService.buscarClientePorId(pedido.getClienteId()).get();
        
        // Notificacao sincrona (para feedback imediato)
        notificationService.notificarPagamentoConfirmado(cliente, pedido);
        
        // Notificacao assincrona (para processamento em background)
        notificationService.notificarPagamentoConfirmadoAsync(cliente, pedido)
            .thenRun(() -> System.out.println("[SISTEMA] Notificacao de pagamento confirmado processada em background"));

        return pedido;
    }

    public Pedido realizarEntrega(Long pedidoId) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("Pedido nao encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.PAGO) {
            throw new IllegalArgumentException("Apenas pedidos com status 'Pago' podem ter entrega realizada.");
        }

        pedido.setStatus(StatusPedido.FINALIZADO);
        pedido.setDataEntrega(LocalDateTime.now());
        pedidoRepository.update(pedido);

        Cliente cliente = clienteService.buscarClientePorId(pedido.getClienteId()).get();
        
        // Notificacao sincrona (para feedback imediato)
        notificationService.notificarEntregaRealizada(cliente, pedido);
        
        // Notificacao assincrona (para processamento em background)
        notificationService.notificarEntregaRealizadaAsync(cliente, pedido)
            .thenRun(() -> System.out.println("[SISTEMA] Notificacao de entrega realizada processada em background"));

        return pedido;
    }

    public List<ItemPedido> listarItensDoPedido(Long pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }

    public BigDecimal calcularValorTotalPedido(Long pedidoId) {
        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedidoId);
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // ===== METODOS ASSINCRONOS PARA PROCESSAMENTO EM BACKGROUND =====
    
    /**
     * Processa pagamento em background usando threads.
     * Simula o processamento assincrono de pagamentos.
     */
    public CompletableFuture<Void> processarPagamentoAsync(Long pedidoId) {
        return CompletableFuture.runAsync(() -> {
            try {
                System.out.println("\n[BACKGROUND] Iniciando processamento de pagamento para pedido #" + pedidoId);
                
                // Simular tempo de processamento do gateway de pagamento
                Thread.sleep(2000);
                
                Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    
                    System.out.println("[BACKGROUND] Processando pagamento de R$ " + 
                        String.format("%.2f", pedido.getValorTotal()) + "...");
                    
                    Thread.sleep(1000); // Simular comunicacao com gateway
                    
                    System.out.println("[BACKGROUND] Pagamento processado com sucesso para pedido #" + pedidoId);
                    
                    // Enviar notificacao assincrona
                    notificationService.notificarPagamentoConfirmadoAsync(
                        clienteService.buscarClientePorId(pedido.getClienteId()).orElse(null), 
                        pedido
                    );
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[BACKGROUND] Processamento de pagamento interrompido para pedido #" + pedidoId);
            } catch (Exception e) {
                System.err.println("[BACKGROUND] Erro ao processar pagamento para pedido #" + pedidoId + ": " + e.getMessage());
            }
        }, backgroundExecutor);
    }
    
    /**
     * Processa preparaa?a?o de pedidos em background.
     * Simula a preparaa?a?o de pedidos para entrega.
     */
    public CompletableFuture<Void> prepararPedidoParaEntregaAsync(Long pedidoId) {
        return CompletableFuture.runAsync(() -> {
            try {
                System.out.println("\n[BACKGROUND] Iniciando preparaa?a?o do pedido #" + pedidoId + " para entrega");
                
                Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
                if (pedidoOpt.isPresent()) {
                    Pedido pedido = pedidoOpt.get();
                    
                    // Simular etapas de preparaa?a?o
                    System.out.println("[BACKGROUND] Verificando estoque para pedido #" + pedidoId);
                    Thread.sleep(1000);
                    
                    System.out.println("[BACKGROUND] Separando produtos para pedido #" + pedidoId);
                    Thread.sleep(1500);
                    
                    System.out.println("[BACKGROUND] Embalando pedido #" + pedidoId);
                    Thread.sleep(800);
                    
                    System.out.println("[BACKGROUND] Pedido #" + pedidoId + " preparado para entrega!");
                    
                    // Simular entrega
                    Thread.sleep(500);
                    pedido.setStatus(StatusPedido.FINALIZADO);
                    pedido.setDataEntrega(LocalDateTime.now());
                    pedidoRepository.update(pedido);
                    
                    // Enviar notificacao de entrega
                    notificationService.notificarEntregaRealizadaAsync(
                        clienteService.buscarClientePorId(pedido.getClienteId()).orElse(null), 
                        pedido
                    );
                    
                    System.out.println("[BACKGROUND] Entrega do pedido #" + pedidoId + " finalizada!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[BACKGROUND] Preparaa?a?o interrompida para pedido #" + pedidoId);
            } catch (Exception e) {
                System.err.println("[BACKGROUND] Erro ao preparar pedido #" + pedidoId + ": " + e.getMessage());
            }
        }, backgroundExecutor);
    }
    
    /**
     * Gera relata?rios em background usando threads.
     */
    public CompletableFuture<String> gerarRelatorioAsync(String tipoRelatorio) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("\n[BACKGROUND] Gerando relata?rio: " + tipoRelatorio);
                
                // Simular tempo de geraa?a?o de relata?rio
                Thread.sleep(1500);
                
                List<Pedido> pedidos = pedidoRepository.findAll();
                
                switch (tipoRelatorio.toLowerCase()) {
                    case "vendas":
                        return gerarRelatorioVendas(pedidos);
                    case "clientes":
                        return gerarRelatorioClientes(pedidos);
                    default:
                        return gerarRelatorioGeral(pedidos);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Relata?rio interrompido: " + tipoRelatorio;
            } catch (Exception e) {
                return "Erro ao gerar relata?rio " + tipoRelatorio + ": " + e.getMessage();
            }
        }, backgroundExecutor);
    }
    
    private String gerarRelatorioVendas(List<Pedido> pedidos) {
        BigDecimal totalVendas = pedidos.stream()
            .filter(p -> p.getValorTotal() != null)
            .map(Pedido::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        long pedidosFinalizados = pedidos.stream()
            .filter(p -> p.getStatus() == StatusPedido.FINALIZADO)
            .count();
            
        return String.format("[RELATORIO] Total de vendas: R$ %.2f | Pedidos finalizados: %d", 
            totalVendas, pedidosFinalizados);
    }
    
    private String gerarRelatorioClientes(List<Pedido> pedidos) {
        long clientesUnicos = pedidos.stream()
            .map(Pedido::getClienteId)
            .distinct()
            .count();
            
        return String.format("[RELATORIO] Total de clientes unicos: %d", clientesUnicos);
    }
    
    private String gerarRelatorioGeral(List<Pedido> pedidos) {
        return String.format("[RELATORIO GERAL] Total de pedidos: %d", pedidos.size());
    }
    
    /**
     * Fecha o executor quando necessario.
     */
    public void shutdown() {
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
            System.out.println("[SISTEMA] PedidoService executor shutdown completed.");
        }
    }
}
