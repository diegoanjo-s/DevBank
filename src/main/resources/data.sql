-- Dados de teste: carregados automaticamente toda vez que a aplicação sobe

INSERT INTO clientes (nome, cpf, email, data_cadastro) VALUES ('Diego Anjos', '52998224725', 'diego@email.com', CURRENT_TIMESTAMP);
INSERT INTO clientes (nome, cpf, email, data_cadastro) VALUES ('Maria Silva', '11144477735', 'maria@email.com', CURRENT_TIMESTAMP);
INSERT INTO clientes (nome, cpf, email, data_cadastro) VALUES ('João Souza', '12345678909', 'joao@email.com', CURRENT_TIMESTAMP);

INSERT INTO contas (numero, saldo, status, cliente_id, data_abertura) VALUES ('10000001', 1000.00, 'ATIVA', 1, CURRENT_TIMESTAMP);
INSERT INTO contas (numero, saldo, status, cliente_id, data_abertura) VALUES ('10000002', 500.00, 'ATIVA', 2, CURRENT_TIMESTAMP);
INSERT INTO contas (numero, saldo, status, cliente_id, data_abertura) VALUES ('10000003', 0.00, 'ENCERRADA', 3, CURRENT_TIMESTAMP);