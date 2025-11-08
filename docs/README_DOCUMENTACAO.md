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
