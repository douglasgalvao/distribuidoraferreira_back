# 📊 Documentação de Arquitetura - Distribuidora Ferreira

## 🎯 Visão Geral do Sistema

Sistema completo de gestão para distribuidora de bebidas com controle de estoque, vendas, comandas, contas de clientes e fluxo de caixa.

**Stack Tecnológico:**
- **Backend:** Spring Boot 3.2.3 + Java 17
- **Frontend:** Angular 17 + TypeScript
- **Banco de Dados:** PostgreSQL 14+
- **Hospedagem:** Heroku (Backend + Database)
- **Armazenamento:** Uploadcare (Imagens)

---

## 📐 Arquitetura do Sistema

### Camadas da Aplicação (Backend)

```
┌─────────────────────────────────────────────┐
│           Controllers (REST API)            │
│  - Produto, Venda, Compra, Comanda, etc    │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│          Services (Business Logic)          │
│  - Validações, Cálculos, Regras de Negócio │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│       Repositories (Data Access JPA)        │
│  - CRUD Operations, Queries Customizadas   │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│         PostgreSQL Database (Heroku)        │
│  - 8 Tabelas Principais + Relacionamentos  │
└─────────────────────────────────────────────┘
```

---

## 🗄️ Modelo de Dados

### Entidades Principais

#### 1. **Produto** 
- **Função:** Catálogo de produtos da distribuidora
- **Campos-Chave:**
  - `cod_barras` (UNIQUE) - Código de barras único
  - `preco` - Preço de venda normal
  - `preco_consumo` - Preço para consumo em comanda
  - `estoque` - Quantidade disponível
  - `estoque_minimo` - Alerta de baixo estoque
- **Relacionamentos:**
  - N:1 com `Categoria`
  - 1:N com `MovimentacaoEstoque`

#### 2. **MovimentacaoEstoque**
- **Função:** Histórico completo de todas as movimentações
- **Tipos:**
  - `ENTRADA` - Compras de fornecedores
  - `SAIDA` - Vendas para clientes
  - `PERDA` - Produtos danificados/vencidos
- **Cálculo Automático:** `valor_total = preco_unitario × quantidade`
- **Relacionamentos:**
  - N:1 com `Produto`
  - N:1 com `Compra` (opcional)
  - N:1 com `Venda` (opcional)

#### 3. **Venda**
- **Função:** Transações de saída de produtos
- **Status Possíveis:**
  - `PAGO` - Venda quitada
  - `PENDENTE` - Aguardando pagamento (fiado)
  - `CANCELADO` - Venda cancelada
- **Métodos de Pagamento:** PIX, Dinheiro, Débito, Crédito, Misto
- **Relacionamentos:**
  - 1:N com `MovimentacaoEstoque`
  - N:1 com `Caixa`
  - N:1 com `ContaCliente` (opcional)
  - N:1 com `Comanda` (opcional)

#### 4. **Compra**
- **Função:** Registro de compras de fornecedores
- **Impacto:** Gera movimentações de ENTRADA no estoque
- **Relacionamentos:**
  - 1:N com `MovimentacaoEstoque`

#### 5. **Caixa**
- **Função:** Controle financeiro diário
- **Regra:** Apenas 1 caixa ABERTO por vez
- **Cálculo:** `valor_total = valor_inicial + faturamento_dia`
- **Status:** ABERTO / FECHADO
- **Relacionamentos:**
  - 1:N com `Venda`

#### 6. **ContaCliente**
- **Função:** Clientes com crédito (vendas fiadas)
- **Controles:**
  - `saldo_devedor` - Total a pagar
  - `total_pago` - Histórico de pagamentos
  - Detalhamento por método (PIX, Débito, etc)
- **Status:** ATIVO / INADIMPLENTE / BLOQUEADO
- **Relacionamentos:**
  - 1:N com `Venda`
  - 1:N com `Comanda`

#### 7. **Comanda**
- **Função:** Conta aberta no estabelecimento
- **Comportamento:** Agrupa múltiplas vendas de um cliente
- **Status:** ABERTA / FECHADA / CANCELADA
- **Relacionamentos:**
  - 1:N com `Venda`
  - N:1 com `ContaCliente`

#### 8. **Categoria**
- **Função:** Classificação de produtos
- **Exemplos:** Bebidas, Água, Cerveja, etc
- **Relacionamentos:**
  - 1:N com `Produto`

---

## 🔄 Fluxos de Negócio Principais

### 1️⃣ Abertura de Caixa
```
1. Usuário abre caixa com valor inicial
2. Sistema valida: apenas 1 caixa aberto
3. Status: ABERTO
4. Vendas começam a ser vinculadas ao caixa
```

