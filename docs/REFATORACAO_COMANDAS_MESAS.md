# 🏗️ Nova Arquitetura - Sistema de Comandas, Mesas e Clientes

## 📋 Visão Geral das Mudanças

### Problemas da Arquitetura Atual
❌ **Comanda acoplada a Cliente** - Não permite flexibilidade  
❌ **Não há conceito de Mesa** - Dificulta gestão física  
❌ **Divisão de conta complexa** - Não suporta múltiplos clientes  
❌ **ContaCliente misturada** - Crédito e comanda no mesmo conceito  

### Solução Proposta
✅ **Cliente independente** - Entidade base, reutilizável  
✅ **Mesa física separada** - Gestão de layout do estabelecimento  
✅ **Comanda flexível** - Com ou sem mesa, múltiplos clientes  
✅ **ContaCliente desacoplada** - Crédito independente de comandas  
✅ **Divisão de conta nativa** - Múltiplos clientes por comanda  

---

## 🎯 Novos Conceitos

### 1. **Cliente** (Novo modelo expandido)

```java
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String telefone;
    
    @Column(unique = true)
    private String cpf; // Opcional, para identificação única
    
    private String email;
    
    @Column(name = "data_cadastro")
    private Date dataCadastro;
    
    private Boolean ativo = true;
    
    private String observacoes;
    
    // Relacionamentos
    @OneToMany(mappedBy = "cliente")
    private List<ComandaCliente> comandas;
    
    @OneToMany(mappedBy = "cliente")
    private List<ContaCliente> contas;
}
```

**Características:**
- Cadastro único e persistente
- Pode participar de múltiplas comandas
- Histórico completo de consumo
- CPF opcional para clientes frequentes

---

### 2. **Mesa** (Nova entidade)

```java
@Entity
@Table(name = "mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Integer numero;
    
    private Integer capacidade;
    
    private String localizacao; // "Salão Principal", "Varanda", etc
    
    @Enumerated(EnumType.STRING)
    private StatusMesa status;
    
    private Boolean ativo = true;
    
    // Relacionamento 1:1 opcional com Comanda
    @OneToOne(mappedBy = "mesa")
    private Comanda comandaAtual;
}

public enum StatusMesa {
    LIVRE,      // Disponível para uso
    OCUPADA,    // Com comanda aberta
    RESERVADA,  // Reservada para chegada
    MANUTENCAO  // Fora de uso
}
```

**Características:**
- Representa mesa física
- Número único de identificação
- Status independente de comanda
- Permite gestão de layout

---

### 3. **Comanda** (Refatorada)

```java
@Entity
@Table(name = "comandas")
public class Comanda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo; // Ex: "CMD-001", "M15", "BAL-042"
    
    @Column(name = "data_abertura")
    private Date dataAbertura;
    
    @Column(name = "data_fechamento")
    private Date dataFechamento;
    
    @Enumerated(EnumType.STRING)
    private StatusComanda status;
    
    @Enumerated(EnumType.STRING)
    private TipoComanda tipo;
    
    private String observacoes;
    
    // Mesa é OPCIONAL
    @OneToOne
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;
    
    @ManyToOne
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;
    
    // Múltiplos clientes via tabela intermediária
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    private List<ComandaCliente> clientes;
    
    @OneToMany(mappedBy = "comanda", cascade = CascadeType.ALL)
    private List<Venda> vendas;
}

public enum StatusComanda {
    ABERTA,
    FECHADA,
    CANCELADA
}

public enum TipoComanda {
    BALCAO,    // Consumo no balcão (sem mesa)
    MESA,      // Mesa física
    DELIVERY   // Entrega (futuro)
}
```

**Mudanças principais:**
- ❌ **Removido:** `conta_cliente_id` (relação direta)
- ✅ **Adicionado:** `tipo` (BALCAO, MESA, DELIVERY)
- ✅ **Adicionado:** `codigo` único de identificação
- ✅ **Adicionado:** Relação opcional com Mesa
- ✅ **Adicionado:** Múltiplos clientes via `ComandaCliente`

---

### 4. **ComandaCliente** (Nova entidade - Relacionamento N:N)

