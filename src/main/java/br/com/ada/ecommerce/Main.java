package br.com.ada.ecommerce;

import br.com.ada.ecommerce.cli.ConsoleUtils;
import br.com.ada.ecommerce.cli.ClienteCLI;
import br.com.ada.ecommerce.cli.ProdutoCLI;
import br.com.ada.ecommerce.cli.PedidoCLI;
import br.com.ada.ecommerce.cli.VendaCLI;
import br.com.ada.ecommerce.services.PedidoService;

public class Main {
    public static void main(String[] args) {
        try {
            executarSistema();
        } catch (Exception e) {
            System.err.println("ERRO: Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ConsoleUtils.fecharScanner();
        }
    }

    private static void executarSistema() {
        // Criar instâncias únicas dos services
        PedidoService pedidoService = new PedidoService();
        
        ClienteCLI clienteCLI = new ClienteCLI();
        ProdutoCLI produtoCLI = new ProdutoCLI();
        PedidoCLI pedidoCLI = new PedidoCLI(pedidoService);
        VendaCLI vendaCLI = new VendaCLI(pedidoService);

        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Sistema de E-Commerce");

            System.out.println("Bem-vindo ao Sistema de E-Commerce da Ada Tech!");
            System.out.println("Gerencie clientes, produtos e pedidos de forma eficiente.");
            System.out.println("Todos os dados sao persistidos em arquivos CSV.");

            String[] opcoes = {
                "Realizar Venda (Fluxo Simplificado)",
                "Gestao de Clientes",
                "Gestao de Produtos", 
                "Gestao de Pedidos"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> vendaCLI.executar();
                case 2 -> clienteCLI.executar();
                case 3 -> produtoCLI.executar();
                case 4 -> pedidoCLI.executar();
                case 0 -> {
                    ConsoleUtils.limparConsole();
                    System.out.println("=".repeat(60));
                    System.out.println("OBRIGADO POR USAR O SISTEMA ADA TECH E-COMMERCE!");
                    System.out.println("=".repeat(60));
                    System.out.println("Todos os dados foram salvos nos arquivos CSV:");
                    System.out.println("   clientes.csv");
                    System.out.println("   produtos.csv");
                    System.out.println("   pedidos.csv");
                    System.out.println("   itens_pedido.csv");
                    System.out.println("=".repeat(60));
                    System.out.println("Ate a proxima!");
                    System.out.println("=".repeat(60));
                    return;
                }
            }
        }
    }
}