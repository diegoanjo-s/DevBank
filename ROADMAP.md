# 🏦 DevBank — Roadmap

API REST de um banco digital com Java 21 + Spring Boot 4 + Maven.

## Fase 0 — Ambiente ✅
- [x] Java 21 + Maven
- [x] Git instalado e configurado
- [x] IntelliJ IDEA
- [x] Conta e repositório no GitHub

## Fase 1 — Setup do projeto ✅
- [x] Gerar projeto no Spring Initializr
- [x] Abrir no IntelliJ e sincronizar o Maven
- [x] Entender o pom.xml (parent, dependências, scopes, plugins)
- [x] Rodar a aplicação (Tomcat na porta 8080)
- [x] Primeiro commit e push

## Fase 2 — Primeiro endpoint ✅
- [x] Criar o pacote controller
- [x] Endpoint GET /hello
- [x] Testar no navegador e no Postman

## Fase 3 — Clientes ✅
- [x] Spring Data JPA + H2 + Validation
- [x] Entidade Cliente
- [x] Repository
- [x] Service
- [x] DTOs (records)
- [x] CRUD de clientes no Controller

## Fase 4 — Tratamento de erros ✅
- [x] Exceções personalizadas
- [x] @RestControllerAdvice
- [x] Respostas de erro padronizadas

## Fase 5 — Contas ✅
- [x] Entidade Conta
- [x] Relacionamento Cliente ↔ Conta
- [x] Abrir conta
- [x] Consultar saldo
- [x] Encerrar conta

## Fase 6 — Transações ✅
- [x] Entidade Transação
- [x] Depósito
- [x] Saque
- [x] Transferência
- [x] @Transactional
- [x] Regras de negócio (saldo insuficiente, conta inativa)

## Fase 7 — Extrato
- [ ] Listar transações de uma conta
- [ ] Paginação
- [ ] Filtro por período

## Fase 8 — Testes
- [ ] Testes unitários dos Services (JUnit + Mockito)
- [ ] Testes dos Controllers (MockMvc)
- [ ] Testes de integração
- [ ] Ajustar o aviso do Mockito (agent)
- [ ] Cobertura de testes

## Fase 9 — Produção
- [ ] Swagger / OpenAPI
- [ ] PostgreSQL
- [ ] Flyway (migrations)
- [ ] Docker + Docker Compose
- [ ] Profiles (dev / prod)

## Fase 10 — Segurança
- [ ] Spring Security
- [ ] Cadastro e login
- [ ] JWT
- [ ] Proteger endpoints
- [ ] Cada cliente acessa só os próprios dados

## 🛡️ Pendências técnicas
- [ ] Atualizar para Spring Boot 4.1.2+ (logback CVE-2026-19880) e remover os overrides de tomcat/jackson
- [x] Dar permissão de execução ao mvnw (para Linux/Docker/CI)