```java
@Entity
@Table(name = "comanda_cliente")
public class ComandaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "comanda_id", nullable = false)
    private Comanda comanda;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "percentual_divisao")
    private Double percentualDivisao; // Para divisão de conta (0-100)
    
    private Boolean principal = false; // Quem abriu a comanda
    
    @Column(name = "data_vinculo")
    private Date dataVinculo;
    
    // Vendas específicas deste cliente nesta comanda
    @OneToMany(mappedBy = "comandaCliente")
    private List<Venda> vendas;
}
```

**Características:**
- Permite múltiplos clientes por comanda
- Suporta divisão de conta (%)
- Identifica cliente principal
- Rastreia vendas por cliente

---

### 5. **ContaCliente** (Refatorada - Crédito)

```java
@Entity
@Table(name = "contas_cliente")
public class ContaCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "data_abertura")
    private Date dataAbertura;
    
    @Column(name = "data_fechamento")
    private Date dataFechamento;
    
    @Column(name = "saldo_devedor")
    private Double saldoDevedor = 0.0;
    
    @Column(name = "total_consumido")
    private Double totalConsumido = 0.0;
    
    @Column(name = "limite_credito")
    private Double limiteCredito = 0.0; // Limite aprovado
    
    @Enumerated(EnumType.STRING)
    private StatusConta status;
    
    @OneToMany(mappedBy = "contaCliente")
    private List<Pagamento> pagamentos;
}

public enum StatusConta {
    ATIVA,        // Pode consumir
    SUSPENSA,     // Limite atingido
    BLOQUEADA,    // Bloqueada manualmente
    QUITADA       // Sem débitos
}
```

**Mudanças principais:**
- ❌ **Removido:** Vinculação direta com vendas
- ✅ **Adicionado:** `limite_credito` (controle de risco)
- ✅ **Adicionado:** `total_consumido` (histórico)
- ✅ **Adicionado:** Vinculação com Cliente (não mais nome direto)
- ✅ **Conceito:** Conta de crédito independente de comandas

---

### 6. **Venda** (Refatorada)

```java
@Entity
@Table(name = "vendas")
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data_hora")
    private Date dataHora;
    
    @Column(name = "metodo_pagamento")
    private String metodoPagamento;
    
    @Column(name = "total_venda")
    private Double totalVenda;
    
    @Column(name = "total_pago")
    private Double totalPago;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status_venda")
    private StatusVenda status;
    
    // Vinculada à comanda
    @ManyToOne
    @JoinColumn(name = "comanda_id")
    private Comanda comanda;
    
    // Atribuída a cliente específico (dentro da comanda)
    @ManyToOne
    @JoinColumn(name = "comanda_cliente_id")
    private ComandaCliente comandaCliente;
    
    @ManyToOne
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;
    
    @OneToMany(mappedBy = "venda")
    private List<MovimentacaoEstoque> movimentacoes;
    
    @OneToMany(mappedBy = "venda")
    private List<Pagamento> pagamentos;
}
```

**Mudanças principais:**
- ❌ **Removido:** Vinculação direta com `ContaCliente`
- ✅ **Adicionado:** Vinculação com `ComandaCliente` (permite rastrear cliente dentro da comanda)
- ✅ **Conceito:** Vendas sempre vinculadas a comandas

---

### 7. **Pagamento** (Nova entidade)

```java
@Entity
@Table(name = "pagamentos")
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "conta_cliente_id")
    private ContaCliente contaCliente;
    
    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;
    
    @Column(name = "data_pagamento")
    private Date dataPagamento;
    
    private Double valor;
    
    @Column(name = "metodo_pagamento")
    private String metodoPagamento;
    
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "caixa_id")
    private Caixa caixa;
}
```

**Características:**
- Registra todos os pagamentos
- Pode quitar venda específica
- Pode abater saldo de conta
- Auditoria completa

---

## 🔄 Fluxos de Negócio Refatorados

### Fluxo 1: Abrir Comanda no Balcão (sem mesa)

