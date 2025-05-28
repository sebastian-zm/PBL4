

# Imagen base Java con herramientas útiles para desarrollo
FROM registry.gitlab.com/mu-bd-ce/devcontainers/java

# Evita prompts interactivos al instalar paquetes
ENV DEBIAN_FRONTEND=noninteractive

# Instalación de utilidades necesarias
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      curl git unzip nodejs npm \
      python3 python3-venv python3-pip \
      default-mysql-client \
      rsync && \
    rm -rf /var/lib/apt/lists/*

# Variables de entorno para Java
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=$JAVA_HOME/bin:$PATH

# Copia de scripts útiles
COPY .devcontainer/scripts /scripts
RUN chmod +x /scripts/*.sh

# Usuario no root (ya viene creado en la imagen base)
USER ubuntu

# Directorio de trabajo por defecto
WORKDIR /workspace

# Mantiene el contenedor activo (útil en devcontainers)
CMD ["sleep", "infinity"]
