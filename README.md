# Benefits Authorizer

![CI](https://github.com/leonardodantas/app-benefits-authorizer-java/workflows/CI/badge.svg)
![Java](https://img.shields.io/badge/Java-21-blue)
![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)

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

## Endpoints

| Método | Caminho | Descrição |
|---|---|---|
| `POST` | `/cartoes` | Criar cartão |
| `GET` | `/cartoes/{numeroCartao}` | Consultar saldo |
| `POST` | `/transacoes` | Processar transação |
| `GET` | `/transacoes/{numeroCartao}` | Histórico paginado de transações |

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

### Consultar histórico de transações (`GET /transacoes/{numeroCartao}?page=0&size=20`)

Retorna o histórico paginado de transações de um cartão, ordenado da mais recente para a mais antiga. Cada transação registra o saldo anterior, novo saldo, valor e data/hora.

- `200` — Histórico retornado com sucesso

## Como Executar

### Pré-requisitos

- **Java 21** (Corretto, OpenJDK ou GraalVM)
- **Docker** (para executar o MySQL localmente via docker-compose)
- **Maven Wrapper** (`./mvnw`) já incluso no projeto

### Variáveis de ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/miniautorizador` | URL do MySQL |
| `SPRING_DATASOURCE_USERNAME` | `root` | Usuário do MySQL |
| `SPRING_DATASOURCE_PASSWORD` | `root` | Senha do MySQL |
| `APP_INITIAL_BALANCE` | `500` | Saldo inicial ao criar cartão |

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

O projeto possui **17 arquivos de teste** distribuídos em três categorias:

### Unitários (15)

Testam classes isoladamente com mocks, sem infraestrutura externa.

| Teste | O que valida |
|---|---|
| `CardControllerTest` | Endpoints de criar e consultar cartão |
| `TransactionControllerTest` | Endpoint de transação e histórico |
| `ApiExceptionHandlerTest` | Handlers de fallback (CustomException e RuntimeException) |
| `CreateCardUseCaseImplTest` | Criação de cartão (sucesso e duplicado) |
| `CreateTransactionUseCaseImplTest` | Execução de transação via executor |
| `GetBalanceUseCaseImplTest` | Consulta de saldo (existe e não existe) |
| `GetTransactionHistoryUseCaseImplTest` | Histórico paginado de transações |
| `TransactionExecutorTest` | Sucesso, stop com exception e stop sem exception |
| `TransactionHandlerTest` | Propagação da chain e captura de exceções |
| `TransactionEventListenerTest` | Delegacão de evento para consumer |
| `EventPublisherHandlerTest` | Publicação de evento após débito |
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

### Relatório de cobertura

O relatório JaCoCo é publicado automaticamente no GitHub Pages a cada push na `main`. Disponível em:

```
https://leonardodantas.github.io/app-benefits-authorizer-java/
```

## Desacoplamento de infraestrutura

Graças aos princípios de Clean Architecture e à separação entre camadas, a infraestrutura é intercambiável sem alterar lógica de negócio:

- **Banco de dados**: a camada `app` conhece apenas interfaces de repositório (`CardRepository`, `TransactionLogRepository`). A implementação atual usa JPA/MySQL, mas poderia ser substituída por MongoDB, PostgreSQL ou qualquer outra tecnologia sem alterar uma linha dos use cases ou domínio.
- **Eventos**: a publicação de eventos (`TransactionEvent`) é feita via `ApplicationEventPublisher` (Spring), com um consumer em `infra` que persiste no banco. Esse consumer poderia ser substituído por um publisher Kafka ou RabbitMQ sem impacto no domínio ou nos use cases — bastaria criar uma nova implementação de `TransactionEventConsumer` em `infra`.
- **Senhas**: o hashing de senhas é abstraído pela interface `PasswordEncoder` em `app`. A implementação atual usa jBCrypt, mas poderia ser trocada para PBKDF2, Argon2 ou qualquer outro algoritmo trocando apenas o bean em `infra`.

## Overengineering consciente

Algumas decisões neste projeto foram intencionalmente levadas além do estritamente necessário para o problema atual. O objetivo é demonstrar domínio técnico sobre padrões, ferramentas e boas práticas, mesmo que para um cenário simples:

- **Chain of Responsibility com `HandlerStatus`** — uma chain com 3 handlers validadores + 1 handler de débito já resolveria. O `HandlerStatus` (`CONTINUE`, `STOP`, `SUCCESS`) e o tratamento de exceções via contexto foram adicionados para mostrar controle de fluxo explícito e desacoplamento entre handlers.
- **Event publishing com `ApplicationEventPublisher`** — para o tamanho do projeto, logar a transação no banco diretamente resolveria. A camada extra de eventos + `@Async` + interface em `app` com implementação em `infra` existe para demonstrar arquitetura orientada a eventos.
- **Índice composto `(card_number, status)`** — o índice simples em `card_number` já é suficiente para o volume esperado. O índice composto foi adicionado para demonstrar conhecimento em modelagem de índices e otimização de consultas.
- **Testes de arquitetura com ArchUnit** — para um projeto pequeno, testes funcionais já bastam. O ArchUnit foi incluído para garantir que a arquitetura definida não seja violada conforme o projeto cresce.

## Commits

O projeto segue [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` — nova funcionalidade
- `fix:` — correção de bug
- `refactor:` — mudança interna sem novo comportamento
- `test:` — adição ou alteração de testes
- `chore:` — tarefas de infraestrutura
- `ci:` — configuração de CI/CD
- `docs:` — documentação