```
1. Garçom abre comanda tipo BALCAO
   - Gera código único (BAL-001)
   - Não vincula mesa
   - Status: ABERTA

2. (Opcional) Vincula cliente
   - Busca/cria cliente
   - Cria registro ComandaCliente (principal=true)
   - Se cliente tem crédito, vincula ContaCliente

3. Adiciona itens
   - Cria vendas vinculadas à comanda
   - Se houver cliente, vincula a ComandaCliente
   - Gera movimentações de estoque

4. Fechamento
   - Escolhe método pagamento
   - Se à vista: marca PAGO
   - Se fiado: adiciona a ContaCliente
   - Comanda Status: FECHADA
```

---

### Fluxo 2: Abrir Comanda em Mesa

```
1. Verifica disponibilidade da mesa
   - Mesa.status deve ser LIVRE

2. Garçom abre comanda tipo MESA
   - Gera código baseado na mesa (M15)
   - Vincula à mesa
   - Mesa.status = OCUPADA
   - Status: ABERTA

3. (Opcional) Vincula cliente(s)
   - Pode adicionar múltiplos clientes
   - Define cliente principal
   - Calcula percentual de divisão

4. Adiciona itens
   - Ao adicionar item, pergunta: "Para qual cliente?"
   - Cria venda vinculada a ComandaCliente específico
   - Permite rastreio individual

5. Fechamento
   - Pode fechar individual (cliente sai)
   - Pode fechar total (mesa libera)
   - Mesa.status = LIVRE
   - Comanda Status: FECHADA
```

---

### Fluxo 3: Divisão de Conta

```
1. Comanda aberta com múltiplos clientes
   - Cliente A (principal)
   - Cliente B
   - Cliente C

2. Opção 1: Divisão por itens
   - Cada venda já tem comandaCliente
   - Soma por cliente
   - Cliente A: R$ 50
   - Cliente B: R$ 30
   - Cliente C: R$ 20

3. Opção 2: Divisão igual
   - Total: R$ 100
   - 3 clientes
   - Cada um: R$ 33,33

4. Opção 3: Divisão por percentual
   - Cliente A: 50% (R$ 50)
   - Cliente B: 30% (R$ 30)
   - Cliente C: 20% (R$ 20)

5. Fechamento individual
   - Cliente B paga sua parte (R$ 30)
   - Cria Pagamento vinculado às vendas dele
   - Comanda continua ABERTA para A e C
```

---

### Fluxo 4: Cliente com Crédito

```
1. Cliente frequente cadastrado
   - Tem ContaCliente ATIVA
   - Limite: R$ 500
   - Saldo devedor: R$ 100

2. Abre nova comanda
   - Vincula Cliente via ComandaCliente
   - Sistema verifica limite disponível: R$ 400

3. Consome
   - Adiciona itens (R$ 80)
   - Vendas Status: PENDENTE
   - ContaCliente.saldoDevedor = R$ 180
   - Ainda tem R$ 320 disponível

4. Fecha comanda "fiado"
   - Vendas continuam PENDENTE
   - Comanda Status: FECHADA
   - Cliente sai sem pagar

5. Pagamento posterior
   - Cliente retorna para pagar
   - Acessa ContaCliente dele
   - Paga R$ 100
   - Sistema distribui nas vendas mais antigas
   - Saldo devedor: R$ 80
```

---

### Fluxo 5: Gestão de Mesas

```
1. Ver status de todas as mesas
   GET /mesas
   - Mesa 1: LIVRE
   - Mesa 2: OCUPADA (Comanda M02)
   - Mesa 3: RESERVADA
   - Mesa 4: MANUTENCAO

2. Reservar mesa
   PUT /mesas/5/reservar
   - Mesa.status = RESERVADA
   - Não cria comanda ainda

3. Cliente chega (mesa reservada)
   - Abre comanda na mesa
   - Mesa.status = OCUPADA
   - Comanda vinculada

4. Transferir mesa
   - Cliente pede para trocar de mesa
   - Atualiza comanda.mesa_id
   - Mesa antiga = LIVRE
   - Mesa nova = OCUPADA

5. Fechar mesa
   - Fecha comanda
   - Mesa.status = LIVRE
   - Disponível para próximo cliente
```

---

## 📊 Comparação: Antes vs Depois

### Tabela de Entidades