### 2️⃣ Venda Simples (À Vista)
```
1. Selecionar produtos
2. Sistema calcula total
3. Escolher método de pagamento
4. Gerar movimentações SAIDA
5. Atualizar estoque (diminuir)
6. Atualizar faturamento do caixa
7. Status: PAGO
```

### 3️⃣ Venda Fiado (Conta Cliente)
```
1. Selecionar/Criar cliente
2. Selecionar produtos
3. Calcular total
4. Criar venda com Status: PENDENTE
5. Adicionar ao saldo_devedor do cliente
6. Cliente Status: INADIMPLENTE
7. Gerar movimentações SAIDA
```

### 4️⃣ Sistema de Comandas
```
ABERTURA:
1. Criar comanda vinculada a cliente
2. Status: ABERTA

DURANTE:
1. Adicionar itens (cria vendas parciais)
2. Cada item atualiza saldo_devedor
3. Movimentações SAIDA geradas

FECHAMENTO:
1. Escolher método pagamento
2. Se pagamento total:
   - Zerar saldo_devedor
   - Vendas Status: PAGO
   - Cliente Status: ATIVO
3. Se pagamento parcial:
   - Atualizar saldo_devedor
   - Cliente Status: INADIMPLENTE
4. Comanda Status: FECHADA
```

### 5️⃣ Pagamento de Conta
```
1. Selecionar cliente
2. Exibir vendas pendentes
3. Informar valor do pagamento
4. Escolher método
5. Distribuir pagamento nas vendas mais antigas
6. Atualizar saldo_devedor
7. Se quitado total: Cliente Status: ATIVO
8. Registrar no caixa aberto
```

### 6️⃣ Registro de Compra
```
1. Informar produtos e quantidades
2. Informar preços unitários
3. Escolher método pagamento
4. Criar movimentações ENTRADA
5. Aumentar estoque automaticamente
6. Calcular total da compra
```

### 7️⃣ Fechamento de Caixa
```
1. Validar: não há comandas abertas
2. Calcular faturamento total do dia
3. valor_total = valor_inicial + faturamento_dia
4. Status: FECHADO
5. Exibir resumo por método de pagamento
```

---

## 🔍 Análise da Arquitetura

### ✅ Pontos Fortes

1. **Separação de Responsabilidades**
   - Controllers, Services, Repositories bem definidos
   - Mappers (MapStruct) para conversão DTO ↔ Entity

2. **Auditoria Completa**
   - Todas as movimentações registradas
   - Histórico preservado mesmo após exclusões

3. **Flexibilidade de Pagamento**
   - Suporte a múltiplos métodos
   - Pagamentos parciais
   - Rastreamento por método (PIX, Débito, etc)

4. **Controle de Estoque Robusto**
   - Movimentações tipadas (ENTRADA/SAIDA/PERDA)
   - Alertas de estoque mínimo
   - Cálculos automáticos de valor

5. **Sistema de Comandas Completo**
   - Vendas incrementais
   - Controle de crédito
   - Fechamento flexível

### ⚠️ Pontos de Atenção

1. **Cascading Operations**
   - `CascadeType.ALL` em vários relacionamentos
   - **Risco:** Exclusão acidental em cadeia
   - **Recomendação:** Revisar para `CascadeType.PERSIST` e `CascadeType.MERGE`

2. **Lazy Loading**
   - Muitos `FetchType.LAZY` podem causar N+1 queries
   - **Recomendação:** Usar `@EntityGraph` ou JOIN FETCH em queries específicas

3. **Validações de Negócio**
   - Algumas validações podem estar duplicadas (Controller + Service)
   - **Recomendação:** Centralizar em Services ou criar Validators dedicados

4. **Controle de Concorrência**
   - Não há `@Version` para controle de versionamento otimista
   - **Risco:** Conflitos em operações simultâneas
   - **Recomendação:** Adicionar `@Version` em entidades críticas (Caixa, Produto)

5. **Soft Delete**
   - Exclusões são permanentes
   - **Recomendação:** Implementar soft delete para auditoria completa

---

## 🚀 Recomendações de Melhorias

### 1. **Segurança**
```java
// Adicionar Spring Security
- Autenticação JWT
- Roles: ADMIN, CAIXA, GERENTE
- Proteção de endpoints sensíveis
```

### 2. **Controle de Concorrência**
```java
@Entity
public class Produto {
    @Version
    private Long version; // Controle de versão otimista
}
```

