package br.com.ada.ecommerce.repositories;

import br.com.ada.ecommerce.models.Cliente;
import br.com.ada.ecommerce.utils.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {
    private static final String CSV_FILE = "data/clientes.csv";
    private static Long nextId = 1L;

    public ClienteRepository() {
        initializeCsv();
    }

    private void initializeCsv() {
        try {
            List<String[]> existingData = CsvUtils.readCsv(CSV_FILE);
            if (existingData.isEmpty()) {
                String[] header = {"id", "nome", "documento", "email", "telefone", "dataCadastro"};
                List<String[]> headerList = new ArrayList<>();
                headerList.add(header);
                CsvUtils.writeCsv(CSV_FILE, headerList);
            } else {
                for (String[] row : existingData) {
                    if (row.length > 0 && !row[0].equals("id")) {
                        try {
                            Long id = Long.parseLong(row[0]);
                            if (id >= nextId) {
                                nextId = id + 1;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao inicializar arquivo CSV: " + e.getMessage());
        }
    }

    public Cliente save(Cliente cliente) {
        try {
            if (cliente.getId() == null) {
                cliente.setId(nextId++);
            }
            
            String[] data = {
                cliente.getId().toString(),
                cliente.getNome(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cliente.getTelefone(),
                CsvUtils.formatDateTime(cliente.getDataCadastro())
            };
            
            CsvUtils.appendToCsv(CSV_FILE, data);
            return cliente;
        } catch (IOException e) {
            System.err.println("Erro ao salvar cliente: " + e.getMessage());
            return null;
        }
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        try {
            List<String[]> records = CsvUtils.readCsv(CSV_FILE);
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                if (row.length >= 6) {
                    Cliente cliente = new Cliente();
                    cliente.setId(Long.parseLong(row[0]));
                    cliente.setNome(row[1]);
                    cliente.setDocumento(row[2]);
                    cliente.setEmail(row[3]);
                    cliente.setTelefone(row[4]);
                    cliente.setDataCadastro(CsvUtils.parseDateTime(row[5]));
                    clientes.add(cliente);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler clientes: " + e.getMessage());
        }
        return clientes;
    }

    public Optional<Cliente> findById(Long id) {
        return findAll().stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst();
    }

    public Optional<Cliente> findByDocumento(String documento) {
        return findAll().stream()
                .filter(cliente -> cliente.getDocumento().equals(documento))
                .findFirst();
    }

    public Cliente update(Cliente cliente) {
        List<Cliente> clientes = findAll();
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId().equals(cliente.getId())) {
                clientes.set(i, cliente);
                saveAll(clientes);
                return cliente;
            }
        }
        return null;
    }

    private void saveAll(List<Cliente> clientes) {
        try {
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"id", "nome", "documento", "email", "telefone", "dataCadastro"});
            
            for (Cliente cliente : clientes) {
                String[] row = {
                    cliente.getId().toString(),
                    cliente.getNome(),
                    cliente.getDocumento(),
                    cliente.getEmail(),
                    cliente.getTelefone(),
                    CsvUtils.formatDateTime(cliente.getDataCadastro())
                };
                data.add(row);
            }
            
            CsvUtils.writeCsv(CSV_FILE, data);
        } catch (IOException e) {
            System.err.println("Erro ao atualizar clientes: " + e.getMessage());
        }
    }
}