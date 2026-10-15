# 🛡️ Segurança — DevBank

Este documento descreve como a segurança é tratada no DevBank e como reportar vulnerabilidades.

> ⚠️ Projeto de estudo/portfólio. Não use com dados reais.

## 📣 Reportando uma vulnerabilidade

Encontrou uma falha de segurança? Por favor, **não abra uma issue pública**.
Use o recurso [Report a vulnerability](../../security/advisories/new) do GitHub (aba **Security**).

## 🧱 Princípios

- **Negar por padrão:** todo endpoint exige autenticação, exceto os explicitamente públicos (cadastro e login).
- **Menor privilégio:** cada usuário acessa apenas os próprios dados.
- **Defesa em camadas:** nenhuma proteção depende de uma única barreira.
- **Análise de aplicabilidade:** vulnerabilidades em dependências são avaliadas antes de decidir a correção.

## ✅ Medidas implementadas

| Medida | Descrição |
|---|---|
| Monitoramento de dependências | CVEs em dependências são acompanhados e corrigidos via override de versão quando necessário |
| Overrides de segurança | Tomcat e Jackson atualizados para versões corrigidas antes do patch oficial do Spring Boot |
| Dependabot | Alertas e PRs automáticos para CVEs em dependências |
| CodeQL | Análise estática do código a cada push |
| Private vulnerability reporting | Canal privado para reportar falhas |

## 🔜 Medidas planejadas

| Medida | Protege contra | Fase |
|---|---|---|
| DTOs com campos explícitos (records) | Mass Assignment (OWASP API3) | 3 |
| Validação de entrada (Bean Validation) | Dados inválidos ou maliciosos | 3 |
| Tratamento global de erros | Vazamento de detalhes internos (stack traces) | 4 |
| Transações atômicas (`@Transactional`) | Inconsistência de saldo em falhas | 6 |
| Autenticação com JWT | Acesso não autenticado | 10 |
| Endpoints `/me` (ID vindo do token) | BOLA / IDOR (OWASP API1) | 10 |
| Senhas com hash (BCrypt) | Vazamento de senhas | 10 |
| Reautenticação para alterar e-mail e senha | Account Takeover | 10 |
| Controle de acesso por papéis (RBAC) | Escalada de privilégio | Bônus |
| Confirmação de e-mail + quarentena | Account Takeover | Bônus |
| 2FA com app autenticador (TOTP) | Roubo de credenciais | Bônus |

## ⚠️ Riscos conhecidos e aceitos

| Item | Justificativa |
|---|---|
| Logback CVE-2026-19880 | A funcionalidade vulnerável (MDC discriminator + FileAppender) não é utilizada. Será corrigido ao atualizar para Spring Boot 4.1.2+ |
