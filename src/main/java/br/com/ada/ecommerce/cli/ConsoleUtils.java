package br.com.ada.ecommerce.cli;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.models.Pedido;
import br.com.ada.ecommerce.models.Produto;
import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {
    private static Scanner scanner = new Scanner(System.in);

    public static void limparConsole() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static Long lerLong(String prompt) {
        while (true) {
            try {
                String input = lerString(prompt);
                if (input.isEmpty()) {
                    return null;
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Por favor, digite um numero valido.");
            }
        }
    }

    public static Integer lerInt(String prompt) {
        while (true) {
            try {
                String input = lerString(prompt);
                if (input.isEmpty()) {
                    return null;
                }
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Por favor, digite um numero inteiro valido.");
            }
        }
    }

    public static void pausar(String mensagem) {
        System.out.println("\n" + mensagem);
        scanner.nextLine();
    }

    public static void exibirCabecalho(String titulo) {
        System.out.println("=".repeat(60));
        System.out.println("ADA TECH E-COMMERCE - " + titulo.toUpperCase());
        System.out.println("=".repeat(60));
    }

    public static void exibirMenu(String[] opcoes) {
        System.out.println("\nMENU:");
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + ". " + opcoes[i]);
        }
        System.out.println("0. Voltar/Sair");
        System.out.print("\n-> Digite sua opcao: ");
    }

    public static int lerOpcao(int maxOpcoes) {
        while (true) {
            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());
                if (opcao >= 0 && opcao <= maxOpcoes) {
                    return opcao;
                } else {
                    System.out.println("ERRO: Opcao invalida. Digite um numero entre 0 e " + maxOpcoes);
                    System.out.print("-> Digite sua opcao: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("ERRO: Por favor, digite um numero valido.");
                System.out.print("-> Digite sua opcao: ");
            }
        }
    }

    public static void fecharScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public static void mostrarClientes(List<Cliente> clientes) {
        System.out.println("CLIENTES DISPONIVEIS:");
        System.out.println("-".repeat(70));
        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId() + " - " + cliente.getNome() + 
                                 " (Doc: " + cliente.getDocumento() + ", Email: " + cliente.getEmail() + ")");
            }
        }
        System.out.println("-".repeat(70));
    }

    public static void mostrarProdutos(List<Produto> produtos) {
        System.out.println("PRODUTOS DISPONIVEIS:");
        System.out.println("-".repeat(80));
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            for (Produto produto : produtos) {
                System.out.println("ID: " + produto.getId() + " - " + produto.getNome() + 
                                 " | Descricao: " + produto.getDescricao() + 
                                 " | Valor: R$ " + String.format("%.2f", produto.getValorPadrao()));
            }
        }
        System.out.println("-".repeat(80));
    }

    public static void mostrarPedidos(List<Pedido> pedidos) {
        System.out.println("PEDIDOS DISPONIVEIS:");
        System.out.println("-".repeat(80));
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
        } else {
            for (Pedido pedido : pedidos) {
                System.out.println("ID: " + pedido.getId() + " - Cliente ID: " + pedido.getClienteId() + 
                                 " | Status: " + pedido.getStatus().getDescricao() + 
                                 " | Total: R$ " + String.format("%.2f", pedido.getValorTotal()) + 
                                 " | Data: " + pedido.getDataCriacao());
            }
        }
        System.out.println("-".repeat(80));
    }
}