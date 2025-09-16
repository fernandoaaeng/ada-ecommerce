package br.com.ada.ecommerce.services;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.repositories.ClienteRepository;
import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService() {
        this.clienteRepository = new ClienteRepository();
    }

    public Cliente cadastrarCliente(String nome, String documento, String email, String telefone) {
        if (clienteRepository.findByDocumento(documento).isPresent()) {
            throw new IllegalArgumentException("Ja existe um cliente cadastrado com este documento.");
        }

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento e obrigatorio.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e obrigatorio.");
        }

        Cliente cliente = new Cliente(nome.trim(), documento.trim(), email.trim(), telefone);
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarClientePorDocumento(String documento) {
        return clienteRepository.findByDocumento(documento);
    }

    public Cliente atualizarCliente(Cliente cliente) {
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("ID do cliente e obrigatorio para atualizacao.");
        }

        Optional<Cliente> clienteExistente = clienteRepository.findById(cliente.getId());
        if (clienteExistente.isEmpty()) {
            throw new IllegalArgumentException("Cliente nao encontrado.");
        }

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (cliente.getDocumento() == null || cliente.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("Documento e obrigatorio.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email e obrigatorio.");
        }

        Optional<Cliente> clienteComMesmoDocumento = clienteRepository.findByDocumento(cliente.getDocumento());
        if (clienteComMesmoDocumento.isPresent() && !clienteComMesmoDocumento.get().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("Ja existe outro cliente cadastrado com este documento.");
        }

        cliente.setNome(cliente.getNome().trim());
        cliente.setDocumento(cliente.getDocumento().trim());
        cliente.setEmail(cliente.getEmail().trim());
        if (cliente.getTelefone() != null) {
            cliente.setTelefone(cliente.getTelefone().trim());
        }

        return clienteRepository.update(cliente);
    }
}