### 3. **Soft Delete**
```java
@Entity
@SQLDelete(sql = "UPDATE produtos SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Produto {
    private Boolean deleted = false;
}
```

### 4. **Auditoria Automática**
```java
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    private Date createdAt;
    
    @LastModifiedDate
    private Date updatedAt;
    
    @CreatedBy
    private String createdBy;
}
```

### 5. **Validações Bean Validation**
```java
public class ProdutoRequest {
    @NotBlank(message = "Nome obrigatório")
    private String nome;
    
    @Positive(message = "Preço deve ser positivo")
    private Double preco;
    
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoque;
}
```

### 6. **DTOs de Projeção**
```java
// Para consultas que não precisam de todos os dados
public interface ProdutoResumo {
    Long getId();
    String getNome();
    Integer getEstoque();
}
```

### 7. **Índices de Performance**
```sql
-- Adicionar no schema
CREATE INDEX idx_produto_cod_barras ON produtos(cod_barras);
CREATE INDEX idx_venda_data_hora ON vendas(data_hora DESC);
CREATE INDEX idx_movimentacao_data ON movimentacoes_estoque(horario_registro DESC);
```

### 8. **Cache Strategy**
```java
@Cacheable("categorias")
public List<Categoria> findAll() {
    return categoriaRepository.findAll();
}
```

### 9. **Eventos de Domínio**
```java
// Desacoplar lógica de notificações
@DomainEvents
Collection<Object> domainEvents() {
    return List.of(new ProdutoEstoqueBaixoEvent(this));
}
```

### 10. **Documentação API**
```java
// OpenAPI/Swagger
@Operation(summary = "Criar venda", 
           description = "Registra uma nova venda e atualiza estoque")
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Venda criada"),
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
})
```

---

## 📊 Métricas e Monitoramento

### KPIs Recomendados

1. **Operacionais:**
   - Vendas por dia/mês
   - Ticket médio
   - Produtos mais vendidos
   - Taxa de vendas fiadas vs à vista

2. **Estoque:**
   - Produtos com estoque crítico
   - Giro de estoque por categoria
   - Taxa de perdas

3. **Financeiro:**
   - Faturamento por método de pagamento
   - Contas a receber (saldo devedor total)
   - Taxa de inadimplência

4. **Performance:**
   - Tempo médio de resposta API
   - Queries lentas (> 1s)
   - Taxa de erro

---

## 🔐 Segurança

### Checklist de Segurança

- [ ] Implementar autenticação (JWT)
- [ ] Adicionar autorização por roles
- [ ] Validar inputs (Bean Validation)
- [ ] Proteção contra SQL Injection (JPA já protege)
- [ ] CORS configurado corretamente
- [ ] HTTPS obrigatório em produção
- [ ] Senhas hash (se houver login)
- [ ] Rate limiting em endpoints críticos
- [ ] Logs de auditoria
- [ ] Backup automático do banco

---

## 📝 Diagrama de Visualização

Para visualizar os diagramas criados:

1. **Online (Recomendado):**
   - Acesse: https://www.plantuml.com/plantuml/uml/
   - Cole o conteúdo dos arquivos `.puml`

2. **VS Code:**
   ```bash
   # Instalar extensão
   code --install-extension jebbs.plantuml
   ```

3. **IntelliJ IDEA:**
   - Plugin: PlantUML Integration
   - Visualização inline

---

## 🎯 Próximos Passos

### Curto Prazo (1-2 semanas)
1. ✅ Deploy no Heroku (FEITO)
2. ✅ Importação de dados reais (FEITO)
3. 🔄 Remover lógica `keepRenderOn` (desnecessária no Heroku)
4. 🔄 Implementar validações robustas
5. 🔄 Adicionar testes unitários críticos

### Médio Prazo (1-2 meses)
1. Implementar autenticação/autorização
2. Adicionar relatórios gerenciais
3. Criar dashboard com métricas
4. Otimizar queries (índices + cache)
5. Implementar soft delete

### Longo Prazo (3-6 meses)
1. App mobile (React Native)
2. Integração com impressora fiscal
3. Sistema de fidelidade
4. Promoções e descontos
5. Analytics avançados

---

## 📚 Referências

- **Documentação Spring Boot:** https://spring.io/projects/spring-boot
- **Angular Docs:** https://angular.io/docs
- **C4 Model:** https://c4model.com/
- **PlantUML:** https://plantuml.com/
- **Heroku Postgres:** https://devcenter.heroku.com/categories/heroku-postgres

---

**Data:** Novembro 2025  
**Versão:** 1.0  
**Autor:** Documentação Técnica - Distribuidora Ferreira
