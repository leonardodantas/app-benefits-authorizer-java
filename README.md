# Benefits Authorizer

Microsserviço de autorização de transações para cartões de benefícios. Responsável por criar cartões, consultar saldos e processar transações com validação de senha e saldo, suportando concorrência com lock pessimista.

## Tecnologias

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 21 | Runtime |
| Spring Boot | 3.4+ | Framework principal |
| Spring Data JPA | — | Persistência relacional |
| MySQL | 8.0 | Banco de dados |
| Flyway | — | Migration do schema |
| jBCrypt | 0.4 | Hashing de senhas |
| MapStruct | — | Mapeamento entre camadas |
| Lombok | — | Redução de boilerplate |
| JUnit 5 | — | Testes unitários |
| Mockito | — | Mocks |
| Testcontainers | 1.20 | Testes de integração com MySQL real |
| ArchUnit | 1.3 | Testes de arquitetura |
| SpringDoc OpenAPI | — | Documentação Swagger |
| JaCoCo | 0.8.12 | Cobertura de código (100%) |
| GitHub Actions | — | CI |

## Arquitetura

O projeto segue uma arquitetura em camadas com domínio puro, inspirada em DDD e princípios de clean architecture:

```
api  →  app  →  domain
 ↓
infra  →  domain
```

### Camadas

- **api** — Controllers, DTOs de request/response, mappers MapStruct, validações HTTP. Nunca contém regras de negócio.
- **app** — Use cases, interfaces de repositório, orquestração. Conhece apenas o domínio.
- **domain** — Entidades imutáveis (records), exceções de negócio, value objects. Puro, sem frameworks.
- **infra** — Implementações concretas (JPA, BCrypt), entities, mappers.
- **config** — Beans e configurações do Spring.

### Fluxo de requisição

```
HTTP Request
    ↓
Controller (api)
    ↓
Use Case (app)
    ↓
Repository Interface (app)
    ↓
Repository Impl (infra)
    ↓
Database
    ↓
Resposta
```

## Funcionalidades

### Criar cartão (`POST /cartoes`)

Cria um novo cartão com número de 16 dígitos e senha. O saldo inicial é configurável (padrão: R$ 500,00).

- `201` — Cartão criado com sucesso
- `400` — Dados inválidos (formato do cartão, campos obrigatórios)
- `422` — Cartão já existe

### Consultar saldo (`GET /cartoes/{numeroCartao}`)

Retorna o saldo atual do cartão.

- `200` — Saldo retornado
- `404` — Cartão não encontrado

### Processar transação (`POST /transacoes`)

Processa uma transação utilizando o padrão **Chain of Responsibility**, onde cada handler é responsável por uma única validação e decide se a chain deve continuar, parar por erro ou finalizar com sucesso — tudo controlado pelo `HandlerStatus` no `TransactionContext`.

Isolamento de responsabilidades:
1. **CardExistenceHandler** — verifica se o cartão existe (com lock pessimista)
2. **PasswordValidationHandler** — valida a senha
3. **BalanceValidationHandler** — valida se há saldo suficiente
4. **DebitHandler** — debita o valor e persiste o cartão atualizado

Cada handler define `CONTINUE`, `STOP` ou `SUCCESS` no contexto. Se `STOP`, a exception armazenada é relançada pelo `TransactionExecutor` e tratada centralizadamente pelo `ApiExceptionHandler`, eliminando `throw`s espalhados pela chain e mantendo o fluxo previsível e testável.

- `201` — Transação aprovada
- `400` — Dados inválidos
- `422` — Cartão inexistente / Senha inválida / Saldo insuficiente

### Concorrência


Transações concorrentes para o mesmo cartão são serializadas via `PESSIMISTIC_WRITE`, garantindo que não haja condição de corrida no saldo. O teste de integração valida este cenário.

## Como Executar

### Pré-requisitos

- Java 21
- Docker (para MySQL)

### Passos

```bash
# Iniciar MySQL
docker compose up -d

# Executar a aplicação
./mvnw spring-boot:run
```

### Documentação da API

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI spec**: `http://localhost:8080/v3/api-docs`
- **Postman**: importe o arquivo `postman/collection.json` no Postman

### Executar Testes

```bash
# Todos os testes
./mvnw verify
```

O relatório de cobertura estará em `target/site/jacoco/index.html`.

## Testes

O projeto possui **14 arquivos de teste** distribuídos em três categorias:

### Unitários (12)

Testam classes isoladamente com mocks, sem infraestrutura externa.

| Teste | O que valida |
|---|---|
| `CardControllerTest` | Endpoints de criar e consultar cartão |
| `TransactionControllerTest` | Endpoint de transação |
| `ApiExceptionHandlerTest` | Handlers de fallback (CustomException e RuntimeException) |
| `CreateCardUseCaseImplTest` | Criação de cartão (sucesso e duplicado) |
| `CreateTransactionUseCaseImplTest` | Execução de transação via executor |
| `GetBalanceUseCaseImplTest` | Consulta de saldo (existe e não existe) |
| `TransactionExecutorTest` | Sucesso, stop com exception e stop sem exception |
| `TransactionHandlerTest` | Propagação da chain e captura de exceções |
| `CardExistenceHandlerTest` | Validação de existência do cartão |
| `PasswordValidationHandlerTest` | Validação de senha |
| `BalanceValidationHandlerTest` | Validação de saldo |
| `DebitHandlerTest` | Débito e persistência do cartão |

### Integração (1)

Testa o fluxo completo com MySQL real via Testcontainers.

| Teste | O que valida |
|---|---|
| `BenefitsAuthorizerIntegrationTest` | Criação de cartão, consulta de saldo, transações (sucesso e erros), concorrência |

### Arquitetura (1)

Garante que as regras de dependência entre camadas sejam respeitadas.

| Teste | O que valida |
|---|---|
| `ArchitectureTest` | Domain não depende de ninguém; api não depende de infra; app não depende de api ou infra; infra não depende de api ou config |

### Cobertura

JaCoCo configurado com 100% de cobertura obrigatória para código produtivo (excluindo mappers, DTOs, config e domínio).

## CI

O projeto possui GitHub Actions configurado para executar `mvn verify` em pushes e pull requests para a branch `main`.

## Commits

O projeto segue [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` — nova funcionalidade
- `fix:` — correção de bug
- `refactor:` — mudança interna sem novo comportamento
- `test:` — adição ou alteração de testes
- `chore:` — tarefas de infraestrutura
- `ci:` — configuração de CI/CD
- `docs:` — documentação
