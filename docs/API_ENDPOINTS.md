# 📡 API Endpoints - Nova Arquitetura

## Base URL
```
http://localhost:8080/api
```

---

## 👤 CLIENTES

### Criar Cliente
```http
POST /clientes
Content-Type: application/json

{
  "nome": "João Silva",
  "telefone": "(11) 98765-4321",
  "cpf": "123.456.789-00",
  "email": "joao@email.com",
  "observacoes": "Cliente VIP",
  "ativo": true
}
```

### Listar Clientes
```http
GET /clientes?apenasAtivos=true
```

### Buscar Cliente por ID
```http
GET /clientes/{id}
```

### Buscar por CPF
```http
GET /clientes/cpf/12345678900
```

### Buscar por Telefone
```http
GET /clientes/telefone/11987654321
```

### Atualizar Cliente
```http
PUT /clientes/{id}
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "telefone": "(11) 98765-4321",
  "email": "joao.novo@email.com"
}
```

### Ativar/Inativar Cliente
```http
PATCH /clientes/{id}/ativar
PATCH /clientes/{id}/inativar
```

### Deletar Cliente
```http
DELETE /clientes/{id}
```

---

## 🪑 MESAS

### Criar Mesa
```http
POST /mesas
Content-Type: application/json

{
  "numero": "01",
  "capacidade": 4,
  "localizacao": "Salão Principal",
  "status": "LIVRE",
  "ativo": true
}
```

### Listar Mesas
```http
GET /mesas?apenasAtivas=true
```

### Buscar Mesa por ID
```http
GET /mesas/{id}
```

### Buscar por Número
```http
GET /mesas/numero/01
```

### Listar por Status
```http
GET /mesas/status/LIVRE
GET /mesas/status/OCUPADA
GET /mesas/status/RESERVADA
GET /mesas/status/MANUTENCAO
```

### Atualizar Mesa
```http
PUT /mesas/{id}
Content-Type: application/json

{
  "numero": "01A",
  "capacidade": 6,
  "localizacao": "Área VIP"
}
```

### Controlar Status da Mesa
```http
PATCH /mesas/{id}/ocupar
PATCH /mesas/{id}/liberar
PATCH /mesas/{id}/reservar
PATCH /mesas/{id}/ativar
PATCH /mesas/{id}/inativar
```

### Deletar Mesa
```http
DELETE /mesas/{id}
```

---

## 📋 COMANDAS (v2)

### Criar Comanda
```http
POST /v2/comandas
Content-Type: application/json

{
  "tipo": "MESA",
  "mesaId": 1,
  "observacoes": "Aniversário",
  "clientes": [
    {
      "clienteId": 1,
      "percentualDivisao": 50.00,
      "principal": true
    },
    {
      "clienteId": 2,
      "percentualDivisao": 50.00,
      "principal": false
    }
  ]
}
```

**Tipos de Comanda:**
- `MESA` - Requer mesaId
- `BALCAO` - Não requer mesa
- `DELIVERY` - Não requer mesa

### Listar Comandas
```http
GET /v2/comandas
```

### Buscar Comanda por ID
```http
GET /v2/comandas/{id}
```

### Buscar por Código
```http
GET /v2/comandas/codigo/A1B2C3D4
```

### Listar por Status
```http
GET /v2/comandas/status/ABERTA
GET /v2/comandas/status/FECHADA
GET /v2/comandas/status/CANCELADA
```

### Listar por Tipo
```http
GET /v2/comandas/tipo/MESA
GET /v2/comandas/tipo/BALCAO
GET /v2/comandas/tipo/DELIVERY
```

### Listar por Mesa
```http
GET /v2/comandas/mesa/{mesaId}
```

### Adicionar Cliente à Comanda
```http
POST /v2/comandas/{id}/clientes/{clienteId}?principal=false
```

### Remover Cliente da Comanda
```http
DELETE /v2/comandas/{id}/clientes/{clienteId}
```

### Fechar Comanda
```http
PATCH /v2/comandas/{id}/fechar
```

### Cancelar Comanda
```http
PATCH /v2/comandas/{id}/cancelar
```

### Deletar Comanda
```http
DELETE /v2/comandas/{id}
```

---

## 💰 PAGAMENTOS

### Registrar Pagamento
```http
POST /pagamentos
Content-Type: application/json

{
  "contaClienteId": 1,
  "vendaId": 5,
  "valor": 50.00,
  "metodoPagamento": "DINHEIRO",
  "observacoes": "Pagamento parcial"
}
```

