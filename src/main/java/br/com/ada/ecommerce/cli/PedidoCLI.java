package br.com.ada.ecommerce.cli;

import br.com.ada.ecommerce.models.*;
import br.com.ada.ecommerce.services.ClienteService;
import br.com.ada.ecommerce.services.PedidoService;
import br.com.ada.ecommerce.services.ProdutoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PedidoCLI {
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoCLI() {
        this.pedidoService = new PedidoService();
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
    }
    
    public PedidoCLI(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        this.clienteService = new ClienteService();
        this.produtoService = new ProdutoService();
    }

    public void executar() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Gestao de Pedidos");

            String[] opcoes = {
                "Criar Pedido",
                "Listar Pedidos",
                "Buscar Pedidos",
                "Gerenciar Itens do Pedido",
                "Finalizar Pedido",
                "Realizar Pagamento",
                "Realizar Entrega"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> criarPedido();
                case 2 -> listarPedidos();
                case 3 -> menuBuscaPedidos();
                case 4 -> gerenciarItensPedido();
                case 5 -> finalizarPedido();
                case 6 -> realizarPagamento();
                case 7 -> realizarEntrega();
                case 0 -> { return; }
            }
        }
    }

    private void criarPedido() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Criar Pedido");

        // Mostrar clientes disponíveis
        List<Cliente> clientes = clienteService.listarClientes();
        ConsoleUtils.mostrarClientes(clientes);
        
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado. Cadastre um cliente primeiro.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long clienteId = ConsoleUtils.lerLong("\nDigite o ID do cliente: ");
        if (clienteId == null) {
            System.out.println("ERRO: ID do cliente e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            Pedido pedido = pedidoService.criarPedido(clienteId);
            System.out.println("\nSUCESSO: Pedido criado com sucesso!");
            System.out.println("ID do Pedido: " + pedido.getId());
            System.out.println("ID do Cliente: " + pedido.getClienteId());
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Data de Criacao: " + pedido.getDataCriacao());

            // Perguntar se quer adicionar produtos
            System.out.println("\nDeseja adicionar produtos ao pedido agora? (s/n)");
            String resposta = ConsoleUtils.lerString("Digite sua opcao: ");
            
            if (resposta.toLowerCase().startsWith("s")) {
                adicionarProdutosAoPedido(pedido.getId());
            }
            
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void adicionarProdutosAoPedido(Long pedidoId) {
        List<Produto> produtos = produtoService.listarProdutos();
        if (produtos.isEmpty()) {
            System.out.println("\nNenhum produto disponivel para adicionar.");
            return;
        }

        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Adicionar Produtos ao Pedido #" + pedidoId);
            
            ConsoleUtils.mostrarProdutos(produtos);

            Long produtoId = ConsoleUtils.lerLong("\nDigite o ID do produto (ou 0 para finalizar): ");
            if (produtoId == null || produtoId == 0) {
                break;
            }

            // Buscar o produto
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
                System.out.println("Usando valor padrao: R$ " + String.format("%.2f", precoVenda));
            } else {
                try {
                    precoStr = precoStr.replace(",", ".");
                    precoVenda = new BigDecimal(precoStr);
                    if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                        System.out.println("ERRO: Preco de venda deve ser maior ou igual a zero.");
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
                System.out.println("\nSUCESSO: Item adicionado!");
                System.out.println("Produto: " + item.getNomeProduto());
                System.out.println("Quantidade: " + item.getQuantidade());
                System.out.println("Preco: R$ " + String.format("%.2f", item.getPrecoVenda()));
                System.out.println("Subtotal: R$ " + String.format("%.2f", item.getSubtotal()));
                
                // Mostrar total atual do pedido
                BigDecimal totalAtual = pedidoService.calcularValorTotalPedido(pedidoId);
                System.out.println("Total do pedido: R$ " + String.format("%.2f", totalAtual));
                
            } catch (Exception e) {
                System.out.println("\nERRO: " + e.getMessage());
            }

            ConsoleUtils.pausar("\nPressione Enter para continuar...");
        }
    }

    private void listarPedidos() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Lista de Pedidos");

        List<Pedido> pedidos = pedidoService.listarPedidos();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
        } else {
            System.out.println("Total de pedidos: " + pedidos.size() + "\n");
            for (Pedido pedido : pedidos) {
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
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void menuBuscaPedidos() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Buscar Pedidos");

            String[] opcoes = {
                "Buscar por ID",
                "Buscar por Cliente",
                "Buscar por Status",
                "Buscar por Valor"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> buscarPedidoPorId();
                case 2 -> buscarPedidosPorCliente();
                case 3 -> buscarPedidosPorStatus();
                case 4 -> buscarPedidosPorValor();
                case 0 -> { return; }
            }
        }
    }

    private void buscarPedidoPorId() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedido por ID");

        // Mostrar pedidos disponíveis
        List<Pedido> pedidos = pedidoService.listarPedidos();
        ConsoleUtils.mostrarPedidos(pedidos);
        
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long id = ConsoleUtils.lerLong("\nDigite o ID do pedido: ");
        if (id == null) {
            System.out.println("ERRO: ID e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Pedido> pedido = pedidoService.buscarPedidoPorId(id);

        if (pedido.isPresent()) {
            System.out.println("\nSUCESSO: Pedido encontrado:");
            System.out.println("ID: " + pedido.get().getId());
            System.out.println("Cliente ID: " + pedido.get().getClienteId());
            System.out.println("Status: " + pedido.get().getStatus().getDescricao());
            System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.get().getValorTotal()));
            System.out.println("Data Criacao: " + pedido.get().getDataCriacao());
            
            List<ItemPedido> itens = pedidoService.listarItensDoPedido(id);
            if (!itens.isEmpty()) {
                System.out.println("\nItens do Pedido:");
                for (ItemPedido item : itens) {
                    System.out.println("  - " + item.getNomeProduto() + " (Qtd: " + item.getQuantidade() + 
                                     ", Preco: R$ " + String.format("%.2f", item.getPrecoVenda()) + 
                                     ", Subtotal: R$ " + String.format("%.2f", item.getSubtotal()) + ")");
                }
            }
        } else {
            System.out.println("\nERRO: Pedido nao encontrado.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarPedidosPorCliente() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos por Cliente");

        // Mostrar clientes disponíveis
        List<Cliente> clientes = clienteService.listarClientes();
        ConsoleUtils.mostrarClientes(clientes);
        
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long clienteId = ConsoleUtils.lerLong("\nDigite o ID do cliente: ");
        if (clienteId == null) {
            System.out.println("ERRO: ID do cliente e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado para este cliente.");
        } else {
            System.out.println("Pedidos do Cliente ID " + clienteId + " (" + pedidos.size() + " pedidos):\n");
            for (Pedido pedido : pedidos) {
                System.out.println("ID: " + pedido.getId());
                System.out.println("Status: " + pedido.getStatus().getDescricao());
                System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
                System.out.println("Data Criacao: " + pedido.getDataCriacao());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarPedidosPorStatus() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos por Status");

        System.out.println("Status disponiveis:");
        StatusPedido[] statusList = StatusPedido.values();
        for (int i = 0; i < statusList.length; i++) {
            System.out.println((i + 1) + " - " + statusList[i].getDescricao());
        }

        Integer opcaoStatus = ConsoleUtils.lerInt("\nEscolha o status: ");
        if (opcaoStatus == null || opcaoStatus < 1 || opcaoStatus > statusList.length) {
            System.out.println("ERRO: Opcao invalida.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        StatusPedido status = statusList[opcaoStatus - 1];
        List<Pedido> pedidos = pedidoService.buscarPedidosPorStatus(status);

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado com status: " + status.getDescricao());
        } else {
            System.out.println("Pedidos encontrados (" + pedidos.size() + "):\n");
            for (Pedido pedido : pedidos) {
                System.out.println("ID: " + pedido.getId());
                System.out.println("Cliente ID: " + pedido.getClienteId());
                System.out.println("Status: " + pedido.getStatus().getDescricao());
                System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
                System.out.println("Data Criacao: " + pedido.getDataCriacao());
                if (pedido.getDataFinalizacao() != null) {
                    System.out.println("Data Finalizacao: " + pedido.getDataFinalizacao());
                }
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarPedidosPorValor() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos por Valor");

        String[] opcoes = {
            "Pedidos com valor maior que",
            "Pedidos com valor menor que",
            "Pedidos com valor entre"
        };

        ConsoleUtils.exibirMenu(opcoes);
        int opcao = ConsoleUtils.lerOpcao(opcoes.length);

        switch (opcao) {
            case 1 -> buscarPedidosPorValorMaiorQue();
            case 2 -> buscarPedidosPorValorMenorQue();
            case 3 -> buscarPedidosPorValorEntre();
            case 0 -> { return; }
        }
    }

    private void buscarPedidosPorValorMaiorQue() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos com Valor Maior Que");

        String valorStr = ConsoleUtils.lerString("Digite o valor minimo: ");
        if (valorStr.isEmpty()) {
            System.out.println("ERRO: Valor e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));
            List<Pedido> pedidos = pedidoService.buscarPedidosPorValorTotalMaiorQue(valor);

            if (pedidos.isEmpty()) {
                System.out.println("Nenhum pedido encontrado com valor maior que R$ " + String.format("%.2f", valor));
            } else {
                System.out.println("Pedidos encontrados (" + pedidos.size() + "):\n");
                for (Pedido pedido : pedidos) {
                    System.out.println("ID: " + pedido.getId());
                    System.out.println("Cliente ID: " + pedido.getClienteId());
                    System.out.println("Status: " + pedido.getStatus().getDescricao());
                    System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
                    System.out.println("Data Criacao: " + pedido.getDataCriacao());
                    System.out.println("-".repeat(40));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ERRO: Valor invalido.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarPedidosPorValorMenorQue() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos com Valor Menor Que");

        String valorStr = ConsoleUtils.lerString("Digite o valor maximo: ");
        if (valorStr.isEmpty()) {
            System.out.println("ERRO: Valor e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(valorStr.replace(",", "."));
            List<Pedido> pedidos = pedidoService.buscarPedidosPorValorTotalMenorQue(valor);

            if (pedidos.isEmpty()) {
                System.out.println("Nenhum pedido encontrado com valor menor que R$ " + String.format("%.2f", valor));
            } else {
                System.out.println("Pedidos encontrados (" + pedidos.size() + "):\n");
                for (Pedido pedido : pedidos) {
                    System.out.println("ID: " + pedido.getId());
                    System.out.println("Cliente ID: " + pedido.getClienteId());
                    System.out.println("Status: " + pedido.getStatus().getDescricao());
                    System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
                    System.out.println("Data Criacao: " + pedido.getDataCriacao());
                    System.out.println("-".repeat(40));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ERRO: Valor invalido.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarPedidosPorValorEntre() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Pedidos com Valor Entre");

        String valorMinStr = ConsoleUtils.lerString("Digite o valor minimo: ");
        if (valorMinStr.isEmpty()) {
            System.out.println("ERRO: Valor minimo e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        String valorMaxStr = ConsoleUtils.lerString("Digite o valor maximo: ");
        if (valorMaxStr.isEmpty()) {
            System.out.println("ERRO: Valor maximo e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            BigDecimal valorMinimo = new BigDecimal(valorMinStr.replace(",", "."));
            BigDecimal valorMaximo = new BigDecimal(valorMaxStr.replace(",", "."));
            
            if (valorMinimo.compareTo(valorMaximo) > 0) {
                System.out.println("ERRO: Valor minimo deve ser menor que valor maximo.");
                ConsoleUtils.pausar("\nPressione Enter para continuar...");
                return;
            }

            List<Pedido> pedidos = pedidoService.buscarPedidosPorValorTotalEntre(valorMinimo, valorMaximo);

            if (pedidos.isEmpty()) {
                System.out.println("Nenhum pedido encontrado com valor entre R$ " + 
                                 String.format("%.2f", valorMinimo) + " e R$ " + String.format("%.2f", valorMaximo));
            } else {
                System.out.println("Pedidos encontrados (" + pedidos.size() + "):\n");
                for (Pedido pedido : pedidos) {
                    System.out.println("ID: " + pedido.getId());
                    System.out.println("Cliente ID: " + pedido.getClienteId());
                    System.out.println("Status: " + pedido.getStatus().getDescricao());
                    System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
                    System.out.println("Data Criacao: " + pedido.getDataCriacao());
                    System.out.println("-".repeat(40));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ERRO: Valor invalido.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void gerenciarItensPedido() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Gerenciar Itens do Pedido");

        // Mostrar pedidos disponíveis
        List<Pedido> pedidos = pedidoService.listarPedidos();
        ConsoleUtils.mostrarPedidos(pedidos);
        
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long pedidoId = ConsoleUtils.lerLong("\nDigite o ID do pedido: ");
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

        Pedido pedido = pedidoOpt.get();
        if (pedido.getStatus() != StatusPedido.ABERTO) {
            System.out.println("ERRO: Apenas pedidos com status 'Aberto' podem ter itens gerenciados.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Gerenciar Itens - Pedido #" + pedidoId);

            List<ItemPedido> itens = pedidoService.listarItensDoPedido(pedidoId);
            if (itens.isEmpty()) {
                System.out.println("Nenhum item no pedido.");
            } else {
                System.out.println("Itens do Pedido:");
                for (ItemPedido item : itens) {
                    System.out.println("ID: " + item.getId() + " - " + item.getNomeProduto() + 
                                     " (Qtd: " + item.getQuantidade() + 
                                     ", Preco: R$ " + String.format("%.2f", item.getPrecoVenda()) + 
                                     ", Subtotal: R$ " + String.format("%.2f", item.getSubtotal()) + ")");
                }
            }

            String[] opcoes = {
                "Adicionar Item",
                "Remover Item",
                "Alterar Quantidade"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> adicionarItemAoPedido(pedidoId);
                case 2 -> removerItemDoPedido(pedidoId);
                case 3 -> alterarQuantidadeItem(pedidoId);
                case 0 -> { return; }
            }
        }
    }

    private void adicionarItemAoPedido(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Adicionar Item ao Pedido");

        // Mostrar produtos disponíveis
        List<Produto> produtos = produtoService.listarProdutos();
        ConsoleUtils.mostrarProdutos(produtos);
        
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado. Cadastre um produto primeiro.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long produtoId = ConsoleUtils.lerLong("\nDigite o ID do produto: ");
        if (produtoId == null) {
            System.out.println("ERRO: ID do produto e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        // Buscar o produto para mostrar o valor padrão
        Optional<Produto> produtoOpt = produtoService.buscarProdutoPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            System.out.println("ERRO: Produto nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Produto produto = produtoOpt.get();
        System.out.println("\nProduto selecionado: " + produto.getNome());
        System.out.println("Valor padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));

        Integer quantidade = ConsoleUtils.lerInt("Digite a quantidade: ");
        if (quantidade == null || quantidade <= 0) {
            System.out.println("ERRO: Quantidade deve ser maior que zero.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        String precoStr = ConsoleUtils.lerString("Digite o preco de venda (ou Enter para usar valor padrao R$ " + 
                                               String.format("%.2f", produto.getValorPadrao()) + "): ");
        BigDecimal precoVenda;
        
        if (precoStr.isEmpty()) {
            precoVenda = produto.getValorPadrao();
            System.out.println("Usando valor padrao: R$ " + String.format("%.2f", precoVenda));
        } else {
            try {
                precoStr = precoStr.replace(",", ".");
                precoVenda = new BigDecimal(precoStr);
                if (precoVenda.compareTo(BigDecimal.ZERO) < 0) {
                    System.out.println("ERRO: Preco de venda deve ser maior ou igual a zero.");
                    ConsoleUtils.pausar("\nPressione Enter para continuar...");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Valor invalido. Usando valor padrao.");
                precoVenda = produto.getValorPadrao();
            }
        }

        try {
            ItemPedido item = pedidoService.adicionarItemAoPedido(pedidoId, produtoId, quantidade, precoVenda);
            System.out.println("\nSUCESSO: Item adicionado com sucesso!");
            System.out.println("ID do Item: " + item.getId());
            System.out.println("Produto: " + item.getNomeProduto());
            System.out.println("Quantidade: " + item.getQuantidade());
            System.out.println("Preco: R$ " + String.format("%.2f", item.getPrecoVenda()));
            System.out.println("Subtotal: R$ " + String.format("%.2f", item.getSubtotal()));
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void removerItemDoPedido(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Remover Item do Pedido");

        Long itemId = ConsoleUtils.lerLong("Digite o ID do item a ser removido: ");
        if (itemId == null) {
            System.out.println("ERRO: ID do item e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            boolean removido = pedidoService.removerItemDoPedido(itemId);
            if (removido) {
                System.out.println("\nSUCESSO: Item removido com sucesso!");
            } else {
                System.out.println("\nERRO: Item nao encontrado.");
            }
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void alterarQuantidadeItem(Long pedidoId) {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Alterar Quantidade do Item");

        Long itemId = ConsoleUtils.lerLong("Digite o ID do item: ");
        if (itemId == null) {
            System.out.println("ERRO: ID do item e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Integer novaQuantidade = ConsoleUtils.lerInt("Digite a nova quantidade: ");
        if (novaQuantidade == null || novaQuantidade <= 0) {
            System.out.println("ERRO: Quantidade deve ser maior que zero.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            ItemPedido item = pedidoService.alterarQuantidadeItem(itemId, novaQuantidade);
            System.out.println("\nSUCESSO: Quantidade alterada com sucesso!");
            System.out.println("Item: " + item.getNomeProduto());
            System.out.println("Nova Quantidade: " + item.getQuantidade());
            System.out.println("Preco: R$ " + String.format("%.2f", item.getPrecoVenda()));
            System.out.println("Novo Subtotal: R$ " + String.format("%.2f", item.getSubtotal()));
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void finalizarPedido() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Finalizar Pedido");

        // Mostrar pedidos disponíveis
        List<Pedido> pedidos = pedidoService.listarPedidos();
        ConsoleUtils.mostrarPedidos(pedidos);
        
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long pedidoId = ConsoleUtils.lerLong("\nDigite o ID do pedido: ");
        if (pedidoId == null) {
            System.out.println("ERRO: ID do pedido e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            Pedido pedido = pedidoService.finalizarPedido(pedidoId);
            System.out.println("\nSUCESSO: Pedido finalizado com sucesso!");
            System.out.println("ID: " + pedido.getId());
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Data Finalizacao: " + pedido.getDataFinalizacao());
            System.out.println("\nCliente foi notificado sobre o pedido finalizado!");
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void realizarPagamento() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Realizar Pagamento");

        // Mostrar pedidos disponíveis
        List<Pedido> pedidos = pedidoService.listarPedidos();
        ConsoleUtils.mostrarPedidos(pedidos);
        
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long pedidoId = ConsoleUtils.lerLong("\nDigite o ID do pedido: ");
        if (pedidoId == null) {
            System.out.println("ERRO: ID do pedido e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            Pedido pedido = pedidoService.realizarPagamento(pedidoId);
            System.out.println("\nSUCESSO: Pagamento realizado com sucesso!");
            System.out.println("ID: " + pedido.getId());
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Data Pagamento: " + pedido.getDataPagamento());
            System.out.println("\nCliente foi notificado sobre o pagamento confirmado!");
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void realizarEntrega() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Realizar Entrega");

        // Mostrar pedidos disponíveis
        List<Pedido> pedidos = pedidoService.listarPedidos();
        ConsoleUtils.mostrarPedidos(pedidos);
        
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Long pedidoId = ConsoleUtils.lerLong("\nDigite o ID do pedido: ");
        if (pedidoId == null) {
            System.out.println("ERRO: ID do pedido e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        try {
            Pedido pedido = pedidoService.realizarEntrega(pedidoId);
            System.out.println("\nSUCESSO: Entrega realizada com sucesso!");
            System.out.println("ID: " + pedido.getId());
            System.out.println("Status: " + pedido.getStatus().getDescricao());
            System.out.println("Valor Total: R$ " + String.format("%.2f", pedido.getValorTotal()));
            System.out.println("Data Entrega: " + pedido.getDataEntrega());
            System.out.println("\nCliente foi notificado sobre a entrega realizada!");
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private BigDecimal lerBigDecimal(String prompt) {
        while (true) {
            try {
                String input = ConsoleUtils.lerString(prompt);
                if (input.isEmpty()) {
                    return null;
                }
                input = input.replace(",", ".");
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Por favor, digite um valor valido (ex: 10.50 ou 10,50).");
            }
        }
    }
}