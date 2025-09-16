package br.com.ada.ecommerce.services;

import br.com.ada.ecommerce.models.*;
import br.com.ada.ecommerce.repositories.ItemPedidoRepository;
import br.com.ada.ecommerce.repositories.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;
    private final NotificationService notificationService;

    public PedidoService() {
        this.pedidoRepository = new PedidoRepository();
        this.itemPedidoRepository = new ItemPedidoRepository();
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
        this.notificationService = new NotificationService();
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
        notificationService.notificarPedidoFinalizado(cliente, pedido);

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
        notificationService.notificarPagamentoConfirmado(cliente, pedido);

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
        notificationService.notificarEntregaRealizada(cliente, pedido);

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
}