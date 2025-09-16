package br.com.ada.ecommerce.repositories;

import br.com.ada.ecommerce.models.Produto;
import br.com.ada.ecommerce.utils.CsvUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {
    private static final String CSV_FILE = "produtos.csv";
    private static Long nextId = 1L;

    public ProdutoRepository() {
        initializeCsv();
    }

    private void initializeCsv() {
        try {
            List<String[]> existingData = CsvUtils.readCsv(CSV_FILE);
            if (existingData.isEmpty()) {
                String[] header = {"id", "nome", "descricao", "valorPadrao", "dataCadastro"};
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

    public Produto save(Produto produto) {
        try {
            if (produto.getId() == null) {
                produto.setId(nextId++);
            }
            
            String[] data = {
                produto.getId().toString(),
                CsvUtils.escapeCsv(produto.getNome()),
                CsvUtils.escapeCsv(produto.getDescricao()),
                produto.getValorPadrao().toString(),
                CsvUtils.formatDateTime(produto.getDataCadastro())
            };
            
            CsvUtils.appendToCsv(CSV_FILE, data);
            return produto;
        } catch (IOException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            return null;
        }
    }

    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();
        try {
            List<String[]> records = CsvUtils.readCsv(CSV_FILE);
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                if (row.length >= 5) {
                    Produto produto = new Produto();
                    produto.setId(Long.parseLong(row[0]));
                    produto.setNome(row[1]);
                    produto.setDescricao(row[2]);
                    produto.setValorPadrao(new BigDecimal(row[3]));
                    produto.setDataCadastro(CsvUtils.parseDateTime(row[4]));
                    produtos.add(produto);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler produtos: " + e.getMessage());
        }
        return produtos;
    }

    public Optional<Produto> findById(Long id) {
        return findAll().stream()
                .filter(produto -> produto.getId().equals(id))
                .findFirst();
    }

    public Produto update(Produto produto) {
        List<Produto> produtos = findAll();
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId().equals(produto.getId())) {
                produtos.set(i, produto);
                saveAll(produtos);
                return produto;
            }
        }
        return null;
    }

    private void saveAll(List<Produto> produtos) {
        try {
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"id", "nome", "descricao", "valorPadrao", "dataCadastro"});
            
            for (Produto produto : produtos) {
                String[] row = {
                    produto.getId().toString(),
                    CsvUtils.escapeCsv(produto.getNome()),
                    CsvUtils.escapeCsv(produto.getDescricao()),
                    produto.getValorPadrao().toString(),
                    CsvUtils.formatDateTime(produto.getDataCadastro())
                };
                data.add(row);
            }
            
            CsvUtils.writeCsv(CSV_FILE, data);
        } catch (IOException e) {
            System.err.println("Erro ao atualizar produtos: " + e.getMessage());
        }
    }
}