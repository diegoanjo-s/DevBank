# 🏦 DevBank

[![CI](https://github.com/diegoanjo-s/DevBank/actions/workflows/ci.yml/badge.svg)](https://github.com/diegoanjo-s/DevBank/actions/workflows/ci.yml)

API REST de um banco digital, desenvolvida com **Java 21** e **Spring Boot 4**, com foco em boas práticas de backend: arquitetura em camadas, regras de negócio bancárias, testes e segurança.

## 🌐 Teste online

| | Link |
|---|---|
| 🖥️ **Site** (cadastro, login, depósito, saque, transferência e extrato) | [diegoanjo-s.vercel.app](https://diegoanjo-s.vercel.app) |
| 📖 **Documentação da API** (Swagger) | [devbank-api-grsh.onrender.com/swagger-ui.html](https://devbank-api-grsh.onrender.com/swagger-ui.html) |
| 🎨 **Código do site** (React + TypeScript) | [diegoanjo-s/portfolio](https://github.com/diegoanjo-s/portfolio) |

> ⏳ A API roda no plano gratuito do Render e "dorme" depois de 15 minutos sem uso. O primeiro acesso pode levar até 1 minuto.

```
Navegador → Site (Vercel) → API Java (Render, Docker) → PostgreSQL (Neon)
```

> ⚠️ **Banco fictício, para estudo e portfólio.** O saldo é simulado e não há dinheiro de verdade. Não use dados reais.
>
> 📋 Veja o progresso no [ROADMAP](ROADMAP.md) e como a segurança é tratada em [SECURITY.md](SECURITY.md).

## 🛠️ Tecnologias

- Java 21 e Spring Boot 4 (Web MVC)
- Spring Data JPA, H2 (desenvolvimento) e PostgreSQL (produção)
- Flyway para as migrations do banco, com índices medidos via `EXPLAIN ANALYZE` ([ver otimização](docs/otimizacao-sql.md))
- Spring Security com JWT e senhas protegidas com BCrypt
- Bean Validation e Swagger (OpenAPI)
- JUnit 5, Mockito e MockMvc
- Docker e Docker Compose
- Maven e Lombok
- GitHub Actions (integração contínua: testes automáticos a cada push e pull request)
- Deploy: Render (API), Neon (PostgreSQL) e Vercel (site)

## ✨ Funcionalidades

- 👤 Cadastro e gestão de clientes
- 🔐 Login com token JWT
- 🛡️ Cada cliente acessa só os próprios dados
- 🏦 Abertura e encerramento de contas, com saldo inicial simulado de R$ 300,00
- 💸 Depósito, saque e transferência entre contas, tudo ou nada
- 📄 Extrato com filtro por período e paginação

## 🔗 Endpoints

| Método | Rota | Descrição | Token |
|---|---|---|---|
| POST | `/clientes` | Cadastra um cliente | Não |
| POST | `/auth/login` | Faz login e devolve o token | Não |
| GET | `/clientes/{id}` | Busca os próprios dados | Sim |
| PUT | `/clientes/{id}` | Atualiza nome e e-mail | Sim |
| DELETE | `/clientes/{id}` | Remove o próprio cadastro | Sim |
| POST | `/contas` | Abre uma conta | Sim |
| GET | `/contas` | Lista as contas do cliente logado | Sim |
| GET | `/contas/{id}` | Consulta a conta e o saldo | Sim |
| PATCH | `/contas/{id}/encerrar` | Encerra uma conta com saldo zero | Sim |
| POST | `/contas/{id}/deposito` | Deposita um valor | Sim |
| POST | `/contas/{id}/saque` | Saca um valor | Sim |
| POST | `/transferencias` | Transfere entre contas | Sim |
| GET | `/contas/{id}/extrato` | Consulta o extrato | Sim |

Para usar o token, envie o cabeçalho `Authorization: Bearer <token>`. Na documentação interativa, use o botão **Authorize**.

## ▶️ Como rodar

### Desenvolvimento (H2 em memória)

**Pré-requisito:** Java 21

```bash
git clone https://github.com/diegoanjo-s/DevBank.git
cd DevBank
./mvnw spring-boot:run
```

Este modo já cria dados de teste: `diego@email.com`, `maria@email.com` e `joao@email.com`, todos com a senha `senha123`.

### Com Docker (PostgreSQL)

**Pré-requisito:** Docker

```bash
git clone https://github.com/diegoanjo-s/DevBank.git
cd DevBank
cp .env.example .env
```

Abra o `.env` e troque `DB_PASSWORD` e `JWT_SECRET`. O `JWT_SECRET` precisa ter pelo menos 32 caracteres, e este comando gera um valor adequado:

```bash
openssl rand -hex 48
```

Depois, suba a aplicação:

```bash
docker compose up --build
```

Este modo começa sem dados: cadastre um cliente em `POST /clientes` para testar.

### Depois de subir

- API: `http://localhost:8080`
- Documentação interativa: `http://localhost:8080/swagger-ui.html`

## 🧪 Testes

```bash
./mvnw test
```

Os testes também rodam automaticamente no **GitHub Actions** a cada push e a cada pull request ([ver execuções](https://github.com/diegoanjo-s/DevBank/actions)).

## 👨‍💻 Autor

**Diego Anjos Aragão**
[GitHub](https://github.com/diegoanjo-s) · [LinkedIn](https://linkedin.com/in/diegoanjo-s)
