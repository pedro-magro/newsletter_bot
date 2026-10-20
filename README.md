# 📰 NewsBot — Newsletter personalizada para Telegram

O NewsBot é uma aplicação back-end em Java e Spring Boot para criar newsletters personalizadas no Telegram.

A ideia é permitir que cada usuário escolha assuntos de interesse — atualmente Tecnologia, Cibersegurança e Economia — e, em uma etapa posterior, receba notícias agrupadas de acordo com essas preferências.

O projeto está sendo desenvolvido como estudo prático de back-end Java, integração com APIs externas, persistência relacional e processamento assíncrono.

## Objetivo

O objetivo do projeto é construir um fluxo completo de newsletter:

1. O usuário inicia uma conversa com o bot pelo comando `/start`.
2. O bot apresenta os assuntos disponíveis.
3. O usuário seleciona ou remove assuntos por meio de botões.
4. As preferências são persistidas no banco de dados.
5. O sistema coleta notícias de provedores externos.
6. O sistema remove duplicidades e monta uma newsletter.
7. A newsletter é enviada ao usuário no horário configurado.

O fluxo implementado atualmente cobre apenas parte da interação inicial com o Telegram. A coleta de notícias, a geração das edições e o envio periódico ainda fazem parte do roadmap.

## Status atual

**Fase: estruturação do domínio e integração inicial com o Telegram.**

Já existem no projeto:

- aplicação Spring Boot com Java 21;
- entidades JPA para usuários, assuntos, notícias, provedores e newsletters;
- repositórios Spring Data JPA;
- migration inicial do banco usando Flyway;
- cliente HTTP para a Telegram Bot API usando `RestClient`;
- processamento inicial do comando `/start`;
- menu de assuntos com botões inline;
- seleção temporária de assuntos em memória;
- serviço de long polling para consultar atualizações do Telegram;
- configuração inicial para PostgreSQL;
- Dockerfile em desenvolvimento.

Ainda não está concluído:

- persistência efetiva das preferências selecionadas pelo usuário;
- validação completa do mapeamento entre entidades e tabelas;
- coleta de notícias em APIs externas;
- integração com GNews ou NewsData.io;
- geração e envio automático de newsletters;
- execução validada do build completo;
- testes unitários e de integração abrangentes;
- RabbitMQ, Redis e Docker Compose;
- preparação para produção.

> O projeto ainda deve ser considerado experimental. Antes de executar, é necessário corrigir e validar os pontos pendentes de compilação e configuração.

## Tecnologias

| Tecnologia | Uso | Estado |
|---|---|---|
| Java 21 | Linguagem principal | Em uso |
| Spring Boot | Aplicação e configuração | Em uso |
| Spring Web / `RestClient` | Comunicação com a API do Telegram | Em uso |
| Spring Data JPA / Hibernate | Persistência e mapeamento ORM | Estruturado |
| PostgreSQL | Banco de dados principal | Configuração inicial |
| Flyway | Versionamento do schema | Migration inicial |
| Telegram Bot API | Interação com usuários | Integração inicial |
| Maven | Build e dependências | Em uso |
| Lombok | Getters e setters das entidades | Em uso |
| RabbitMQ | Filas e processamento assíncrono | Planejado |
| Redis | Cache, cotas e rate limiting | Planejado |
| Docker Compose | Execução da aplicação e infraestrutura | Planejado |
| JUnit, Mockito e Testcontainers | Testes | Planejado |

## Pré-requisitos

Para executar localmente, instale:

