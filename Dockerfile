FROM maven:3.9.11-eclipse-temurin-21

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        docker.io \
        curl \
        ca-certificates \
        uidmap && \
    rm -rf /var/lib/apt/lists/*

ARG DOCKER_GID

RUN groupdel docker 2>/dev/null || true \
    && groupadd -g ${DOCKER_GID} docker

RUN usermod -aG docker root

WORKDIR /app
