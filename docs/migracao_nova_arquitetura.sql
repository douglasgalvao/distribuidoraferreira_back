-- ==========================================
-- SCRIPT DE MIGRAÇÃO - NOVA ARQUITETURA
-- Sistema de Comandas, Mesas e Clientes
-- Data: 08/11/2025
-- ==========================================

-- ATENÇÃO: Execute este script em ambiente de desenvolvimento primeiro!
-- Faça backup completo antes de executar em produção!

BEGIN;

-- ==========================================
-- 1. CRIAR NOVAS TABELAS
-- ==========================================

-- Tabela: clientes (nova estrutura simplificada)
CREATE TABLE IF NOT EXISTS clientes (
    cliente_id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255),
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT true,
    observacoes VARCHAR(500)
);

-- Tabela: mesas
CREATE TABLE IF NOT EXISTS mesas (
    mesa_id BIGSERIAL PRIMARY KEY,
    numero INTEGER UNIQUE NOT NULL,
    capacidade INTEGER,
    localizacao VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'LIVRE',
    ativo BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT chk_status_mesa CHECK (status IN ('LIVRE', 'OCUPADA', 'RESERVADA', 'MANUTENCAO'))
);

-- Tabela: comandas_clientes (relacionamento N:N)
CREATE TABLE IF NOT EXISTS comandas_clientes (
    comanda_cliente_id BIGSERIAL PRIMARY KEY,
    comanda_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    percentual_divisao DECIMAL(5,2) DEFAULT 100.00,
    principal BOOLEAN NOT NULL DEFAULT false,
    data_vinculo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comanda FOREIGN KEY (comanda_id) REFERENCES comandas(comanda_id) ON DELETE CASCADE,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id) ON DELETE CASCADE
);

-- Tabela: pagamentos
CREATE TABLE IF NOT EXISTS pagamentos (
    pagamento_id BIGSERIAL PRIMARY KEY,
    conta_cliente_id BIGINT,
    venda_id BIGINT,
    data_pagamento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor DECIMAL(10,2) NOT NULL,
    metodo_pagamento VARCHAR(50) NOT NULL,
    observacoes VARCHAR(500),
    CONSTRAINT fk_pagamento_conta FOREIGN KEY (conta_cliente_id) REFERENCES contas_cliente(conta_cliente_id),
    CONSTRAINT fk_pagamento_venda FOREIGN KEY (venda_id) REFERENCES vendas(venda_id)
);

-- ==========================================
-- 2. MODIFICAR TABELA COMANDAS
-- ==========================================

-- Adicionar novas colunas
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS codigo VARCHAR(50) UNIQUE;
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS data_abertura TIMESTAMP;
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS data_fechamento TIMESTAMP;
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS tipo VARCHAR(20);
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS observacoes VARCHAR(500);
ALTER TABLE comandas ADD COLUMN IF NOT EXISTS mesa_id BIGINT;

-- Adicionar constraint de tipo
ALTER TABLE comandas ADD CONSTRAINT IF NOT EXISTS chk_tipo_comanda 
    CHECK (tipo IN ('BALCAO', 'MESA', 'DELIVERY'));

-- Adicionar FK para mesa
ALTER TABLE comandas ADD CONSTRAINT IF NOT EXISTS fk_comanda_mesa 
    FOREIGN KEY (mesa_id) REFERENCES mesas(mesa_id);

-- Atualizar dados existentes
UPDATE comandas SET data_abertura = data_hora WHERE data_abertura IS NULL;
UPDATE comandas SET tipo = 'BALCAO' WHERE tipo IS NULL;

-- Gerar códigos únicos para comandas existentes
UPDATE comandas 
SET codigo = 'CMD-' || LPAD(comanda_id::TEXT, 8, '0') 
WHERE codigo IS NULL;

-- Remover colunas antigas (comentado por segurança - descomente se tiver certeza)
-- ALTER TABLE comandas DROP COLUMN IF EXISTS conta_cliente_id;

-- ==========================================
-- 3. MODIFICAR TABELA CONTAS_CLIENTE
-- ==========================================

-- Adicionar novas colunas
ALTER TABLE contas_cliente ADD COLUMN IF NOT EXISTS cliente_id BIGINT;
ALTER TABLE contas_cliente ADD COLUMN IF NOT EXISTS data_abertura TIMESTAMP;
ALTER TABLE contas_cliente ADD COLUMN IF NOT EXISTS data_fechamento TIMESTAMP;
ALTER TABLE contas_cliente ADD COLUMN IF NOT EXISTS total_consumido DECIMAL(10,2) DEFAULT 0;
ALTER TABLE contas_cliente ADD COLUMN IF NOT EXISTS limite_credito DECIMAL(10,2);

-- Atualizar dados existentes
UPDATE contas_cliente SET data_abertura = CURRENT_TIMESTAMP WHERE data_abertura IS NULL;
UPDATE contas_cliente SET total_consumido = COALESCE(saldo_devedor, 0) WHERE total_consumido IS NULL;

-- Migrar dados antigos para nova estrutura de clientes
INSERT INTO clientes (nome, telefone, data_cadastro, ativo)
SELECT DISTINCT nome_cliente, telefone, CURRENT_TIMESTAMP, true
FROM contas_cliente
WHERE nome_cliente NOT IN (SELECT nome FROM clientes)
ON CONFLICT (cpf) DO NOTHING;

