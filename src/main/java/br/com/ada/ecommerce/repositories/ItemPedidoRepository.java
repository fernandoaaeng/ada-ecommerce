package br.com.ada.ecommerce.repositories;

import br.com.ada.ecommerce.models.ItemPedido;
import br.com.ada.ecommerce.utils.CsvUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPedidoRepository {
    private static final String CSV_FILE = "data/itens_pedido.csv";
    private static Long nextId = 1L;

    public ItemPedidoRepository() {
        initializeCsv();
    }

    private void initializeCsv() {
        try {
            List<String[]> existingData = CsvUtils.readCsv(CSV_FILE);
            if (existingData.isEmpty()) {
                String[] header = {"id", "pedidoId", "produtoId", "nomeProduto", "quantidade", "precoVenda"};
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

    public ItemPedido save(ItemPedido itemPedido) {
        try {
            if (itemPedido.getId() == null) {
                itemPedido.setId(nextId++);
            }
            
            String[] data = {
                itemPedido.getId().toString(),
                itemPedido.getPedidoId().toString(),
                itemPedido.getProdutoId().toString(),
                CsvUtils.escapeCsv(itemPedido.getNomeProduto()),
                itemPedido.getQuantidade().toString(),
                itemPedido.getPrecoVenda().toString()
            };
            
            CsvUtils.appendToCsv(CSV_FILE, data);
            return itemPedido;
        } catch (IOException e) {
            System.err.println("Erro ao salvar item do pedido: " + e.getMessage());
            return null;
        }
    }

    public List<ItemPedido> findAll() {
        List<ItemPedido> itens = new ArrayList<>();
        try {
            List<String[]> records = CsvUtils.readCsv(CSV_FILE);
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                if (row.length >= 6) {
                    ItemPedido item = new ItemPedido();
                    item.setId(Long.parseLong(row[0]));
                    item.setPedidoId(Long.parseLong(row[1]));
                    item.setProdutoId(Long.parseLong(row[2]));
                    item.setNomeProduto(row[3]);
                    item.setQuantidade(Integer.parseInt(row[4]));
                    item.setPrecoVenda(new BigDecimal(row[5]));
                    itens.add(item);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler itens do pedido: " + e.getMessage());
        }
        return itens;
    }

    public Optional<ItemPedido> findById(Long id) {
        return findAll().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    public List<ItemPedido> findByPedidoId(Long pedidoId) {
        return findAll().stream()
                .filter(item -> item.getPedidoId().equals(pedidoId))
                .toList();
    }

    public ItemPedido update(ItemPedido itemPedido) {
        List<ItemPedido> itens = findAll();
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).getId().equals(itemPedido.getId())) {
                itens.set(i, itemPedido);
                saveAll(itens);
                return itemPedido;
            }
        }
        return null;
    }

    public boolean delete(Long id) {
        List<ItemPedido> itens = findAll();
        boolean removed = itens.removeIf(item -> item.getId().equals(id));
        if (removed) {
            saveAll(itens);
        }
        return removed;
    }

    private void saveAll(List<ItemPedido> itens) {
        try {
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"id", "pedidoId", "produtoId", "nomeProduto", "quantidade", "precoVenda"});
            
            for (ItemPedido item : itens) {
                String[] row = {
                    item.getId().toString(),
                    item.getPedidoId().toString(),
                    item.getProdutoId().toString(),
                    CsvUtils.escapeCsv(item.getNomeProduto()),
                    item.getQuantidade().toString(),
                    item.getPrecoVenda().toString()
                };
                data.add(row);
            }
            
            CsvUtils.writeCsv(CSV_FILE, data);
        } catch (IOException e) {
            System.err.println("Erro ao atualizar itens do pedido: " + e.getMessage());
        }
    }
}