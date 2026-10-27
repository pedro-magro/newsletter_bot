# Repository Guidelines

## Project Overview

This repository contains an educational Spring Boot application built with Maven and Java 21. The project is a Telegram newsletter bot. Its current implementation is an early-stage modular monolith: it models the main domain, communicates with the Telegram Bot API through long polling, and prepares the persistence layer, but it does not yet provide the complete news-collection and newsletter-delivery flow.

The application entry point is `TelegramBotApplication` under `src/main/java/com/newsletterbot/telegram_bot/`. Runtime configuration belongs in `src/main/resources/application.properties`. Tests are under `src/test/java/` and use the same base package.

## Project Structure

Production code is organized by domain and technical responsibility:

```text
src/main/java/com/newsletterbot/telegram_bot/
├── news/                    # News entity and repository
├── newsletter/              # Newsletter entity and repository
├── provider/                # News-provider entity and repository
├── telegram/                # Telegram API client and polling/update services
├── topic/                   # Assunto entity and repository
├── user/                    # Usuario entity and repository
└── TelegramBotApplication.java
```

Database migrations are stored in `src/main/resources/db/migration/`. The current migration is `V1_create_db.sql`.

Important existing components include:

- `TelegramClient`: calls `getUpdates`, `sendMessage`, `answerCallbackQuery` and sends the inline topic menu;
- `TelegramPollingService`: receives Telegram updates using long polling;
- `TelegramUpdateService`: handles `/start` and topic callbacks;
- JPA entities: `Usuario`, `Assunto`, `Noticia`, `Provedor` and `Newsletter`;
- Spring Data repositories for the current entities.

Do not create duplicate domain classes or packages. Before adding a class, inspect the closest existing domain package and reuse its model, repository or service when appropriate.

Generated output under `target/` must not be committed.

## Current Implementation Status

The following functionality exists in some form:

- Spring Boot application and Maven build configuration;
- initial JPA model and repositories;
- initial Flyway migration;
- PostgreSQL configuration;
- Telegram HTTP client using Spring `RestClient`;
- `/start` handling;
- inline buttons for Tecnologia, Cibersegurança and Economia;
- temporary in-memory topic preferences;
- Telegram long polling service.

The following functionality is not complete yet:

- durable persistence of Telegram users and selected topics;
- complete validation of JPA mappings against the migration;
- collection from external news providers;
- GNews or NewsData.io integration;
- newsletter generation and scheduled delivery;
- RabbitMQ, Redis and Docker Compose;
- production deployment and observability;
- comprehensive unit and integration tests.

Treat the project as experimental. Do not describe planned functionality as implemented, and do not assume that a successful compilation means the Telegram-to-database flow is complete.

## Build, Test and Run Commands

Use the Maven Wrapper so contributors use the project-defined Maven version:

```text
./mvnw clean verify       # clean, compile, run tests and package
./mvnw test               # run the test suite
./mvnw spring-boot:run    # start the application locally
```

On Windows, use the equivalent commands with `mvnw.cmd`, for example:

```text
mvnw.cmd clean verify
mvnw.cmd test
mvnw.cmd spring-boot:run
```

Before claiming that a change works, run the narrowest relevant check and report its result. For larger changes, run `mvnw clean verify`.

If Maven fails before compilation because the wrapper, local Maven repository or network is unavailable, distinguish that environment failure from Java or Spring errors. Do not infer that the source compiles from stale files in `target/`.

## Local Configuration

The application expects PostgreSQL and environment-specific settings. Keep secrets in a local `.env` file or environment variables; never add tokens, passwords or production database settings to source files or commits.

Expected settings include:

```text
DB_URLL
DB_USER
DB_PASSWORD
TELEGRAM_BOT_TOKEN
```

The `.env` file is ignored by `.gitignore`, but an ignored file can still be tracked if it was added previously. Check `git status` and the index before committing. If `.env` is tracked, stop and review it for credentials before making any commit.

The application uses PostgreSQL, Flyway and `spring.jpa.hibernate.ddl-auto=validate`. The database schema must therefore exist and match the entities and migrations; Hibernate is not expected to create the schema automatically.

## Coding Style and Naming

Follow standard Java conventions:

- packages use lowercase names;
- classes and interfaces use `PascalCase`;
- methods and fields use `camelCase`;
- repository interfaces normally end with `Repository`;
- use descriptive singular domain names such as `Usuario`, `Assunto`, `Noticia`, `Provedor` and `Newsletter`;
- place new classes in the narrowest relevant domain package;
- use four spaces for newly written Java indentation, unless preserving the surrounding file is safer.

Existing Java files contain tabs in some blocks. Do not reformat unrelated files solely to change indentation. Keep formatting consistent with the file being edited and avoid broad mechanical formatting changes.

The existing `UsuarioRepository` repository name does not follow the `Repository` suffix convention. Treat it as legacy code; prefer `UsuarioRepository` or a more descriptive `...Repository` name for new work and do not introduce additional names with this inconsistency.

Maven XML currently uses tabs. Preserve the existing style when editing `pom.xml`.

## Spring, JPA and Telegram Guidelines

When changing a JPA entity, inspect the corresponding migration and related entities together. Check identifiers, column names, relationship cardinality, join tables, nullability, uniqueness and generated values.

When changing Telegram handling:

- preserve the separation between `TelegramClient` and update-processing services;
- do not put database or business rules inside the HTTP client;
- validate update types and callback data;
- preserve the long-polling offset behavior;
- do not log bot tokens or sensitive user data;
- remember that current topic preferences are in memory and are lost on restart.

Scheduling and polling configuration must be validated against the actual Spring configuration before changing it. Do not assume that a property alone enables scheduled execution.

## Testing Guidelines

Tests use JUnit 5 and Spring Boot test support. Name test classes with a `Tests` suffix and use descriptive method names such as `contextLoads` or `createsTopic`.

The current suite is minimal. Add focused tests for new behavior, especially for:

- Telegram update parsing and callback handling;
- polling offset and failure behavior;
- repository queries;
- JPA mappings and Flyway compatibility;
- configuration-dependent startup.

Use slice tests where suitable and integration tests with real PostgreSQL/Testcontainers when database behavior is important. Do not claim integration coverage unless the required infrastructure is actually exercised.

## Docker and Infrastructure

The Dockerfile is still under development. Docker builds depend on the Maven build passing first. Do not use Docker as a substitute for fixing compilation or application-context errors.

PostgreSQL may be run locally or in Docker during development. RabbitMQ, Redis, Docker Compose and provider integrations are planned, not currently available as completed infrastructure.

## Git, Commits and Pull Requests

This checkout currently has no usable commit history, so repository-specific commit conventions cannot be inferred. Use concise, imperative commit subjects, for example:

```text
Add topic persistence test
```

Before committing:

1. inspect `git status`;
2. ensure `.env`, tokens, passwords and generated output are not staged;
3. run the relevant Maven checks;
4. review the diff for accidental formatting or unrelated changes.

Pull requests should explain the change, identify affected domains, describe database migrations or configuration changes, and include the commands and results used for validation. Screenshots are only relevant for user-facing interface changes.

## Security and Configuration

- never commit `.env`, credentials, bot tokens or production configuration;
- use environment variables for secrets;
- do not expose secrets in logs, exceptions or test fixtures;
- validate data received from external APIs;
- review Flyway migrations and JPA changes together;
- use uniqueness constraints and idempotent processing where duplicate Telegram updates or news items are possible;
- do not add external integrations or infrastructure credentials without documenting their local configuration.
