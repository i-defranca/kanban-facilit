# Kanban API

## Tech Stack

**Core:** Java 21 + Spring Boot 3.5 

**Database:** Postgres

**CI:** GitHub Actions

**Tests:** JUnit5, Mockito, Testcontainers

**Containers:** Docker with `docker-compose`, (optional) `kool.dev`

## Features

- CRUD endpoints for Project, Member and Department
- Project listing by status and project status update 

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

Mirror your docker group into the container (to run testcontainers from within docker):
```bash
  echo "DOCKER_GID=$(stat -c "%g" /var/run/docker.sock)" >> .env
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

- API docs
- Metrics endpoints
- GraphQL support
- Pagination support
- Authenticate routes - scope by project members
- Status transition tests
- Optimize status transition rules
- Project members handling

## Badges

[![License: Unlicense](https://img.shields.io/badge/license-Unlicense-blue.svg)](http://unlicense.org/)
[![Coverage](https://codecov.io/gh/i-defranca/kanban-facilit/branch/main/graph/badge.svg)](https://codecov.io/gh/i-defranca/kanban-facilit)
