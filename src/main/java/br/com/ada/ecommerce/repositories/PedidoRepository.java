package br.com.ada.ecommerce.repositories;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.ada.ecommerce.models.Pedido;
import br.com.ada.ecommerce.models.StatusPedido;
import br.com.ada.ecommerce.utils.CsvUtils;

public class PedidoRepository {
    private static final String CSV_FILE = "data/pedidos.csv";
    private static Long nextId = 1L;

    public PedidoRepository() {
        initializeCsv();
    }

    private void initializeCsv() {
        try {
            List<String[]> existingData = CsvUtils.readCsv(CSV_FILE);
            if (existingData.isEmpty()) {
                String[] header = {"id", "clienteId", "status", "valorTotal", "dataCriacao", "dataFinalizacao", "dataPagamento", "dataEntrega"};
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

    public Pedido save(Pedido pedido) {
        try {
            if (pedido.getId() == null) {
                pedido.setId(nextId++);
            }
            
            String[] data = {
                pedido.getId().toString(),
                pedido.getClienteId().toString(),
                pedido.getStatus().name(),
                pedido.getValorTotal().toString(),
                CsvUtils.formatDateTime(pedido.getDataCriacao()),
                CsvUtils.formatDateTime(pedido.getDataFinalizacao()),
                CsvUtils.formatDateTime(pedido.getDataPagamento()),
                CsvUtils.formatDateTime(pedido.getDataEntrega())
            };
            
            CsvUtils.appendToCsv(CSV_FILE, data);
            return pedido;
        } catch (IOException e) {
            System.err.println("Erro ao salvar pedido: " + e.getMessage());
            return null;
        }
    }

    public List<Pedido> findAll() {
        List<Pedido> pedidos = new ArrayList<>();
        try {
            List<String[]> records = CsvUtils.readCsv(CSV_FILE);
            for (int i = 1; i < records.size(); i++) {
                String[] row = records.get(i);
                if (row.length >= 8) {
                    Pedido pedido = new Pedido();
                    pedido.setId(Long.parseLong(row[0]));
                    pedido.setClienteId(Long.parseLong(row[1]));
                    pedido.setStatus(StatusPedido.valueOf(row[2]));
                    pedido.setValorTotal(new BigDecimal(row[3]));
                    pedido.setDataCriacao(CsvUtils.parseDateTime(row[4]));
                    pedido.setDataFinalizacao(CsvUtils.parseDateTime(row[5]));
                    pedido.setDataPagamento(CsvUtils.parseDateTime(row[6]));
                    pedido.setDataEntrega(CsvUtils.parseDateTime(row[7]));
                    pedidos.add(pedido);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler pedidos: " + e.getMessage());
        }
        return pedidos;
    }

    public Optional<Pedido> findById(Long id) {
        return findAll().stream()
                .filter(pedido -> pedido.getId().equals(id))
                .findFirst();
    }

    public List<Pedido> findByClienteId(Long clienteId) {
        return findAll().stream()
                .filter(pedido -> pedido.getClienteId().equals(clienteId))
                .toList();
    }

    public List<Pedido> findByStatus(StatusPedido status) {
        return findAll().stream()
                .filter(pedido -> pedido.getStatus().equals(status))
                .toList();
    }

    public List<Pedido> findByValorTotalMaiorQue(java.math.BigDecimal valor) {
        return findAll().stream()
                .filter(pedido -> pedido.getValorTotal().compareTo(valor) > 0)
                .toList();
    }

    public List<Pedido> findByValorTotalMenorQue(java.math.BigDecimal valor) {
        return findAll().stream()
                .filter(pedido -> pedido.getValorTotal().compareTo(valor) < 0)
                .toList();
    }

    public List<Pedido> findByValorTotalEntre(java.math.BigDecimal valorMinimo, java.math.BigDecimal valorMaximo) {
        return findAll().stream()
                .filter(pedido -> pedido.getValorTotal().compareTo(valorMinimo) >= 0 && 
                                 pedido.getValorTotal().compareTo(valorMaximo) <= 0)
                .toList();
    }

    public Pedido update(Pedido pedido) {
        List<Pedido> pedidos = findAll();
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId().equals(pedido.getId())) {
                pedidos.set(i, pedido);
                saveAll(pedidos);
                return pedido;
            }
        }
        return null;
    }

    private void saveAll(List<Pedido> pedidos) {
        try {
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"id", "clienteId", "status", "valorTotal", "dataCriacao", "dataFinalizacao", "dataPagamento", "dataEntrega"});
            
            for (Pedido pedido : pedidos) {
                String[] row = {
                    pedido.getId().toString(),
                    pedido.getClienteId().toString(),
                    pedido.getStatus().name(),
                    pedido.getValorTotal().toString(),
                    CsvUtils.formatDateTime(pedido.getDataCriacao()),
                    CsvUtils.formatDateTime(pedido.getDataFinalizacao()),
                    CsvUtils.formatDateTime(pedido.getDataPagamento()),
                    CsvUtils.formatDateTime(pedido.getDataEntrega())
                };
                data.add(row);
            }
            
            CsvUtils.writeCsv(CSV_FILE, data);
        } catch (IOException e) {
            System.err.println("Erro ao atualizar pedidos: " + e.getMessage());
        }
    }
}