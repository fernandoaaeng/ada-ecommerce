# Ada E-Commerce

Sistema de E-Commerce CLI desenvolvido para Ada Tech, permitindo gerenciar clientes, produtos e pedidos através de interface de linha de comando.

## Instalação e Execução

### 1. Compile o projeto
```bash
mvn clean compile
```

### 2. Execute a aplicação
```bash
mvn exec:java
```

### 3. (Opcional) Crie um JAR executável
```bash
mvn clean package
java -jar target/ada-ecommerce-1.0.0.jar
```

### Scripts .bat (Recomendado para Windows)
```bash
# Compilar e executar
run.bat

# Apenas executar (se já compilado)
start.bat
```

## Funcionalidades

### Gestão de Clientes
- Cadastrar novos clientes
- Listar todos os clientes
- Buscar cliente por ID ou documento
- Atualizar dados do cliente

### Gestão de Produtos
- Cadastrar novos produtos
- Listar todos os produtos
- Buscar produto por ID
- Atualizar informações do produto

### Gestão de Pedidos
- Criar novos pedidos
- Adicionar/remover produtos do pedido
- Alterar quantidades
- Finalizar pedidos
- Processar pagamentos
- Processar entregas
- Listar pedidos por cliente ou status

### Notificações
- Simulação de notificações via console para:
  - Finalização de pedidos
  - Confirmação de pagamento
  - Confirmação de entrega

## Estrutura do Projeto

```
ada-ecommerce/
├── src/main/java/br/com/ada/ecommerce/
│   ├── Main.java                    # Classe principal
│   ├── models/                      # Modelos de dados
│   │   ├── Cliente.java
│   │   ├── Produto.java
│   │   ├── Pedido.java
│   │   ├── ItemPedido.java
│   │   └── StatusPedido.java
│   ├── repositories/                # Camada de persistência
│   │   ├── ClienteRepository.java
│   │   ├── ProdutoRepository.java
│   │   ├── PedidoRepository.java
│   │   └── ItemPedidoRepository.java
│   ├── services/                    # Lógica de negócio
│   │   ├── ClienteService.java
│   │   ├── ProdutoService.java
│   │   ├── PedidoService.java
│   │   └── NotificationService.java
│   ├── utils/                       # Utilitários
│   │   └── CsvUtils.java
│   └── cli/                         # Interface de linha de comando
│       ├── ConsoleUtils.java
│       ├── ClienteCLI.java
│       ├── ProdutoCLI.java
│       └── PedidoCLI.java
├── data/                            # Diretório de dados
│   ├── clientes.csv                 # Dados dos clientes
│   ├── produtos.csv                 # Catálogo de produtos
│   ├── pedidos.csv                  # Informações dos pedidos
│   └── itens_pedido.csv             # Itens de cada pedido
├── run.bat                          # Script para compilar e executar
├── start.bat                        # Script para apenas executar
├── pom.xml                          # Configuração do Maven
└── README.md
```

## Arquivos de Dados

O sistema utiliza os seguintes arquivos CSV para persistência no diretório `data/`:

- **data/clientes.csv**: Dados dos clientes
- **data/produtos.csv**: Catálogo de produtos
- **data/pedidos.csv**: Informações dos pedidos
- **data/itens_pedido.csv**: Itens de cada pedido

## Comandos Úteis

### Scripts .bat (Recomendado para Windows)
```bash
# Compilar e executar
run.bat

# Apenas executar (se já compilado)
start.bat
```

### Comandos Maven Diretos
```bash
# Compilar o projeto
mvn compile

# Executar a aplicação
mvn exec:java

# Executar testes (quando implementados)
mvn test

# Criar JAR executável
mvn package

# Limpar arquivos compilados
mvn clean

# Instalar no repositório local
mvn install
```

## Tecnologias Utilizadas

- **Java 17**: Linguagem de programação
- **Maven**: Gerenciamento de dependências e build
- **OpenCSV 5.9**: Manipulação de arquivos CSV
- **Console Input/Output**: Interface de linha de comando

## Funcionalidades do Sistema

### Status de Pedidos
- **ABERTO**: Pedido criado, pode receber itens
- **AGUARDANDO_PAGAMENTO**: Pedido finalizado, aguardando pagamento
- **PAGO**: Pagamento confirmado, aguardando entrega
- **FINALIZADO**: Pedido entregue ao cliente

### Fluxo de Trabalho
1. Cadastrar clientes e produtos
2. Criar pedidos para clientes
3. Adicionar produtos aos pedidos
4. Finalizar pedidos
5. Processar pagamentos
6. Processar entregas

## Desenvolvido para Ada Tech

Este projeto foi desenvolvido como parte do programa de formação da Ada Tech, demonstrando boas práticas de programação orientada a objetos, estruturação de projetos Java e desenvolvimento de sistemas CLI.

## Licença

Este projeto é desenvolvido para fins educacionais como parte do programa Ada Tech.