# Kanban API

## Tech Stack

**Core:** Java 21 + Spring Boot 3.5 

**Database:** Postgres

**CI:** GitHub Actions

**Tests:** JUnit5, Mockito, Testcontainers

**Containers:** Docker with `docker-compose`, (optional) `kool.dev`

## Features

## Run Locally

Clone the project

```bash
  git clone https://github.com/i-defranca/kanban-facilit
```

Go to the project directory

```bash
  cd kanban-facilit
```

Copy environment file:

```bash
  cp .env.example .env
```

Build and start container:

```bash
  docker-compose up --build
```

Stop the container:
```bash
  docker-compose down
```

Check container status:
```bash
  docker-compose ps
```

## Running Tests

To run tests, run the following command

```bash
  mvn clean verify
```
## Roadmap

## Badges

[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)
