package br.com.ada.ecommerce.cli;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.models.ItemPedido;
import br.com.ada.ecommerce.models.Pedido;
import br.com.ada.ecommerce.models.Produto;
import br.com.ada.ecommerce.models.StatusPedido;
import br.com.ada.ecommerce.services.ClienteService;
import br.com.ada.ecommerce.services.PedidoService;
import br.com.ada.ecommerce.services.ProdutoService;

public class VendaCLI {
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public VendaCLI() {
        this.pedidoService = new PedidoService();
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
    }
    
    public VendaCLI(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
    }

    public void executar() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Fluxo de Venda Simplificado");

            // Verificar se existem clientes e produtos
            List<Cliente> clientes = clienteService.listarClientes();
            List<Produto> produtos = produtoService.listarProdutos();

            if (clientes.isEmpty()) {
                System.out.println("ERRO: Nenhum cliente cadastrado. Cadastre pelo menos um cliente primeiro.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                return;
            }

            if (produtos.isEmpty()) {
                System.out.println("ERRO: Nenhum produto cadastrado. Cadastre pelo menos um produto primeiro.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                return;
            }

            String[] opcoes = {
                "Criar Novo Pedido",
                "Gerenciar Pedidos Existentes",
                "Voltar ao Menu Principal"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> criarNovoPedido(clientes, produtos);
                case 2 -> gerenciarPedidosExistentes();
                case 0 -> { return; }
            }
        }
    }

    private void criarNovoPedido(List<Cliente> clientes, List<Produto> produtos) {
        try {
            // 1. Selecionar Cliente
            Long clienteId = selecionarCliente(clientes);
            if (clienteId == null) {
                return;
            }

            // 2. Criar Pedido
            Pedido pedido = pedidoService.criarPedido(clienteId);
            System.out.println("\n* Pedido criado com sucesso! ID: " + pedido.getId());

            // 3. Adicionar Produtos
            adicionarProdutosAoPedido(pedido.getId(), produtos);

            // 4. Gerenciar Pedido
            gerenciarPedidoEspecifico(pedido.getId());

        } catch (Exception e) {
            System.out.println("\n*** ERRO durante a criacao do pedido: " + e.getMessage());
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
        }
    }

    private void gerenciarPedidosExistentes() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Gerenciar Pedidos Existentes");

        List<Pedido> pedidos = pedidoService.listarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        ConsoleUtils.mostrarPedidos(pedidos);
        
