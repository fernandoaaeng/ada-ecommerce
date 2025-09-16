# Ada Tech E-Commerce

Sistema de E-Commerce desenvolvido em Java 21 para gerenciamento de clientes, produtos e pedidos via linha de comando (CLI).

## Funcionalidades

### Gestao de Clientes
- Cadastrar novos clientes (nome, documento obrigatorio, email, telefone)
- Listar clientes cadastrados
- Buscar cliente por ID ou documento
- Atualizar dados de clientes
- Exclusao nao permitida (mantem historico)

### Gestao de Produtos
- Cadastrar novos produtos (nome, descricao, valor padrao)
- Listar produtos cadastrados
- Buscar produto por ID
- Atualizar dados dos produtos
- Exclusao nao permitida (mantem historico)

### Gestao de Pedidos
- Criar pedido para um cliente
- Adicionar/remover itens ao pedido
- Alterar quantidade de itens
- Finalizar pedido (status: "Aguardando pagamento")
- Realizar pagamento (status: "Pago")
- Realizar entrega (status: "Finalizado")
- Notificacoes automaticas ao cliente

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
│   └── PedidoCLI.java
└── utils/                            # Utilitarios
    └── CsvUtils.java
```

## Persistencia de Dados

O sistema utiliza arquivos CSV para persistir os dados:

- **`clientes.csv`** - Dados dos clientes
- **`produtos.csv`** - Dados dos produtos
- **`pedidos.csv`** - Dados dos pedidos
- **`itens_pedido.csv`** - Itens de cada pedido

Os arquivos sao criados automaticamente na primeira execucao do sistema.

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

### Exemplo de Fluxo:
1. Cadastre um cliente
2. Cadastre alguns produtos
3. Crie um pedido para o cliente
4. Adicione itens ao pedido
5. Finalize o pedido
6. Realize o pagamento
7. Realize a entrega
8. Observe as notificacoes ao cliente



