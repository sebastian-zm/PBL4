FROM registry.gitlab.com/mu-bd-ce/devcontainers/java

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      curl git unzip nodejs npm \
      python3 python3-venv python3-pip \
      default-mysql-client && \
    rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# Copiamos y damos permiso a los scripts
COPY .devcontainer/scripts /scripts
RUN chmod +x /scripts/*.sh

USER ubuntu

WORKDIR /workspace

CMD ["sleep", "infinity"]

