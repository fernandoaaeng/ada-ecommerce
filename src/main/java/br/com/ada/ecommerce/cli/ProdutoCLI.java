package br.com.ada.ecommerce.cli;

import br.com.ada.ecommerce.models.Produto;
import br.com.ada.ecommerce.services.ProdutoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProdutoCLI {
    private final ProdutoService produtoService;

    public ProdutoCLI() {
        this.produtoService = new ProdutoService();
    }

    public void executar() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Gestao de Produtos");

            String[] opcoes = {
                "Cadastrar Produto",
                "Listar Produtos",
                "Buscar Produtos",
                "Atualizar Produto"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> cadastrarProduto();
                case 2 -> listarProdutos();
                case 3 -> menuBuscaProdutos();
                case 4 -> atualizarProduto();
                case 0 -> { return; }
            }
        }
    }

    private void cadastrarProduto() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Cadastro de Produto");

        try {
            String nome = ConsoleUtils.lerString("Nome do produto: ");
            String descricao = ConsoleUtils.lerString("Descricao (opcional): ");
            BigDecimal valorPadrao = lerBigDecimal("Valor padrao: ");

            Produto produto = produtoService.cadastrarProduto(nome, descricao, valorPadrao);
            
            System.out.println("\nSUCESSO: Produto cadastrado com sucesso!");
            System.out.println("ID: " + produto.getId());
            System.out.println("Nome: " + produto.getNome());
            System.out.println("Descricao: " + produto.getDescricao());
            System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));

        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void listarProdutos() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Lista de Produtos");

        List<Produto> produtos = produtoService.listarProdutos();

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            System.out.println("Total de produtos: " + produtos.size() + "\n");
            for (Produto produto : produtos) {
                System.out.println("ID: " + produto.getId());
                System.out.println("Nome: " + produto.getNome());
                System.out.println("Descricao: " + produto.getDescricao());
                System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));
                System.out.println("Data Cadastro: " + produto.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void menuBuscaProdutos() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Buscar Produtos");

            String[] opcoes = {
                "Buscar por ID",
                "Buscar por Nome",
                "Buscar por Descricao"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> buscarProdutoPorId();
                case 2 -> buscarProdutosPorNome();
                case 3 -> buscarProdutosPorDescricao();
                case 0 -> { return; }
            }
        }
    }

    private void buscarProdutoPorId() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Produto por ID");

        Long id = ConsoleUtils.lerLong("Digite o ID do produto: ");
        if (id == null) {
            System.out.println("ERRO: ID e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Produto> produto = produtoService.buscarProdutoPorId(id);

        if (produto.isPresent()) {
            System.out.println("\nSUCESSO: Produto encontrado:");
            System.out.println("ID: " + produto.get().getId());
            System.out.println("Nome: " + produto.get().getNome());
            System.out.println("Descricao: " + produto.get().getDescricao());
            System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.get().getValorPadrao()));
            System.out.println("Data Cadastro: " + produto.get().getDataCadastro());
        } else {
            System.out.println("\nERRO: Produto nao encontrado.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarProdutosPorNome() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Produtos por Nome");

        String nome = ConsoleUtils.lerString("Digite o nome (ou parte do nome) do produto: ");
        if (nome.isEmpty()) {
            System.out.println("ERRO: Nome e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Produto> produtos = produtoService.buscarProdutosPorNome(nome);

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado com o nome: " + nome);
        } else {
            System.out.println("Produtos encontrados (" + produtos.size() + "):\n");
            for (Produto produto : produtos) {
                System.out.println("ID: " + produto.getId());
                System.out.println("Nome: " + produto.getNome());
                System.out.println("Descricao: " + produto.getDescricao());
                System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));
                System.out.println("Data Cadastro: " + produto.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarProdutosPorDescricao() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Produtos por Descricao");

        String descricao = ConsoleUtils.lerString("Digite a descricao (ou parte da descricao) do produto: ");
        if (descricao.isEmpty()) {
            System.out.println("ERRO: Descricao e obrigatoria.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Produto> produtos = produtoService.buscarProdutosPorDescricao(descricao);

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado com a descricao: " + descricao);
        } else {
            System.out.println("Produtos encontrados (" + produtos.size() + "):\n");
            for (Produto produto : produtos) {
                System.out.println("ID: " + produto.getId());
                System.out.println("Nome: " + produto.getNome());
                System.out.println("Descricao: " + produto.getDescricao());
                System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));
                System.out.println("Data Cadastro: " + produto.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void atualizarProduto() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Atualizar Produto");

        Long id = ConsoleUtils.lerLong("Digite o ID do produto a ser atualizado: ");
        if (id == null) {
            System.out.println("ERRO: ID e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Produto> produtoOpt = produtoService.buscarProdutoPorId(id);
        if (produtoOpt.isEmpty()) {
            System.out.println("ERRO: Produto nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Produto produto = produtoOpt.get();
        System.out.println("\nDados atuais do produto:");
        System.out.println("Nome: " + produto.getNome());
        System.out.println("Descricao: " + produto.getDescricao());
        System.out.println("Valor Padrao: R$ " + String.format("%.2f", produto.getValorPadrao()));

        System.out.println("\nDigite os novos dados (deixe em branco para manter o valor atual):");

        String nome = ConsoleUtils.lerString("Nome [" + produto.getNome() + "]: ");
        if (!nome.isEmpty()) {
            produto.setNome(nome);
        }

        String descricao = ConsoleUtils.lerString("Descricao [" + produto.getDescricao() + "]: ");
        if (!descricao.isEmpty()) {
            produto.setDescricao(descricao);
        }

        String valorStr = ConsoleUtils.lerString("Valor Padrao [R$ " + String.format("%.2f", produto.getValorPadrao()) + "]: ");
        if (!valorStr.isEmpty()) {
            try {
                BigDecimal novoValor = new BigDecimal(valorStr.replace(",", "."));
                produto.setValorPadrao(novoValor);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Valor invalido. Mantendo valor atual.");
            }
        }

        try {
            Produto produtoAtualizado = produtoService.atualizarProduto(produto);
            System.out.println("\nSUCESSO: Produto atualizado com sucesso!");
            System.out.println("Nome: " + produtoAtualizado.getNome());
            System.out.println("Descricao: " + produtoAtualizado.getDescricao());
            System.out.println("Valor Padrao: R$ " + String.format("%.2f", produtoAtualizado.getValorPadrao()));
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
                    throw new IllegalArgumentException("Valor e obrigatorio.");
                }
                input = input.replace(",", ".");
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Por favor, digite um valor valido (ex: 10.50 ou 10,50).");
            } catch (IllegalArgumentException e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
    }
}