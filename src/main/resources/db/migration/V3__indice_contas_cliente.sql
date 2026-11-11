-- O PostgreSQL não cria índice automaticamente em chave estrangeira.
-- Sem este índice, buscar as contas de um cliente (GET /contas e a regra
-- "cliente já tem conta ativa?") obrigava o banco a ler a tabela inteira (Seq Scan).
-- Medição com 150 mil contas: ver docs/otimizacao-sql.md
CREATE INDEX idx_contas_cliente ON contas (cliente_id);
