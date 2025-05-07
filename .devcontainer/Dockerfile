FROM mcr.microsoft.com/devcontainers/java:0-17

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      curl git unzip nodejs npm \
      python3 python3-venv python3-pip && \
    rm -rf /var/lib/apt/lists/*

# Sólo pip base; el resto se instala en el venv en postCreate
RUN pip3 install --no-cache-dir --upgrade pip

ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# Copiamos y damos permiso a los scripts
COPY scripts /scripts
RUN chmod +x /scripts/*.sh

USER vscode
WORKDIR /workspace