-- Vincular contas existentes aos novos clientes
UPDATE contas_cliente cc
SET cliente_id = c.cliente_id
FROM clientes c
WHERE cc.nome_cliente = c.nome
AND cc.cliente_id IS NULL;

-- Adicionar FK (comentado por segurança - descomente após validar dados)
-- ALTER TABLE contas_cliente ADD CONSTRAINT fk_conta_cliente 
--     FOREIGN KEY (cliente_id) REFERENCES clientes(cliente_id);

-- Remover colunas antigas (comentado por segurança)
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS nome_cliente;
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS telefone;
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS total_pago_pix;
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS total_pago_debito;
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS total_pago_credito;
-- ALTER TABLE contas_cliente DROP COLUMN IF EXISTS total_pago_dinheiro;

-- ==========================================
-- 4. MODIFICAR TABELA VENDAS
-- ==========================================

-- Adicionar nova coluna
ALTER TABLE vendas ADD COLUMN IF NOT EXISTS comanda_cliente_id BIGINT;

-- Adicionar FK
ALTER TABLE vendas ADD CONSTRAINT IF NOT EXISTS fk_venda_comanda_cliente 
    FOREIGN KEY (comanda_cliente_id) REFERENCES comandas_clientes(comanda_cliente_id);

-- ==========================================
-- 5. CRIAR ÍNDICES
-- ==========================================

-- Clientes
CREATE INDEX IF NOT EXISTS idx_clientes_cpf ON clientes(cpf);
CREATE INDEX IF NOT EXISTS idx_clientes_telefone ON clientes(telefone);
CREATE INDEX IF NOT EXISTS idx_clientes_ativo ON clientes(ativo);

-- Mesas
CREATE INDEX IF NOT EXISTS idx_mesas_numero ON mesas(numero);
CREATE INDEX IF NOT EXISTS idx_mesas_status ON mesas(status);
CREATE INDEX IF NOT EXISTS idx_mesas_ativo ON mesas(ativo);

-- Comandas
CREATE INDEX IF NOT EXISTS idx_comandas_codigo ON comandas(codigo);
CREATE INDEX IF NOT EXISTS idx_comandas_tipo ON comandas(tipo);
CREATE INDEX IF NOT EXISTS idx_comandas_mesa ON comandas(mesa_id);
CREATE INDEX IF NOT EXISTS idx_comandas_data_abertura ON comandas(data_abertura DESC);

-- Comandas Clientes
CREATE INDEX IF NOT EXISTS idx_comandas_clientes_comanda ON comandas_clientes(comanda_id);
CREATE INDEX IF NOT EXISTS idx_comandas_clientes_cliente ON comandas_clientes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_comandas_clientes_principal ON comandas_clientes(principal);

-- Contas Cliente
CREATE INDEX IF NOT EXISTS idx_contas_cliente_cliente ON contas_cliente(cliente_id);
CREATE INDEX IF NOT EXISTS idx_contas_cliente_status ON contas_cliente(status);

-- Pagamentos
CREATE INDEX IF NOT EXISTS idx_pagamentos_conta ON pagamentos(conta_cliente_id);
CREATE INDEX IF NOT EXISTS idx_pagamentos_venda ON pagamentos(venda_id);
CREATE INDEX IF NOT EXISTS idx_pagamentos_data ON pagamentos(data_pagamento DESC);

-- Vendas
CREATE INDEX IF NOT EXISTS idx_vendas_comanda_cliente ON vendas(comanda_cliente_id);

-- ==========================================
-- 6. DADOS INICIAIS (OPCIONAL)
-- ==========================================

-- Inserir mesas de exemplo
INSERT INTO mesas (numero, capacidade, localizacao, status) VALUES
(1, 4, 'Área Externa', 'LIVRE'),
(2, 4, 'Área Externa', 'LIVRE'),
(3, 6, 'Salão Principal', 'LIVRE'),
(4, 6, 'Salão Principal', 'LIVRE'),
(5, 2, 'Varanda', 'LIVRE')
ON CONFLICT (numero) DO NOTHING;

-- ==========================================
-- 7. VALIDAÇÕES
-- ==========================================

-- Verificar contagem de registros
DO $$
BEGIN
    RAISE NOTICE 'Total de clientes: %', (SELECT COUNT(*) FROM clientes);
    RAISE NOTICE 'Total de mesas: %', (SELECT COUNT(*) FROM mesas);
    RAISE NOTICE 'Total de comandas: %', (SELECT COUNT(*) FROM comandas);
    RAISE NOTICE 'Total de contas cliente: %', (SELECT COUNT(*) FROM contas_cliente);
END $$;

-- ==========================================
-- FIM DA MIGRAÇÃO
-- ==========================================

COMMIT;

-- ==========================================
-- ROLLBACK EM CASO DE ERRO
-- ==========================================
-- Se algo der errado, execute: ROLLBACK;

-- ==========================================
-- PRÓXIMOS PASSOS APÓS VALIDAÇÃO
-- ==========================================
-- 1. Testar todas as operações CRUD
-- 2. Validar integridade referencial
-- 3. Executar testes de carga
-- 4. Documentar mudanças no código
-- 5. Atualizar documentação da API
-- 6. Treinar equipe nas novas funcionalidades