- JDK 21;
- Docker, caso o PostgreSQL seja executado em container;
- um bot criado pelo [BotFather](https://t.me/BotFather) no Telegram;
- PostgreSQL acessível pela aplicação;
- Windows: use `mvnw.cmd`; Linux/macOS: use `./mvnw`.

O projeto inclui o Maven Wrapper, portanto não é necessário instalar o Maven separadamente.

## Configuração local

Crie um arquivo `.env` na raiz do projeto. Ele deve permanecer fora do controle de versão:

```dotenv
DB_URLL=jdbc:postgresql://localhost:5432/newsbot
DB_USER=newsbot
DB_PASSWORD=newsbot
TELEGRAM_BOT_TOKEN=coloque_o_token_do_bot_aqui
```

As propriedades são carregadas por `src/main/resources/application.properties`.

A aplicação usa:

- porta HTTP `8080`;
- PostgreSQL no banco `newsbot`;
- Flyway para aplicar `src/main/resources/db/migration/V1_create_db.sql`;
- long polling para receber eventos do Telegram;
- `ddl-auto=validate`, ou seja, o Hibernate valida o schema, mas não cria tabelas automaticamente.

## Banco de dados

O PostgreSQL precisa estar disponível antes de iniciar a aplicação. Exemplo usando Docker:

```powershell
docker run --name newsbot-postgres `
  -e POSTGRES_DB=newsbot `
  -e POSTGRES_USER=newsbot `
  -e POSTGRES_PASSWORD=newsbot `
  -p 5432:5432 `
  -d postgres:16
```

Depois, confirme que o arquivo `.env` aponta para essa mesma configuração.

## Como executar localmente

Na raiz do projeto, no Windows:

```powershell
mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

Quando a aplicação estiver em execução, envie `/start` para o bot no Telegram.

## Build e testes

Para executar apenas os testes:

```powershell
mvnw.cmd test
```

Para compilar, testar e empacotar:

```powershell
mvnw.cmd clean verify
```

Para gerar apenas o JAR sem executar os testes:

```powershell
mvnw.cmd clean package -DskipTests
```

O arquivo gerado ficará em `target/`.

Se o Maven Wrapper não conseguir baixar a distribuição ou criar seu cache local, verifique as permissões do usuário e o acesso à internet. Esse problema é diferente de um erro de compilação do código.

## Docker

O Dockerfile está em desenvolvimento. A intenção é utilizar um build em duas etapas: uma imagem Maven/JDK para compilar o projeto e uma imagem menor com apenas o JRE para executar o JAR.

Depois que o build Maven estiver passando, a imagem poderá ser construída com:

```powershell
docker build -t newsbot .
```

E executada com:

```powershell
docker run --name newsbot `
  --env-file .env `
  -p 8080:8080 `
  newsbot
```

Para produção, a aplicação e o PostgreSQL devem ser orquestrados com Docker Compose ou outra solução equivalente. O token do Telegram e as credenciais do banco não devem ser gravados na imagem.

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/com/newsletterbot/telegram_bot/
│   │   ├── news/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   ├── newsletter/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   ├── provider/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   ├── telegram/
│   │   │   ├── client/
│   │   │   └── Service/
│   │   ├── topic/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   ├── user/
│   │   │   ├── model/
│   │   │   └── repository/
│   │   └── TelegramBotApplication.java
│   └── resources/
│       ├── db/migration/
│       └── application.properties
└── test/
```

O ponto de entrada é `TelegramBotApplication`.

## Funcionamento atual do Telegram

O `TelegramClient` encapsula chamadas para `getUpdates`, `sendMessage`, `answerCallbackQuery` e envio do menu de assuntos com teclado inline.

O `TelegramPollingService` consulta o Telegram periodicamente e encaminha as atualizações para o `TelegramUpdateService`.

O `TelegramUpdateService` reconhece o comando `/start` e processa callbacks de assuntos. Atualmente, as preferências são mantidas em memória; portanto, elas são perdidas quando a aplicação é reiniciada.

## O que será aprendido

Este projeto permite praticar:

### Java e Spring Boot

- organização de um projeto Maven;
- criação de uma aplicação Spring Boot;
- injeção de dependências;
- configuração por propriedades e variáveis de ambiente;
- criação de componentes, serviços e repositórios;
- uso de tarefas agendadas e long polling.

### Persistência de dados

- modelagem de entidades com JPA;
- relacionamentos `ManyToOne` e `ManyToMany`;
- criação de repositórios com Spring Data;
- validação do schema com Hibernate;
- migrations versionadas com Flyway;
- índices, chaves únicas e deduplicação por hash.

### Integrações externas

- comunicação HTTP com `RestClient`;
- consumo da Telegram Bot API;
- tratamento de respostas JSON;
- controle de timeout, falhas e reprocessamentos;
- integração com múltiplos provedores de notícias.

### Arquitetura e processamento

- separação por domínio em um monólito modular;
- criação de interfaces para provedores intercambiáveis;
- estratégia de fallback entre APIs;
- processamento assíncrono com filas;
- cache e rate limiting;
- idempotência e prevenção de mensagens duplicadas.

### Qualidade e operação

- testes unitários com JUnit e Mockito;
- testes de integração com Testcontainers;
- execução reprodutível com Docker;
- configuração segura de segredos;
- logs, métricas e tratamento de erros;
- preparação de pipeline de CI/CD.

## Roadmap

### Próximos passos

- corrigir e validar o build Maven;
- revisar os relacionamentos JPA e a migration inicial;
- persistir usuários e assuntos selecionados;
- finalizar o fluxo de callbacks do Telegram;
- criar a interface de provedores de notícias;
- integrar o primeiro provedor externo;
- implementar coleta, normalização e deduplicação;
- gerar e enviar a primeira edição de newsletter;
- adicionar testes automatizados.

### Evoluções posteriores

- fallback entre GNews e NewsData.io;
- RabbitMQ para preparação e entrega;
- Redis para cache, cotas e locks;
- retry com backoff e Dead Letter Queue;
- Docker Compose;
- métricas e observabilidade;
- webhook HTTPS em substituição ao long polling;
- recomendações e resumos personalizados.

## Segurança

- nunca versione `.env`, tokens ou senhas;
- use variáveis de ambiente para configurações sensíveis;
- não coloque credenciais em `application.properties` versionado;
- não registre tokens nos logs;
- valide dados recebidos de APIs externas;
- use restrições de unicidade e idempotência para evitar duplicidade.

## Licença e finalidade

Este projeto tem finalidade educacional e de portfólio. A implementação ainda está em desenvolvimento e não deve ser utilizada em produção sem revisão de segurança, testes, observabilidade e configuração de infraestrutura.

---

*NewsBot — informação personalizada, entregue no momento certo.*
