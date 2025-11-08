# 📚 Documentação Técnica - Distribuidora Ferreira

Sistema completo de gestão para distribuidora de bebidas com controle de estoque, vendas, comandas, contas de clientes e fluxo de caixa.

## 🗂️ Arquivos de Documentação

### 📊 Diagramas UML/C4

| Arquivo | Descrição | Visualizar |
|---------|-----------|------------|
| [`documentacao_c4.puml`](documentacao_c4.puml) | Diagrama C4 - Arquitetura em alto nível | [PlantUML Online](https://www.plantuml.com/plantuml/uml/) |
| [`modelo_entidades.puml`](modelo_entidades.puml) | Modelo Entidade-Relacionamento completo | [PlantUML Online](https://www.plantuml.com/plantuml/uml/) |
| [`fluxos_negocio.puml`](fluxos_negocio.puml) | Fluxos de negócio principais (Activity Diagrams) | [PlantUML Online](https://www.plantuml.com/plantuml/uml/) |
| [`diagramas_sequencia.puml`](diagramas_sequencia.puml) | Diagramas de sequência dos principais fluxos | [PlantUML Online](https://www.plantuml.com/plantuml/uml/) |

### 📝 Documentação Escrita

| Arquivo | Descrição |
|---------|-----------|
| [`ARQUITETURA_COMPLETA.md`](ARQUITETURA_COMPLETA.md) | Documentação completa da arquitetura, análise e recomendações |

### 🗄️ Scripts SQL

| Arquivo | Descrição |
|---------|-----------|
| [`importacao_completa_supabase.sql`](importacao_completa_supabase.sql) | Script completo de importação (estrutura + dados reais) |
| [`dados_produtos_reais.sql`](dados_produtos_reais.sql) | Apenas dados de produtos |
| [`dados_vendas_reais.sql`](dados_vendas_reais.sql) | Apenas dados de vendas |
| [`dados_movimentacoes_estoque_reais.sql`](dados_movimentacoes_estoque_reais.sql) | Apenas movimentações de estoque |

---

## 🎯 Visão Geral do Sistema

### Stack Tecnológico

```
┌─────────────────────────────────────┐
│   Frontend: Angular 17 + TypeScript │
└─────────────┬───────────────────────┘
              │ REST/JSON
┌─────────────▼───────────────────────┐
│   Backend: Spring Boot 3.2.3 + Java 17 │
└─────────────┬───────────────────────┘
              │ JDBC/JPA
┌─────────────▼───────────────────────┐
│   Database: PostgreSQL 14+ (Heroku) │
└─────────────────────────────────────┘
```

### Funcionalidades Principais

- ✅ **Gestão de Produtos** - Catálogo, categorias, controle de estoque
- ✅ **Sistema de Vendas** - À vista, fiado, múltiplos métodos de pagamento
- ✅ **Comandas** - Sistema de conta aberta para consumo no local
- ✅ **Controle de Caixa** - Abertura/fechamento, faturamento diário
- ✅ **Contas de Clientes** - Crédito, controle de inadimplência
- ✅ **Movimentações de Estoque** - Histórico completo (ENTRADA/SAIDA/PERDA)
- ✅ **Gestão de Compras** - Registro de compras de fornecedores

---

## 📊 Modelo de Dados Resumido

### 8 Entidades Principais

```
Categoria (1) ──── (N) Produto (1) ──── (N) MovimentacaoEstoque
                            │
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
    Compra (1) ────── (N) MovEst (N) ────── (1) Venda
                                                  │
                            ┌─────────────────────┼─────────────────────┐
                            │                     │                     │
                      Caixa (1) ──── (N)    ContaCliente (1) ──── (N)   Comanda (1) ──── (N)
```

**Legenda:**
- `(1)` - Um
- `(N)` - Muitos

### Relacionamentos-Chave

| Entidade | Relacionamento | Descrição |
|----------|---------------|-----------|
| **Produto** ↔ **Categoria** | N:1 | Cada produto pertence a uma categoria |
| **MovimentacaoEstoque** ↔ **Produto** | N:1 | Histórico de movimentações por produto |
| **Venda** ↔ **Caixa** | N:1 | Vendas vinculadas a um caixa |
| **Venda** ↔ **ContaCliente** | N:1 | Vendas fiadas vinculadas a cliente |
| **Comanda** ↔ **Venda** | 1:N | Comanda agrupa múltiplas vendas |
| **Compra** ↔ **MovimentacaoEstoque** | 1:N | Compra gera movimentações de entrada |

---

## 🔄 Fluxos de Negócio

### 1. Venda Simples (À Vista)
```
Selecionar Produtos → Calcular Total → Escolher Pagamento 
→ Gerar Movimentações SAIDA → Atualizar Estoque 
→ Atualizar Caixa → Status: PAGO
```

### 2. Venda Fiado (Conta Cliente)
```
Selecionar/Criar Cliente → Selecionar Produtos 
→ Adicionar ao Saldo Devedor → Cliente: INADIMPLENTE 
→ Gerar Movimentações → Status: PENDENTE
```

### 3. Sistema de Comandas
```
ABERTURA:
  Criar Comanda → Vincular Cliente → Status: ABERTA

DURANTE:
  Adicionar Itens → Criar Vendas Parciais 
  → Atualizar Saldo Devedor → Gerar Movimentações

FECHAMENTO:
  Escolher Pagamento → Atualizar Vendas 
  → Zerar/Atualizar Saldo → Comanda: FECHADA
```

### 4. Controle de Caixa
```
ABERTURA:
  Definir Valor Inicial → Status: ABERTO 
  → Apenas 1 caixa aberto por vez

DURANTE O DIA:
  Vendas Atuam no Faturamento

FECHAMENTO:
  Calcular Faturamento Total 
  → Valor Final = Inicial + Faturamento 
  → Status: FECHADO
```

---

## 🛠️ Como Visualizar os Diagramas

### Opção 1: PlantUML Online (Mais Fácil)

1. Acesse: https://www.plantuml.com/plantuml/uml/
2. Copie o conteúdo de qualquer arquivo `.puml`
3. Cole na caixa de texto
4. Clique em "Submit" ou pressione `Ctrl+Enter`
5. O diagrama será renderizado automaticamente

### Opção 2: VS Code (Recomendado para Desenvolvimento)

```bash
# Instalar extensão PlantUML
code --install-extension jebbs.plantuml

# Pré-requisitos (Ubuntu/Debian)
sudo apt install graphviz default-jre

# Visualizar
# Abra o arquivo .puml e pressione Alt+D
```

### Opção 3: IntelliJ IDEA

1. Instale o plugin: **PlantUML Integration**
2. Abra qualquer arquivo `.puml`
3. Visualização inline automática

### Opção 4: Docker (Sem instalação local)

```bash
# Executar servidor PlantUML local
docker run -d -p 8080:8080 plantuml/plantuml-server:latest

# Acessar
# http://localhost:8080
```

---

## 📈 Estrutura do Backend

```
backend/
├── controllers/         # REST API endpoints
│   ├── ProdutoController.java
│   ├── VendaController.java
│   ├── ComandaController.java
│   ├── CaixaController.java
│   ├── ClienteController.java
│   └── CompraController.java
│
├── services/           # Lógica de negócio
│   ├── implementations/
│   │   ├── ProdutoServiceImpl.java
│   │   ├── VendaServiceImpl.java
│   │   ├── ComandaServiceImpl.java
│   │   └── ...
│   └── interfaces/
│       └── ...
│
├── repositories/       # Acesso a dados (JPA)
│   ├── ProdutoRepository.java
│   ├── VendaRepository.java
│   └── ...
│
├── models/            # Entidades JPA
│   ├── Produto.java
│   ├── Venda.java
│   ├── Comanda.java
│   ├── ContaCliente.java
│   └── ...
│
├── dtos/              # Data Transfer Objects
│   ├── requests/
│   └── responses/
│
├── mappers/           # MapStruct conversores
│   ├── ProdutoMapper.java
│   └── ...
│
└── enums/             # Enumerações
    ├── StatusVenda.java
    ├── StatusCaixa.java
    ├── TipoMovimentacao.java
    └── ...
```

---

## 🎯 Decisões de Arquitetura

### 1. Por que JPA + Hibernate?
- ✅ Mapeamento objeto-relacional automático
- ✅ Queries type-safe com JPQL
- ✅ Controle de transações
- ✅ Lazy/Eager loading configurável

### 2. Por que MapStruct?
- ✅ Conversão DTO ↔ Entity em tempo de compilação
- ✅ Performance superior a reflection
- ✅ Type-safe
- ✅ Menos código boilerplate

### 3. Por que PostgreSQL?
- ✅ ACID completo
- ✅ Suporte a JSON/JSONB (futuro)
- ✅ Excelente performance
- ✅ Open source e maduro

### 4. Por que Heroku?
- ✅ Deploy simples (Git Push)
- ✅ PostgreSQL nativo
- ✅ Escalabilidade automática
- ✅ Monitoramento integrado

---

## 🔐 Segurança e Boas Práticas

### Implementadas ✅
- Validação de entrada nos Controllers
- Transações gerenciadas pelo Spring
- Relacionamentos JPA bem definidos
- Separação de responsabilidades (MVC)

### A Implementar 🔄
- [ ] Autenticação JWT
- [ ] Autorização por roles (ADMIN, CAIXA, GERENTE)
- [ ] Soft delete para auditoria
- [ ] Controle de concorrência (@Version)
- [ ] Rate limiting
- [ ] Logs estruturados
- [ ] Monitoramento APM

---

## 📊 Métricas do Projeto

### Banco de Dados
- **Tabelas:** 8 principais
- **Relacionamentos:** 12 foreign keys
- **Enums:** 5 tipos
- **Índices:** 3 + primary keys

### Backend (Java)
- **Controllers:** 7
- **Services:** 7
- **Repositories:** 8
- **Entities:** 8
- **DTOs:** ~30
- **Mappers:** 7

### Dados Importados (Backup Real)
- **Categorias:** 22
- **Produtos:** 263
- **Vendas:** 21
- **Compras:** 8
- **Movimentações:** 16
- **Caixas:** 11
- **Comandas:** 10
- **Clientes:** 3

---

## 🚀 Deploy e Infraestrutura

### Ambiente de Produção

```yaml
Aplicação: Heroku Dyno
  - Tipo: Web (Spring Boot)
  - Port: $PORT (dinâmico)
  - Buildpack: heroku/java
  - Java: 17

Banco de Dados: Heroku Postgres
  - Plano: Essential 0
  - Limite: 10.000 rows
  - Storage: 1GB
  - Connections: 20

Armazenamento: Uploadcare
  - Imagens de produtos
  - CDN global
```

### Variáveis de Ambiente

```properties
# Backend (Heroku)
DATABASE_URL=postgres://...
DB_USERNAME=...
DB_PASSWORD=...
PORT=8080

# Frontend
API_URL=https://distribuidorabebidas-323b6f9478c3.herokuapp.com
```

---

## 📚 Referências Técnicas

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [MapStruct Reference](https://mapstruct.org/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [C4 Model](https://c4model.com/)
- [PlantUML Documentation](https://plantuml.com/)
- [Heroku Dev Center](https://devcenter.heroku.com/)

---

## 🤝 Contribuição

Para contribuir com melhorias na documentação:

1. Edite os arquivos `.puml` para diagramas
2. Atualize `ARQUITETURA_COMPLETA.md` para documentação textual
3. Mantenha os diagramas sincronizados com o código
4. Documente decisões de arquitetura importantes

---

## 📝 Changelog da Documentação

### v1.0 - Novembro 2025
- ✅ Criação inicial dos diagramas C4
- ✅ Modelo de entidades completo
- ✅ Fluxos de negócio documentados
- ✅ Diagramas de sequência
- ✅ Documentação escrita completa
- ✅ README principal

---

**Mantido por:** Equipe Distribuidora Ferreira  
**Última atualização:** Novembro 2025  
**Versão:** 1.0
