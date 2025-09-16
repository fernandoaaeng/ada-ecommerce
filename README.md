# Ada Tech E-Commerce

Sistema de E-Commerce desenvolvido em Java 21 para gerenciamento de clientes, produtos e pedidos via linha de comando (CLI).

## Funcionalidades

### Fluxo de Venda Simplificado (Novo!)
- Venda guiada passo a passo - Fluxo otimizado para vendas rapidas
- Selecao de cliente - Escolha entre clientes cadastrados
- Criacao automatica de pedido - Sistema cria pedido automaticamente
- Adicao de produtos - Interface intuitiva para adicionar itens
- Finalizacao completa - Pagamento e entrega em sequencia
- Experiencia otimizada - Assumindo que clientes e produtos ja estao cadastrados

### Gestao de Clientes
- Cadastrar novos clientes (nome, documento obrigatorio, email, telefone)
- Listar clientes cadastrados
- Busca Avancada:
  - Buscar por ID
  - Buscar por documento (exato)
  - Buscar por email
  - Buscar por nome (parcial)
  - Buscar por telefone
  - Buscar por documento (parcial)
- Atualizar dados de clientes
- Exclusao nao permitida (mantem historico)

### Gestao de Produtos
- Cadastrar novos produtos (nome, descricao, valor padrao)
- Listar produtos cadastrados
- Busca Avancada:
  - Buscar por ID
  - Buscar por nome (parcial)
  - Buscar por descricao (parcial)
- Atualizar dados dos produtos
- Exclusao nao permitida (mantem historico)

### Gestao de Pedidos
- Criar pedido para um cliente
- Adicionar/remover itens ao pedido
- Alterar quantidade de itens
- Finalizar pedido (status: "Aguardando pagamento")
- Realizar pagamento (status: "Pago")
- Realizar entrega (status: "Finalizado")
- Busca Avancada:
  - Buscar por ID
  - Buscar por cliente
  - Buscar por status
  - Buscar por valor (maior que, menor que, entre valores)
- Notificacoes automaticas ao cliente

### Sistema de Busca Aprimorado
- Menus organizados - Buscas agrupadas em submenus para melhor navegacao
- Busca parcial - Encontre registros digitando apenas parte do nome/descricao
- Busca por valores - Filtre pedidos por faixa de preco
- Busca por status - Visualize pedidos por estado (Aberto, Pago, Finalizado, etc.)
- Busca case-insensitive - Funciona independente de maiusculas/minusculas

## Como Executar

### Pre-requisitos
- Java 21 ou superior instalado
- Command Prompt ou PowerShell

### Execucao Rapida (Recomendado)
**Duplo clique no arquivo `start.bat`** ou execute no Command Prompt:
```cmd
start.bat
```

### Execucao Manual no Windows

1. **Abra o Command Prompt ou PowerShell** no diretorio do projeto

2. **Compilar o projeto:**
   ```cmd
   mkdir build
   javac -d build -cp src/main/java src/main/java/br/com/ada/ecommerce/Main.java src/main/java/br/com/ada/ecommerce/models/*.java src/main/java/br/com/ada/ecommerce/utils/*.java src/main/java/br/com/ada/ecommerce/repositories/*.java src/main/java/br/com/ada/ecommerce/services/*.java src/main/java/br/com/ada/ecommerce/cli/*.java
   ```

3. **Executar o sistema:**
   ```cmd
   java -cp build br.com.ada.ecommerce.Main
   ```

### Alternativa: Execucao com PowerShell
Se preferir usar PowerShell:
```powershell
# Compilar
New-Item -ItemType Directory -Path "build" -Force
javac -d build -cp src/main/java src/main/java/br/com/ada/ecommerce/Main.java src/main/java/br/com/ada/ecommerce/models/*.java src/main/java/br/com/ada/ecommerce/utils/*.java src/main/java/br/com/ada/ecommerce/repositories/*.java src/main/java/br/com/ada/ecommerce/services/*.java src/main/java/br/com/ada/ecommerce/cli/*.java

# Executar
java -cp build br.com.ada.ecommerce.Main
```

## Estrutura do Projeto

```
src/main/java/br/com/ada/ecommerce/
├── Main.java                          # Classe principal
├── models/                            # Modelos de dados
│   ├── Cliente.java
│   ├── Produto.java
│   ├── Pedido.java
│   ├── ItemPedido.java
│   └── StatusPedido.java
├── repositories/                      # Persistencia em CSV
│   ├── ClienteRepository.java
│   ├── ProdutoRepository.java
│   ├── PedidoRepository.java
│   └── ItemPedidoRepository.java
├── services/                          # Logica de negocio
│   ├── ClienteService.java
│   ├── ProdutoService.java
│   ├── PedidoService.java
│   └── NotificationService.java
├── cli/                              # Interface de linha de comando
│   ├── ConsoleUtils.java
│   ├── ClienteCLI.java
│   ├── ProdutoCLI.java
│   ├── PedidoCLI.java
│   └── VendaCLI.java                 # Fluxo de venda simplificado
└── utils/                            # Utilitarios
    └── CsvUtils.java
```

## Persistencia de Dados

O sistema utiliza arquivos CSV para persistir os dados na pasta `data/`:

- **`data/clientes.csv`** - Dados dos clientes (10 clientes de exemplo)
- **`data/produtos.csv`** - Dados dos produtos (20 produtos de exemplo)
- **`data/pedidos.csv`** - Dados dos pedidos (10 pedidos de exemplo)
- **`data/itens_pedido.csv`** - Itens de cada pedido (21 itens de exemplo)

Os arquivos sao criados automaticamente na primeira execucao do sistema com dados de exemplo para facilitar os testes.

## Sistema de Notificacoes

O sistema simula notificacoes ao cliente atraves de mensagens no console:

- **Pedido Finalizado** - Quando o pedido muda para "Aguardando pagamento"
- **Pagamento Confirmado** - Quando o pagamento e realizado
- **Entrega Realizada** - Quando o pedido e entregue

## Como Usar

1. **Execute o sistema** usando um dos comandos acima
2. **Navegue pelos menus** usando os numeros das opcoes
3. **Use a opcao 0** para voltar ou sair
4. **Siga as instrucoes** na tela para cada operacao

### Exemplo de Fluxo Rapido (Recomendado):
1. Execute o sistema e escolha "1 - Realizar Venda (Fluxo Simplificado)"
2. Selecione um cliente da lista (dados ja pre-cadastrados)
3. Adicione produtos ao pedido (produtos ja cadastrados)
4. Finalize o pedido automaticamente
5. Realize o pagamento 
6. Confirme a entrega
7. Venda concluida!

### Exemplo de Fluxo Completo:
1. Cadastre um cliente (se necessario)
2. Cadastre alguns produtos (se necessario)
3. Crie um pedido para o cliente
4. Adicione itens ao pedido
5. Finalize o pedido
6. Realize o pagamento
7. Realize a entrega
8. Observe as notificacoes ao cliente



