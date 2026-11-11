# Otimização de consultas SQL com índices

Medições feitas com `EXPLAIN (ANALYZE, BUFFERS)` num PostgreSQL 18 (mesma versão do banco de produção no Neon), com dados de teste:

| Tabela | Linhas |
|---|---|
| clientes | 100.000 |
| contas | 150.000 |
| transacoes | 2.000.000 |

Cada consulta foi executada 3 vezes e o tempo registrado é o da última (com cache aquecido).

## 1. Contas de um cliente — `V3__indice_contas_cliente.sql`

O PostgreSQL **não cria índice automaticamente em chave estrangeira**. A coluna `contas.cliente_id` é usada em:

- `GET /contas` → `findByClienteIdOrderByDataAberturaDesc` (painel do site)
- abertura de conta → `existsByClienteIdAndStatus` (regra "cliente já tem conta ativa?")

Sem índice, o banco lia a tabela inteira (`Seq Scan`) para encontrar 1 conta.

```sql
CREATE INDEX idx_contas_cliente ON contas (cliente_id);
```

| Consulta | Antes | Depois | Ganho |
|---|---|---|---|
| Contas do cliente logado | Seq Scan, 1.405 páginas, **8,14 ms** | Index Scan, 10 páginas, **0,076 ms** | ~107× |
| Cliente já tem conta ativa? | Seq Scan, 509 páginas, **2,37 ms** | Index Scan, 4 páginas, **0,017 ms** | ~140× |

## 2. Extrato — índice composto da `V1` (já existente)

O extrato filtra por conta e período e ordena por data. O índice composto `(conta_id, data_hora)` atende os três ao mesmo tempo: o banco encontra a conta, percorre o período já ordenado (`Index Scan Backward`) e para assim que tem as 10 linhas da página.

```sql
CREATE INDEX idx_transacoes_conta_data ON transacoes (conta_id, data_hora);
```

| Consulta | Sem o índice | Com o índice | Ganho |
|---|---|---|---|
| Extrato (10 últimas transações em 30 dias) | Parallel Seq Scan, **37,27 ms** | Index Scan Backward, **0,086 ms** | ~430× |

## Como reproduzir

```bash
docker run -d --name devbank-bench -e POSTGRES_PASSWORD=bench -e POSTGRES_DB=devbank postgres:18
# aplicar V1, V2 e V3, popular com generate_series e rodar:
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM contas WHERE cliente_id = 54321 ORDER BY data_abertura DESC;
```

## Lição

Em tabelas pequenas a diferença não aparece, e por isso é fácil esquecer índices em desenvolvimento. Com volume de produção, uma consulta que lê a tabela inteira cresce junto com os dados, enquanto a consulta com índice continua praticamente constante.
