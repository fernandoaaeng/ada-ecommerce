package br.com.ada.ecommerce.services;

import br.com.ada.ecommerce.models.Produto;
import br.com.ada.ecommerce.repositories.ProdutoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService() {
        this.produtoRepository = new ProdutoRepository();
    }

    public Produto cadastrarProduto(String nome, String descricao, BigDecimal valorPadrao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (valorPadrao == null || valorPadrao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor padrao deve ser maior ou igual a zero.");
        }

        Produto produto = new Produto(nome.trim(), descricao != null ? descricao.trim() : "", valorPadrao);
        return produtoRepository.save(produto);
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public Produto atualizarProduto(Produto produto) {
        if (produto.getId() == null) {
            throw new IllegalArgumentException("ID do produto e obrigatorio para atualizacao.");
        }

        Optional<Produto> produtoExistente = produtoRepository.findById(produto.getId());
        if (produtoExistente.isEmpty()) {
            throw new IllegalArgumentException("Produto nao encontrado.");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (produto.getValorPadrao() == null || produto.getValorPadrao().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor padrao deve ser maior ou igual a zero.");
        }

        produto.setNome(produto.getNome().trim());
        produto.setDescricao(produto.getDescricao() != null ? produto.getDescricao().trim() : "");

        return produtoRepository.update(produto);
    }
}