        Long pedidoId = ConsoleUtils.lerLong("\nDigite o ID do pedido para gerenciar: ");
        if (pedidoId == null) {
            System.out.println("ERRO: ID do pedido e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            System.out.println("ERRO: Pedido nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        gerenciarPedidoEspecifico(pedidoId);
    }

    private void gerenciarPedidoEspecifico(Long pedidoId) {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Gerenciar Pedido #" + pedidoId);

            // Mostrar status atual do pedido
            Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(pedidoId);
            if (pedidoOpt.isEmpty()) {
                System.out.println("ERRO: Pedido nao encontrado.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                return;
            }

            Pedido pedido = pedidoOpt.get();
            System.out.println("Status atual: " + pedido.getStatus().getDescricao());
            System.out.println("Valor total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Cliente ID: " + pedido.getClienteId());

            // Mostrar itens do pedido
            List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
            if (!itens.isEmpty()) {
                System.out.println("\nItens do pedido:");
                for (ItemPedido item : itens) {
                    System.out.println("  * " + item.getNomeProduto() + " - Qtd: " + item.getQuantidade() + 
                                     " - R$ " + String.format("%.2f", item.getSubtotal()));
                }
            }

            // Opcoes baseadas no status do pedido
            String[] opcoes = getOpcoesPorStatus(pedido.getStatus());
            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            boolean continuar = processarOpcaoPedido(pedidoId, opcao, pedido.getStatus());
            if (!continuar) {
                break;
            }
        }
    }

    private String[] getOpcoesPorStatus(StatusPedido status) {
        switch (status) {
            case ABERTO:
                return new String[]{
                    "Adicionar Itens",
                    "Remover Itens", 
                    "Finalizar Pedido",
                    "Voltar ao Menu Principal"
                };
            case AGUARDANDO_PAGAMENTO:
                return new String[]{
                    "Realizar Pagamento",
                    "Voltar ao Menu Principal"
                };
            case PAGO:
                return new String[]{
                    "Confirmar Entrega",
                    "Voltar ao Menu Principal"
                };
            case FINALIZADO:
                return new String[]{
                    "Visualizar Detalhes",
                    "Voltar ao Menu Principal"
                };
            default:
                return new String[]{
                    "Voltar ao Menu Principal"
                };
        }
    }

    private boolean processarOpcaoPedido(Long pedidoId, int opcao, StatusPedido status) {
        switch (status) {
            case ABERTO:
                switch (opcao) {
                    case 1 -> adicionarItensAoPedido(pedidoId);
                    case 2 -> removerItensDoPedido(pedidoId);
                    case 3 -> finalizarPedido(pedidoId);
                    case 0 -> { return false; }
                }
                break;
            case AGUARDANDO_PAGAMENTO:
                switch (opcao) {
                    case 1 -> realizarPagamento(pedidoId);
                    case 0 -> { return false; }
                }
                break;
            case PAGO:
                switch (opcao) {
                    case 1 -> confirmarEntrega(pedidoId);
                    case 0 -> { return false; }
                }
                break;
            case FINALIZADO:
                switch (opcao) {
                    case 1 -> visualizarDetalhesPedido(pedidoId);
                    case 0 -> { return false; }
                }
                break;
        }
        return true;
    }

    private void adicionarItensAoPedido(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Adicionar Itens - Pedido #" + pedidoId);

        List<Produto> produtos = produtoService.listarProdutos();
        ConsoleUtils.mostrarProdutos(produtos);

        while (true) {
            Long produtoId = ConsoleUtils.lerLong("\nDigite o ID do produto (ou 0 para finalizar): ");
            if (produtoId == null || produtoId == 0) {
                break;
            }

            Optional<Produto> produtoOpt = produtoService.buscarProdutoPorId(produtoId);
            if (produtoOpt.isEmpty()) {
                System.out.println("ERRO: Produto nao encontrado.");
                continue;
            }

            Produto produto = produtoOpt.get();
            System.out.println("\nProduto selecionado: " + produto.getNome());
            System.out.println("Valor padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));

            Integer quantidade = ConsoleUtils.lerInt("Digite a quantidade: ");
            if (quantidade == null || quantidade <= 0) {
                System.out.println("ERRO: Quantidade deve ser maior que zero.");
                continue;
            }

            String precoStr = ConsoleUtils.lerString("Digite o preco de venda (ou Enter para usar valor padrao): ");
            BigDecimal precoVenda;
            
            if (precoStr.isEmpty()) {
                precoVenda = produto.getValorPadrao();
            } else {
                try {
                    precoStr = precoStr.replace(",", ".");
                    precoVenda = new BigDecimal(precoStr);
                    if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                        System.out.println("ERRO: Preco deve ser maior ou igual a zero.");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERRO: Valor invalido. Usando valor padrao.");
                    precoVenda = produto.getValorPadrao();
                }
            }

            try {
                ItemPedido item = pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade, precoVenda);
                System.out.println("\n* Item adicionado com sucesso!");
                System.out.println("Produto: " + item.getNomeProduto());
                System.out.println("Quantidade: " + item.getQuantidade());
                System.out.println("Preco: R$ " + String.format("%.2f", item.getPrecoVenda()));
                System.out.println("Subtotal: R$ " + String.format("%.2f", item.getSubtotal()));
                
            } catch (Exception e) {
                System.out.println("\nERRO: Erro ao adicionar item: " + e.getMessage());
            }

            ConsoleUtils.pausar("\nPressione Enter para continuar...");
        }
    }

    private void removerItensDoPedido(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Remover Itens - Pedido #" + pedidoId);

        List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
        if (itens.isEmpty()) {
            System.out.println("Nenhum item no pedido.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        System.out.println("Itens do pedido:");
        for (ItemPedido item : itens) {
            System.out.println("ID: " + item.getId() + " - " + item.getNomeProduto() + 
                             " (Qtd: " + item.getQuantidade() + 
                             ", Preco: R$ " + String.format("%.2f", item.getPrecoVenda()) + 
                             ", Subtotal: R$ " + String.format("%.2f", item.getSubtotal()) + ")");
        }

        Long itemId = ConsoleUtils.lerLong("\nDigite o ID do item a ser removido (ou 0 para cancelar): ");
        if (itemId == null || itemId == 0) {
            return;
        }

        try {
            boolean removido = pedidoService.removerItemDoPedido(itemId);
            if (removido) {
                System.out.println("\n* Item removido com sucesso!");
            } else {
                System.out.println("\nERRO: Item nao encontrado.");
            }
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void finalizarPedido(Long pedidoId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINALIZANDO PEDIDO");
        System.out.println("=".repeat(50));

        try {
            Pedido pedido = pedidoService.finalizarPedido(pedidoId);
            System.out.println("* Pedido finalizado com sucesso!");
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Data Finalizacao: " + pedido.getDataFinalizacao());
            
            // Mostrar resumo do pedido
            List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
            System.out.println("\nResumo do Pedido:");
            for (ItemPedido item : itens) {
                System.out.println("  * " + item.getNomeProduto() + " - Qtd: " + item.getQuantidade() + 
                                 " - R$ " + String.format("%.2f", item.getSubtotal()));
            }
            
        } catch (Exception e) {
            System.out.println("ERRO: Erro ao finalizar pedido: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void realizarPagamento(Long pedidoId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("REALIZANDO PAGAMENTO");
        System.out.println("=".repeat(50));

        try {
            Pedido pedido = pedidoService.realizarPagamento(pedidoId);
            System.out.println("* Pagamento realizado com sucesso!");
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Valor Pago: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Data Pagamento: " + pedido.getDataPagamento());
            
        } catch (Exception e) {
            System.out.println("ERRO: Erro ao realizar pagamento: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void confirmarEntrega(Long pedidoId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("CONFIRMAR ENTREGA");
        System.out.println("=".repeat(50));

        String confirmar = ConsoleUtils.lerString("Confirmar entrega do pedido? (s/n): ");
        if (!confirmar.toLowerCase().startsWith("s")) {
            System.out.println("Entrega nao confirmada. Pedido permanece como 'Pago'.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            Pedido pedido = pedidoService.realizarEntrega(pedidoId);
            System.out.println("* Entrega confirmada com sucesso!");
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Data Entrega: " + pedido.getDataEntrega());
            System.out.println("\n*** PEDIDO FINALIZADO COM SUCESSO! ***");
            
        } catch (Exception e) {
            System.out.println("ERRO: Erro ao confirmar entrega: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void visualizarDetalhesPedido(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Detalhes do Pedido #" + pedidoId);

        Optional<Pedido> pedidoOpt = pedidoService.buscarPedidoPorId(pedidoId);
        if (pedidoOpt.isEmpty()) {
            System.out.println("ERRO: Pedido nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Pedido pedido = pedidoOpt.get();
        System.out.println("ID: " + pedido.getId());
        System.out.println("Cliente ID: " + pedido.getClienteId());
        System.out.println("Status: " + pedido.getStatus().getDescricao());
        System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
        System.out.println("Data Criacao: " + pedido.getDataCriacao());
        
        if (pedido.getDataFinalizacao() != null) {
            System.out.println("Data Finalizacao: " + pedido.getDataFinalizacao());
        }
        if (pedido.getDataPagamento() != null) {
            System.out.println("Data Pagamento: " + pedido.getDataPagamento());
        }
        if (pedido.getDataEntrega() != null) {
            System.out.println("Data Entrega: " + pedido.getDataEntrega());
        }

        List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
        if (!itens.isEmpty()) {
            System.out.println("\nItens do Pedido:");
            for (ItemPedido item : itens) {
                System.out.println("  * " + item.getNomeProduto() + " (Qtd: " + item.getQuantidade() + 
                                 ", Preco: R$ " + String.format("%.2f", item.getPrecoVenda()) + 
                                 ", Subtotal: R$ " + String.format("%.2f", item.getSubtotal()) + ")");
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private Long selecionarCliente(List<Cliente> clientes) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Selecionar Cliente");

        System.out.println("Clientes disponiveis:");
        ConsoleUtils.mostrarClientes(clientes);

        Long clienteId = ConsoleUtils.lerLong("\nDigite o ID do cliente: ");
        if (clienteId == null) {
            System.out.println("ERRO: ID do cliente e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return null;
        }

        Optional<Cliente> cliente = clienteService.buscarClientePorId(clienteId);
        if (cliente.isEmpty()) {
            System.out.println("ERRO: Cliente nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return null;
        }

        System.out.println("\n* Cliente selecionado: " + cliente.get().getNome());
        return clienteId;
    }

    private void adicionarProdutosAoPedido(Long pedidoId, List<Produto> produtos) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ADICIONAR PRODUTOS AO PEDIDO");
        System.out.println("=".repeat(50));

        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Adicionar Produtos - Pedido #" + pedidoId);

            // Mostrar produtos disponiveis
            ConsoleUtils.mostrarProdutos(produtos);

            // Mostrar itens ja adicionados
            List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
            if (!itens.isEmpty()) {
                System.out.println("\nItens ja adicionados:");
                for (ItemPedido item : itens) {
                    System.out.println("  * " + item.getNomeProduto() + " - Qtd: " + item.getQuantidade() + 
                                     " - R$ " + String.format("%.2f", item.getSubtotal()));
                }
                BigDecimal totalAtual = pedidoService.calcularValorTotalPedido(pedidoId);
                System.out.println("Total atual: R$ " + String.format("%.2f", totalAtual));
            }

            Long produtoId = ConsoleUtils.lerLong("\nDigite o ID do produto (ou 0 para finalizar): ");
            if (produtoId == null || produtoId == 0) {
                break;
            }

            Optional<Produto> produtoOpt = produtoService.buscarProdutoPorId(produtoId);
            if (produtoOpt.isEmpty()) {
                System.out.println("ERRO: Produto nao encontrado.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                continue;
            }

            Produto produto = produtoOpt.get();
            System.out.println("\nProduto selecionado: " + produto.getNome());
            System.out.println("Valor padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));

            Integer quantidade = ConsoleUtils.lerInt("Digite a quantidade: ");
            if (quantidade == null || quantidade <= 0) {
                System.out.println("ERRO: Quantidade deve ser maior que zero.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                continue;
            }

            String precoStr = ConsoleUtils.lerString("Digite o preco de venda (ou Enter para usar valor padrao R$ " + 
                                                   String.format("%.2f", produto.getValorPadrao()) + "): ");
            BigDecimal precoVenda;
            
            if (precoStr.isEmpty()) {
                precoVenda = produto.getValorPadrao();
            } else {
                try {
                    precoStr = precoStr.replace(",", ".");
                    precoVenda = new BigDecimal(precoStr);
                    if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                        System.out.println("ERRO: Preco deve ser maior ou igual a zero.");
                        ConsoleUtils.pausar("\nPressione Enter para continuar...");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("ERRO: Valor invalido. Usando valor padrao.");
                    precoVenda = produto.getValorPadrao();
                }
            }

            try {
                ItemPedido item = pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade, precoVenda);
                System.out.println("\n* Item adicionado com sucesso!");
                System.out.println("Produto: " + item.getNomeProduto());
                System.out.println("Quantidade: " + item.getQuantidade());
                System.out.println("Preco: R$ " + String.format("%.2f", item.getPrecoVenda()));
                System.out.println("Subtotal: R$ " + String.format("%.2f", item.getSubtotal()));
                
            } catch (Exception e) {
                System.out.println("\nERRO: Erro ao adicionar item: " + e.getMessage());
            }

            ConsoleUtils.pausar("\nPressione Enter para continuar...");
        }
    }
}
