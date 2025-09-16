package br.com.ada.ecommerce.cli;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.services.ClienteService;
import java.util.List;
import java.util.Optional;

public class ClienteCLI {
    private final ClienteService clienteService;

    public ClienteCLI() {
        this.clienteService = new ClienteService();
    }

    public void executar() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Gestao de Clientes");

            String[] opcoes = {
                "Cadastrar Cliente",
                "Listar Clientes",
                "Buscar Clientes",
                "Atualizar Cliente"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> cadastrarCliente();
                case 2 -> listarClientes();
                case 3 -> menuBuscaClientes();
                case 4 -> atualizarCliente();
                case 0 -> { return; }
            }
        }
    }

    private void cadastrarCliente() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Cadastro de Cliente");

        try {
            String nome = ConsoleUtils.lerString("Nome: ");
            String documento = ConsoleUtils.lerString("Documento (CPF/CNPJ): ");
            String email = ConsoleUtils.lerString("Email: ");
            String telefone = ConsoleUtils.lerString("Telefone (opcional): ");

            Cliente cliente = clienteService.cadastrarCliente(nome, documento, email, telefone);
            
            System.out.println("\nSUCESSO: Cliente cadastrado com sucesso!");
            System.out.println("ID: " + cliente.getId());
            System.out.println("Nome: " + cliente.getNome());
            System.out.println("Documento: " + cliente.getDocumento());
            System.out.println("Email: " + cliente.getEmail());

        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void listarClientes() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Lista de Clientes");

        List<Cliente> clientes = clienteService.listarClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            System.out.println("Total de clientes: " + clientes.size() + "\n");
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nome: " + cliente.getNome());
                System.out.println("Documento: " + cliente.getDocumento());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
                System.out.println("Data Cadastro: " + cliente.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void menuBuscaClientes() {
        while (true) {
            ConsoleUtils.limparConsole();
            ConsoleUtils.exibirCabecalho("Buscar Clientes");

            String[] opcoes = {
                "Buscar por ID",
                "Buscar por Documento (exato)",
                "Buscar por Email",
                "Buscar por Nome",
                "Buscar por Telefone",
                "Buscar por Documento (parcial)"
            };

            ConsoleUtils.exibirMenu(opcoes);
            int opcao = ConsoleUtils.lerOpcao(opcoes.length);

            switch (opcao) {
                case 1 -> buscarClientePorId();
                case 2 -> buscarClientePorDocumento();
                case 3 -> buscarClientePorEmail();
                case 4 -> buscarClientesPorNome();
                case 5 -> buscarClientesPorTelefone();
                case 6 -> buscarClientesPorDocumentoContendo();
                case 0 -> { return; }
            }
        }
    }

    private void buscarClientePorId() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Cliente por ID");

        Long id = ConsoleUtils.lerLong("Digite o ID do cliente: ");
        if (id == null) {
            System.out.println("ERRO: ID e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Cliente> cliente = clienteService.buscarClientePorId(id);

        if (cliente.isPresent()) {
            System.out.println("\nSUCESSO: Cliente encontrado:");
            System.out.println("ID: " + cliente.get().getId());
            System.out.println("Nome: " + cliente.get().getNome());
            System.out.println("Documento: " + cliente.get().getDocumento());
            System.out.println("Email: " + cliente.get().getEmail());
            System.out.println("Telefone: " + (cliente.get().getTelefone() != null ? cliente.get().getTelefone() : "Nao informado"));
            System.out.println("Data Cadastro: " + cliente.get().getDataCadastro());
        } else {
            System.out.println("\nERRO: Cliente nao encontrado.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarClientePorDocumento() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Cliente por Documento");

        String documento = ConsoleUtils.lerString("Digite o documento do cliente: ");
        if (documento.isEmpty()) {
            System.out.println("ERRO: Documento e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Cliente> cliente = clienteService.buscarClientePorDocumento(documento);

        if (cliente.isPresent()) {
            System.out.println("\nSUCESSO: Cliente encontrado:");
            System.out.println("ID: " + cliente.get().getId());
            System.out.println("Nome: " + cliente.get().getNome());
            System.out.println("Documento: " + cliente.get().getDocumento());
            System.out.println("Email: " + cliente.get().getEmail());
            System.out.println("Telefone: " + (cliente.get().getTelefone() != null ? cliente.get().getTelefone() : "Nao informado"));
            System.out.println("Data Cadastro: " + cliente.get().getDataCadastro());
        } else {
            System.out.println("\nERRO: Cliente nao encontrado.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarClientePorEmail() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Cliente por Email");

        String email = ConsoleUtils.lerString("Digite o email do cliente: ");
        if (email.isEmpty()) {
            System.out.println("ERRO: Email e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Cliente> cliente = clienteService.buscarClientePorEmail(email);

        if (cliente.isPresent()) {
            System.out.println("\nSUCESSO: Cliente encontrado:");
            System.out.println("ID: " + cliente.get().getId());
            System.out.println("Nome: " + cliente.get().getNome());
            System.out.println("Documento: " + cliente.get().getDocumento());
            System.out.println("Email: " + cliente.get().getEmail());
            System.out.println("Telefone: " + (cliente.get().getTelefone() != null ? cliente.get().getTelefone() : "Nao informado"));
            System.out.println("Data Cadastro: " + cliente.get().getDataCadastro());
        } else {
            System.out.println("\nERRO: Cliente nao encontrado.");
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarClientesPorNome() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Clientes por Nome");

        String nome = ConsoleUtils.lerString("Digite o nome (ou parte do nome) do cliente: ");
        if (nome.isEmpty()) {
            System.out.println("ERRO: Nome e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Cliente> clientes = clienteService.buscarClientesPorNome(nome);

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com o nome: " + nome);
        } else {
            System.out.println("Clientes encontrados (" + clientes.size() + "):\n");
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nome: " + cliente.getNome());
                System.out.println("Documento: " + cliente.getDocumento());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
                System.out.println("Data Cadastro: " + cliente.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarClientesPorTelefone() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Clientes por Telefone");

        String telefone = ConsoleUtils.lerString("Digite o telefone (ou parte do telefone) do cliente: ");
        if (telefone.isEmpty()) {
            System.out.println("ERRO: Telefone e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Cliente> clientes = clienteService.buscarClientesPorTelefone(telefone);

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com o telefone: " + telefone);
        } else {
            System.out.println("Clientes encontrados (" + clientes.size() + "):\n");
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nome: " + cliente.getNome());
                System.out.println("Documento: " + cliente.getDocumento());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
                System.out.println("Data Cadastro: " + cliente.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void buscarClientesPorDocumentoContendo() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Buscar Clientes por Documento (Parcial)");

        String documento = ConsoleUtils.lerString("Digite parte do documento (CPF/CNPJ): ");
        if (documento.isEmpty()) {
            System.out.println("ERRO: Documento e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        List<Cliente> clientes = clienteService.buscarClientesPorDocumentoContendo(documento);

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com documento contendo: " + documento);
        } else {
            System.out.println("Clientes encontrados (" + clientes.size() + "):\n");
            for (Cliente cliente : clientes) {
                System.out.println("ID: " + cliente.getId());
                System.out.println("Nome: " + cliente.getNome());
                System.out.println("Documento: " + cliente.getDocumento());
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));
                System.out.println("Data Cadastro: " + cliente.getDataCadastro());
                System.out.println("-".repeat(40));
            }
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }

    private void atualizarCliente() {
        ConsoleUtils.limparConsole();
        ConsoleUtils.exibirCabecalho("Atualizar Cliente");

        Long id = ConsoleUtils.lerLong("Digite o ID do cliente a ser atualizado: ");
        if (id == null) {
            System.out.println("ERRO: ID e obrigatorio.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(id);
        if (clienteOpt.isEmpty()) {
            System.out.println("ERRO: Cliente nao encontrado.");
            ConsoleUtils.pausar("\nPressione Enter para continuar...");
            return;
        }

        Cliente cliente = clienteOpt.get();
        System.out.println("\nDados atuais do cliente:");
        System.out.println("Nome: " + cliente.getNome());
        System.out.println("Documento: " + cliente.getDocumento());
        System.out.println("Email: " + cliente.getEmail());
        System.out.println("Telefone: " + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado"));

        System.out.println("\nDigite os novos dados (deixe em branco para manter o valor atual):");

        String nome = ConsoleUtils.lerString("Nome [" + cliente.getNome() + "]: ");
        if (!nome.isEmpty()) {
            cliente.setNome(nome);
        }

        String documento = ConsoleUtils.lerString("Documento [" + cliente.getDocumento() + "]: ");
        if (!documento.isEmpty()) {
            cliente.setDocumento(documento);
        }

        String email = ConsoleUtils.lerString("Email [" + cliente.getEmail() + "]: ");
        if (!email.isEmpty()) {
            cliente.setEmail(email);
        }

        String telefone = ConsoleUtils.lerString("Telefone [" + (cliente.getTelefone() != null ? cliente.getTelefone() : "Nao informado") + "]: ");
        if (!telefone.isEmpty()) {
            cliente.setTelefone(telefone);
        }

        try {
            Cliente clienteAtualizado = clienteService.atualizarCliente(cliente);
            System.out.println("\nSUCESSO: Cliente atualizado com sucesso!");
            System.out.println("Nome: " + clienteAtualizado.getNome());
            System.out.println("Documento: " + clienteAtualizado.getDocumento());
            System.out.println("Email: " + clienteAtualizado.getEmail());
            System.out.println("Telefone: " + (clienteAtualizado.getTelefone() != null ? clienteAtualizado.getTelefone() : "Nao informado"));
        } catch (Exception e) {
            System.out.println("\nERRO: " + e.getMessage());
        }

        ConsoleUtils.pausar("\nPressione Enter para continuar...");
    }
}