| Conceito | Arquitetura Antiga | Arquitetura Nova |
|----------|-------------------|------------------|
| **Cliente** | Dentro de ContaCliente | Entidade independente |
| **Mesa** | ❌ Não existia | ✅ Entidade própria |
| **Comanda** | Vinculada a 1 cliente | Vinculada a N clientes |
| **Divisão de Conta** | ❌ Não suportado | ✅ Nativo (ComandaCliente) |
| **Crédito** | Misturado com comanda | Entidade separada (ContaCliente) |
| **Pagamento** | Dentro de Venda | ✅ Entidade própria |
| **Tipos de Comanda** | ❌ Não diferenciado | ✅ BALCAO/MESA/DELIVERY |

---

## 🗄️ Script de Migração SQL

```sql
-- 1. Criar nova tabela Cliente
CREATE TABLE clientes (
    cliente_id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,
    observacoes TEXT
);

-- 2. Criar tabela Mesa
CREATE TABLE mesas (
    mesa_id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    capacidade INTEGER,
    localizacao VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'LIVRE',
    ativo BOOLEAN DEFAULT TRUE,
    CHECK (status IN ('LIVRE', 'OCUPADA', 'RESERVADA', 'MANUTENCAO'))
);

-- 3. Alterar tabela Comanda
ALTER TABLE comandas 
    ADD COLUMN codigo VARCHAR(50) UNIQUE,
    ADD COLUMN tipo VARCHAR(20) DEFAULT 'BALCAO',
    ADD COLUMN mesa_id BIGINT REFERENCES mesas(mesa_id),
    DROP COLUMN conta_cliente_id; -- Remove vinculação direta

UPDATE comandas SET tipo = 'BALCAO' WHERE tipo IS NULL;
UPDATE comandas SET codigo = CONCAT('CMD-', LPAD(comanda_id::TEXT, 6, '0')) WHERE codigo IS NULL;

ALTER TABLE comandas ALTER COLUMN tipo SET NOT NULL;
ALTER TABLE comandas ALTER COLUMN codigo SET NOT NULL;

-- 4. Criar tabela ComandaCliente (N:N)
CREATE TABLE comanda_cliente (
    comanda_cliente_id BIGSERIAL PRIMARY KEY,
    comanda_id BIGINT NOT NULL REFERENCES comandas(comanda_id),
    cliente_id BIGINT NOT NULL REFERENCES clientes(cliente_id),
    percentual_divisao DECIMAL(5,2) DEFAULT 100.00,
    principal BOOLEAN DEFAULT FALSE,
    data_vinculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(comanda_id, cliente_id)
);

-- 5. Refatorar ContaCliente
ALTER TABLE contas_cliente
    ADD COLUMN cliente_id BIGINT REFERENCES clientes(cliente_id),
    ADD COLUMN total_consumido DECIMAL(10,2) DEFAULT 0,
    ADD COLUMN limite_credito DECIMAL(10,2) DEFAULT 0;

-- Migrar dados: criar clientes a partir de contas_cliente existentes
INSERT INTO clientes (nome, telefone, data_cadastro)
SELECT DISTINCT nome_cliente, telefone, NOW()
FROM contas_cliente;

-- Vincular ContaCliente aos novos Clientes
UPDATE contas_cliente cc
SET cliente_id = c.cliente_id
FROM clientes c
WHERE cc.nome_cliente = c.nome AND cc.telefone = c.telefone;

-- Pode remover nome_cliente e telefone de contas_cliente depois
-- ALTER TABLE contas_cliente DROP COLUMN nome_cliente, DROP COLUMN telefone;

-- 6. Criar tabela Pagamento
CREATE TABLE pagamentos (
    pagamento_id BIGSERIAL PRIMARY KEY,
    conta_cliente_id BIGINT REFERENCES contas_cliente(conta_cliente_id),
    venda_id BIGINT REFERENCES vendas(venda_id),
    data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor DECIMAL(10,2) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,
    observacoes TEXT,
    caixa_id BIGINT REFERENCES caixas(caixa_id)
);

-- 7. Atualizar Vendas
ALTER TABLE vendas 
    ADD COLUMN comanda_cliente_id BIGINT REFERENCES comanda_cliente(comanda_cliente_id),
    DROP COLUMN conta_cliente_id; -- Remove vinculação direta antiga

-- 8. Criar índices para performance
CREATE INDEX idx_comanda_cliente_comanda ON comanda_cliente(comanda_id);
CREATE INDEX idx_comanda_cliente_cliente ON comanda_cliente(cliente_id);
CREATE INDEX idx_vendas_comanda_cliente ON vendas(comanda_cliente_id);
CREATE INDEX idx_pagamentos_conta ON pagamentos(conta_cliente_id);
CREATE INDEX idx_pagamentos_venda ON pagamentos(venda_id);
CREATE INDEX idx_mesas_status ON mesas(status);
CREATE INDEX idx_comandas_mesa ON comandas(mesa_id);
CREATE INDEX idx_comandas_tipo ON comandas(tipo);
CREATE INDEX idx_clientes_cpf ON clientes(cpf);
CREATE INDEX idx_clientes_telefone ON clientes(telefone);

-- 9. Popular mesas (exemplo)
INSERT INTO mesas (numero, capacidade, localizacao, status) VALUES
(1, 4, 'Salão Principal', 'LIVRE'),
(2, 4, 'Salão Principal', 'LIVRE'),
(3, 6, 'Salão Principal', 'LIVRE'),
(4, 2, 'Varanda', 'LIVRE'),
(5, 8, 'Sala VIP', 'LIVRE');
```