**Métodos de Pagamento:**
- `DINHEIRO`
- `PIX`
- `CARTAO_CREDITO`
- `CARTAO_DEBITO`

### Listar Pagamentos
```http
GET /pagamentos
```

### Buscar Pagamento por ID
```http
GET /pagamentos/{id}
```

### Listar por Conta Cliente
```http
GET /pagamentos/conta/{contaClienteId}
```

### Listar por Venda
```http
GET /pagamentos/venda/{vendaId}
```

### Calcular Total Pago
```http
GET /pagamentos/conta/{contaClienteId}/total
```

### Deletar Pagamento (com reversão)
```http
DELETE /pagamentos/{id}
```

---

## 💳 CONTAS CLIENTE (v2)

### Criar Conta
```http
POST /v2/contas-cliente
Content-Type: application/json

{
  "clienteId": 1,
  "limiteCredito": 500.00,
  "observacoes": "Conta especial"
}
```

### Listar Contas
```http
GET /v2/contas-cliente?apenasAtivas=true
```

### Buscar Conta por ID
```http
GET /v2/contas-cliente/{id}
```

### Listar por Cliente
```http
GET /v2/contas-cliente/cliente/{clienteId}
```

### Atualizar Conta
```http
PUT /v2/contas-cliente/{id}
Content-Type: application/json

{
  "limiteCredito": 1000.00,
  "observacoes": "Limite aumentado"
}
```

### Adicionar Débito
```http
POST /v2/contas-cliente/{id}/debito?valor=150.00
```

### Realizar Pagamento
```http
POST /v2/contas-cliente/{id}/pagamento?valor=100.00
```

### Controlar Status da Conta
```http
PATCH /v2/contas-cliente/{id}/bloquear
PATCH /v2/contas-cliente/{id}/desbloquear
PATCH /v2/contas-cliente/{id}/suspender
PATCH /v2/contas-cliente/{id}/reativar
PATCH /v2/contas-cliente/{id}/fechar
```

**Status de Conta:**
- `ATIVA` - Pode realizar compras
- `SUSPENSA` - Temporariamente bloqueada
- `BLOQUEADA` - Bloqueada por inadimplência
- `QUITADA` - Conta fechada sem débitos

### Deletar Conta
```http
DELETE /v2/contas-cliente/{id}
```

---

## 🔴 Respostas de Erro

### 400 Bad Request
```json
{
  "timestamp": "2025-01-08T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Telefone já cadastrado"
}
```

### 400 Validation Failed
```json
{
  "timestamp": "2025-01-08T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Erros de validação nos campos fornecidos",
  "errors": {
    "nome": "Nome é obrigatório",
    "telefone": "Telefone inválido"
  }
}
```

### 404 Not Found
```json
{
  "timestamp": "2025-01-08T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente não encontrado"
}
```

### 409 Conflict
```json
{
  "timestamp": "2025-01-08T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Mesa não está disponível"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2025-01-08T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ocorreu um erro inesperado. Por favor, tente novamente."
}
```

---

## 📝 Notas Importantes

1. **CORS:** Todos os endpoints aceitam requisições de qualquer origem (`*`)
2. **Validação:** Campos obrigatórios são validados automaticamente
3. **Transações:** Todas as operações são transacionais
4. **Logs:** Todas as requisições são logadas
5. **Status HTTP:** 201 (Created), 200 (OK), 204 (No Content)

---

## 🔧 Exemplo de Fluxo Completo

### 1. Criar Cliente
```http
POST /clientes
{ "nome": "João", "telefone": "11987654321", "cpf": "12345678900" }
```

### 2. Criar Mesa
```http
POST /mesas
{ "numero": "01", "capacidade": 4, "localizacao": "Salão" }
```

### 3. Abrir Comanda
```http
POST /v2/comandas
{
  "tipo": "MESA",
  "mesaId": 1,
  "clientes": [{ "clienteId": 1, "principal": true }]
}
```

### 4. Criar Conta para Cliente
```http
POST /v2/contas-cliente
{ "clienteId": 1, "limiteCredito": 500.00 }
```

### 5. Adicionar Débito (após venda)
```http
POST /v2/contas-cliente/1/debito?valor=150.00
```

### 6. Registrar Pagamento
```http
POST /pagamentos
{
  "contaClienteId": 1,
  "valor": 50.00,
  "metodoPagamento": "DINHEIRO"
}
```

### 7. Fechar Comanda
```http
PATCH /v2/comandas/1/fechar
```

---

**Gerado em:** 2025-01-08  
**Versão da API:** 2.0