---

## 🎯 Benefícios da Nova Arquitetura

### 1. **Flexibilidade**
✅ Comanda pode ser balcão, mesa ou delivery  
✅ Mesa opcional (não obrigatória)  
✅ Múltiplos clientes por comanda  
✅ Cliente pode participar de várias comandas  

### 2. **Divisão de Conta**
✅ Rastreio individual de consumo  
✅ Divisão por itens, igual ou percentual  
✅ Fechamento parcial (cliente sai antes)  
✅ Histórico detalhado  

### 3. **Gestão de Mesas**
✅ Status em tempo real  
✅ Reservas  
✅ Transferência de mesa  
✅ Manutenção (mesa quebrada)  

### 4. **Crédito Desacoplado**
✅ ContaCliente independente  
✅ Limite de crédito  
✅ Histórico completo  
✅ Pagamentos rastreados  

### 5. **Auditoria**
✅ Quem consumiu o quê  
✅ Quando e quanto pagou  
✅ Histórico de comandas  
✅ Relatórios detalhados  

---

## 📋 Próximos Passos

### 1. **Backend (Spring Boot)**
- [ ] Criar entidades Java (Cliente, Mesa, ComandaCliente, Pagamento)
- [ ] Refatorar Comanda (adicionar tipo, código, mesa)
- [ ] Refatorar ContaCliente (adicionar limite, cliente_id)
- [ ] Criar repositories
- [ ] Criar services com nova lógica
- [ ] Atualizar controllers
- [ ] Criar endpoints de Mesa
- [ ] Criar endpoints de divisão de conta

### 2. **Banco de Dados**
- [ ] Executar migration script
- [ ] Migrar dados existentes
- [ ] Testar integridade referencial
- [ ] Popular mesas

### 3. **Frontend (Angular)**
- [ ] Criar componente de Mesas (layout visual)
- [ ] Refatorar componente de Comanda
- [ ] Criar fluxo de divisão de conta
- [ ] Criar cadastro de Cliente completo
- [ ] Atualizar gestão de ContaCliente

### 4. **Testes**
- [ ] Testar abertura de comanda (balcão vs mesa)
- [ ] Testar vinculação de múltiplos clientes
- [ ] Testar divisão de conta
- [ ] Testar gestão de mesas
- [ ] Testar crédito com limite

---

## 🤔 Considerações

### Perguntas para Decidir:

1. **Mesa é obrigatória para tipo MESA?**
   - Sugestão: Sim, validar no backend

2. **Permitir transferência de mesa?**
   - Sugestão: Sim, útil quando mesa fica suja

3. **Limite de clientes por comanda?**
   - Sugestão: Máximo 10 (configurável)

4. **Como lidar com comandas antigas na migração?**
   - Criar cliente "Sem Cadastro" para comandas antigas
   - Ou deixar comanda_cliente_id NULL (compatibilidade)

5. **Cliente sem cadastro pode abrir comanda?**
   - Sugestão: Sim, mas cria Cliente com dados mínimos

---

**Precisa de ajuda para implementar alguma parte específica?** 🚀
