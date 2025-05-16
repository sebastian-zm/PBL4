file path: `.devcontainer/Dockerfile`
```
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

```
file path: `.devcontainer/devcontainer.json`
```
{
  "name": "Oposiciones-ML-DevContainer",
  "dockerComposeFile": ["docker-compose.yml"],
  "service": "app",
  "workspaceFolder": "/workspace",
  "shutdownAction": "stopCompose",
  "customizations": {
    "vscode": {
      "settings": {
        "terminal.integrated.shell.linux": "/bin/bash",
        "maven.executable.path": "/workspace/mvnw",
        // Path to the Python interpreter in the virtual environment.
        "python.defaultInterpreterPath": "${containerEnv:HOME}/.venv/bin/python"
      },
      "extensions": [
        "redhat.java",
        "vscjava.vscode-java-pack",
        "vscjava.vscode-spring-boot",
        "vscjava.vscode-maven",
        "ms-python.python",
        "ms-toolsai.jupyter",
        "cweijan.vscode-database-client",
        "formulahendry.vscode-mysql",
        "eamodio.gitlens",
        "esbenp.prettier-vscode",
        "dbaeumer.vscode-eslint",
        "ms-azuretools.vscode-docker",
        "mechatroner.rainbow-csv"
      ]
    }
  },
  "forwardPorts": [3306, 8080, 3000, 8888],
  "postCreateCommand": ".devcontainer/scripts/init-python-env.sh && .devcontainer/scripts/init-db.sh && echo \"✔ Entorno inicializado\"",
  "mounts": [
    "source=m2-cache,target=/home/vscode/.m2,type=volume",
    "source=npm-cache,target=/home/vscode/.npm,type=volume",
    "source=venv-cache,target=/home/vscode/.venv,type=volume"
  ]
}
```
file path: `.devcontainer/docker-compose.yml`
```
version: '3.8'

services:
  app:
    build:
      context: ..
      dockerfile: .devcontainer/Dockerfile
    image: localhost/pbl4_devcontainer-app
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: oposiciones
      MYSQL_USER: dev
      MYSQL_PASSWORD: dev
      MYSQL_HOST: db
    volumes:
      - ..:/workspace:cached,Z
      - venv-cache:/home/ubuntu/.venv:Z
      - m2-cache:/home/ubuntu/.m2:Z
      - npm-cache:/home/ubuntu/.npm:Z
    ports:
      - "8080:8080"
      - "3000:3000"
      - "8888:8888"
    depends_on:
      - db

  db:
    image: docker.io/mysql:8.0
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: oposiciones
      MYSQL_USER: dev
      MYSQL_PASSWORD: dev
    ports:
      - "3306:3306"
    volumes:
      - db-data:/var/lib/mysql:Z

volumes:
  db-data:
  m2-cache:
  npm-cache:
  venv-cache:

```
file path: `.devcontainer/scripts/init-db.sh`
```
#!/usr/bin/env bash
set -e

echo "=> Esperando a que MySQL esté listo..."
for i in {1..30}; do
  mysqladmin ping -h db -u root -p"$MYSQL_ROOT_PASSWORD" &>/dev/null && break
  sleep 1
done

echo "=> Configurando usuarios y base de datos"
mysql -u root -p"$MYSQL_ROOT_PASSWORD" << EOF
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '$MYSQL_ROOT_PASSWORD';
CREATE DATABASE IF NOT EXISTS oposiciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'dev'@'%' IDENTIFIED BY '$MYSQL_PASSWORD';
GRANT ALL PRIVILEGES ON oposiciones.* TO 'dev'@'%';
FLUSH PRIVILEGES;
EOF

echo "✅ MySQL preparado: root/root, dev/dev"

```
file path: `.devcontainer/scripts/init-python-env.sh`
```
#!/usr/bin/env bash
set -e

# Creamos el venv dentro de $HOME para no pisar NTFS-mounted workspace
VENV_DIR="$HOME/.venv"

echo "=> Creando virtualenv en $VENV_DIR"
python3 -m venv "$VENV_DIR"
# shellcheck disable=SC1091
source "$VENV_DIR/bin/activate"

echo "=> Instalando dependencias Python"
pip install --upgrade pip
if [ -f /workspace/requirements.txt ]; then
  pip install -r /workspace/requirements.txt
else
  pip install jupyterlab pandas scikit-learn matplotlib seaborn openai
fi

echo "✅ Entorno Python listo"

```
file path: `.gitattributes`
```
/mvnw text eol=lf
*.cmd text eol=crlf
```
file path: `.gitignore`
```
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/

### Python ###
__pycache__
/pdfs

/.env
```
file path: `.mvn/wrapper/maven-wrapper.properties`
```
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
wrapperVersion=3.3.2
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
```
file path: `.vscode/settings.json`
```
{
    "java.compile.nullAnalysis.mode": "automatic"
}```
file path: `bin/asignar_etiquetas_local`
```
#!/usr/bin/env python3

from sentence_transformers import SentenceTransformer
import sys
import mysql.connector
import numpy as np
from lib.db_config import db_config
from lib.embeddings import get_modelo_id, similaridad_coseno, cargar_convocatoria, insertar_actualizar_etiquetado, cargar_embeddings_etiquetas

# Nombre del modelo de embedding en tu tabla MODELO
NOMBRE_MODELO_EMBEDDING = 'all-MiniLM-L6-v2'  # Ejemplo rápido y pequeño, puedes usar otro

UMBRAL_SIMILITUD = 0.26

model = SentenceTransformer(NOMBRE_MODELO_EMBEDDING)

def main():
    if len(sys.argv) < 2:
        print("Uso: python script.py <convocatoriaId>")
        sys.exit(1)

    convocatoriaId = sys.argv[1]

    try:
        conn = mysql.connector.connect(**db_config)
        modeloId = get_modelo_id(conn, NOMBRE_MODELO_EMBEDDING)
        etiquetas, embeddings_etiquetas = cargar_embeddings_etiquetas(conn, modeloId)

        if len(etiquetas) == 0:
            print(f"No hay embeddings de etiquetas para el modelo {NOMBRE_MODELO_EMBEDDING}")
            sys.exit(1)

        convocatoria = cargar_convocatoria(conn, convocatoriaId)
        texto_concat = (convocatoria['titulo'] or '') + ' ' + (convocatoria['texto'] or '')
        emb_conv = model.encode(texto_concat, convert_to_numpy=True)

        sims = similaridad_coseno(emb_conv, embeddings_etiquetas)
        print(sims, etiquetas)
        indices_relevantes = np.where(sims >= UMBRAL_SIMILITUD)[0]

        if len(indices_relevantes) == 0:
            # No se encontraron etiquetas relevantes
            sys.exit(1)

        for idx in indices_relevantes:
            etiquetaId = etiquetas[idx]
            confianza = float(sims[idx])
            insertar_actualizar_etiquetado(conn, convocatoriaId, etiquetaId, modeloId, confianza)

        conn.commit()
        sys.exit(0)

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

    finally:
        try:
            conn.close()
        except:
            pass

if __name__ == '__main__':
    main()
```
file path: `bin/asignar_etiquetas_openai`
```
#!/usr/bin/env python3

import sys
import os
import mysql.connector
import numpy as np
import openai
import tiktoken
from lib.db_config import db_config
from lib.embeddings import get_modelo_id, similaridad_coseno, cargar_convocatoria, insertar_actualizar_etiquetado, cargar_embeddings_etiquetas

# Nombre del modelo de embedding en tu tabla MODELO
NOMBRE_MODELO_EMBEDDING = 'text-embedding-3-large'  # Ejemplo rápido y pequeño, puedes usar otro

UMBRAL_SIMILITUD = 0.4

openai_client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))


def truncate_text_to_str(text, max_tokens=8191):
    encoding = tiktoken.encoding_for_model(NOMBRE_MODELO_EMBEDDING)
    tokens = encoding.encode(text)[:max_tokens]
    truncated_text = encoding.decode(tokens)
    return truncated_text

def get_openai_embedding(text, model):
    response = openai_client.embeddings.create(
        input=text,
        model=model
    )
    return np.array(response.data[0].embedding, dtype=np.float32)

def main():
    if len(sys.argv) < 2:
        print("Uso: python script.py <convocatoriaId>")
        sys.exit(1)

    convocatoriaId = sys.argv[1]

    try:
        conn = mysql.connector.connect(**db_config)
        modeloId = get_modelo_id(conn, NOMBRE_MODELO_EMBEDDING)
        etiquetas, embeddings_etiquetas = cargar_embeddings_etiquetas(conn, modeloId)

        if len(etiquetas) == 0:
            print(f"No hay embeddings de etiquetas para el modelo {NOMBRE_MODELO_EMBEDDING}")
            sys.exit(1)

        convocatoria = cargar_convocatoria(conn, convocatoriaId)
        texto_concat = (convocatoria['titulo'] or '') + ' ' + (convocatoria['texto'] or '')

        texto_truncado = truncate_text_to_str(texto_concat, max_tokens=8190)
        emb_conv = get_openai_embedding(texto_truncado, model=NOMBRE_MODELO_EMBEDDING)

        sims = similaridad_coseno(emb_conv, embeddings_etiquetas)
        print(sims, etiquetas)
        indices_relevantes = np.where(sims >= UMBRAL_SIMILITUD)[0]

        if len(indices_relevantes) == 0:
            # No se encontraron etiquetas relevantes
            sys.exit(1)

        for idx in indices_relevantes:
            etiquetaId = etiquetas[idx]
            confianza = float(sims[idx])
            insertar_actualizar_etiquetado(conn, convocatoriaId, etiquetaId, modeloId, confianza)

        conn.commit()
        sys.exit(0)

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)

    finally:
        try:
            conn.close()
        except:
            pass

if __name__ == '__main__':
    main()
```
file path: `bin/create-migration.sh`
```
#!/bin/bash

# Check if a description is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <description>"
    echo "Example: $0 create_users_table"
    exit 1
fi

# Parse the description from arguments
description=$(echo $1 | tr ' ' '_')

# Generate timestamp in format V{yyyyMMddHHmmss}__
timestamp=$(date +V%Y%m%d%H%M%S)

# Create the filename
filename="${timestamp}__${description}.sql"

# Path to migration directory
migration_dir="src/main/resources/db/migration"

# Create the directory if it doesn't exist
mkdir -p $migration_dir

# Create the migration file
file_path="$migration_dir/$filename"
touch $file_path

echo "-- Flyway migration script" > $file_path
echo "-- Created: $(date)" >> $file_path
echo "" >> $file_path
echo "-- Write your SQL below this line" >> $file_path

echo "Migration file created: $file_path"

# Open the file in the default editor if available
if command -v code &> /dev/null; then
    code $file_path
elif [ -n "$EDITOR" ]; then
    $EDITOR $file_path
fi```
file path: `bin/generar_convocatorias`
```
#!/usr/bin/env python3

import requests
import feedparser
import pdfplumber
import json
import re
from datetime import datetime
from pathlib import Path
import mysql.connector
import csv
from bs4 import BeautifulSoup
from lib.db_config import db_config

# ----------------------------
# CONFIGURACIÓN
# ----------------------------
RSS_URL = "https://www.boe.es/rss/canal_per.php?l=p&c=140"
PDF_DIR = Path("pdfs")
PDF_DIR.mkdir(parents=True, exist_ok=True)
LOG_CSV = Path("python/boe/log_fallos.csv")

# ------------------------------------------------
# 1) Clasificación y mapeo de números
# ------------------------------------------------

def classify_convocation(text):
    tl = text.lower()
    if any(k in tl for k in ["ayuntamiento de", "diputación", "cabildo", "consell insular", "mancomunidad", "administración local"]):
        return "municipal"
    elif any(k in tl for k in ["universidad de", "consorcio", "empresa pública"]):
        return "otros"
    elif any(k in tl for k in ["ministerio de", "subsecretaría", "secretaría de estado", "dirección general", "guardia civil", "policía nacional", "cuerpo de"]):
        return "estatal"
    else:
        return "otros"

num_map = {
    "cero": 0, "uno": 1, "una": 1, "dos": 2, "tres": 3, "cuatro": 4, "cinco": 5,
    # … completa hasta donde necesites …
    "cien": 100
}

def word_to_number(w):
    return num_map.get(w, None)

# ------------------------------------------------
# 2) Función principal de extracción
# ------------------------------------------------

def extract_fields(text):
    tipo = classify_convocation(text)
    organo = plazas = denominacion = localidad = plazo = ""
    warnings = []

    if tipo == "municipal":
        # … tu regex para municipal …
        pass  # reemplaza con tu implementación
    elif tipo == "estatal":
        # … tu regex para estatal …
        pass
    else:
        # … tu regex para otros …
        pass

    return {
        "tipo": tipo,
        "órgano": organo,
        "número de plazas": plazas,
        "denominación": denominacion,
        "localidad/provincia": localidad,
        "plazo de solicitud": plazo,
        "warnings": warnings
    }

# ----------------------------
# FUNCIONES AUXILIARES
# ----------------------------

def extraer_texto_div_textoxslt(texto):
    """
   Extrae el texto dentro del div con id="textoxslt".
    Retorna el texto plano extraído o cadena vacía si no se encuentra o error.
    """
    try:
        soup = BeautifulSoup(texto, "html.parser")
        div = soup.find("div", id="textoxslt")
        if div:
            return div.get_text(separator="\n", strip=True)
        else:
            print(f"⚠️ No se encontró el div #textoxslt en texto.")
            return ""
    except Exception as e:
        print(f"⚠️ Error analizando texto: {e}")
        return ""


def descargar_pdf(url):
    """
    Descarga un PDF desde la URL si es un PDF válido.
    """
    nombre_pdf = url.split("/")[-1]
    path = PDF_DIR / nombre_pdf
    if not path.exists():
        print(f"⬇️ Intentando descargar PDF: {nombre_pdf}")
        res = requests.get(url)
        if res.status_code == 200 and res.content.startswith(b"%PDF"):
            with open(path, "wb") as f:
                f.write(res.content)
        else:
            print(f"⚠️ PDF no disponible o inválido: {url}")
            return None
    return path


def leer_pdf(path):
    """
    Lee un PDF con pdfplumber y devuelve su texto.
    """
    try:
        with pdfplumber.open(path) as pdf:
            return "\n".join(page.extract_text() or "" for page in pdf.pages)
    except Exception as e:
        print(f"⚠️ Error leyendo PDF {path.name}: {e}")
        return ""


def descargar_html(link, boe_id):
    """
    Intenta descargar el HTML con la fecha dada.
    """
    res = requests.get(link)
    texto = res.text if res.status_code == 200 else ""
    if res.status_code == 200 and "boe" in texto.lower() and "disposición" in texto.lower():
        texto = extraer_texto_div_textoxslt(texto)
        if texto.strip():
            print(f"📄 Usando TXT para {boe_id}")
            return texto
    print(f"❌  TXT vacío o no válido para {boe_id}")
    return ""


def log_error(boe_id, motivo):
    """
    Registra errores en un CSV para revisarlos posteriormente.
    """
    if not LOG_CSV.exists():
        with open(LOG_CSV, mode="w", newline="", encoding="utf-8") as f:
            writer = csv.writer(f)
            writer.writerow(["boeId", "motivo", "timestamp"] )
    with open(LOG_CSV, mode="a", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)
        writer.writerow([boe_id, motivo, datetime.now().isoformat()])

# ----------------------------
# PROCESO PRINCIPAL
# ----------------------------

def main():
    print("📡 Leyendo feed RSS del BOE...")
    feed = feedparser.parse(RSS_URL)

    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    for entry in feed.entries[0:20]:
        # 1) Extraer BOE ID desde entry.link (txt.php?id=...)
        m = re.search(r"id=(BOE-A-\d{4}-\d+)", entry.link)
        boe_id = m.group(1) if m else None

        if not boe_id:
            print(f"⚠️ No se pudo obtener el BOE ID para {entry.link}")
            log_error("N/A", "BOE ID no extraído")
            continue

        # 2) Usar GUID para el PDF y pubDate para la fecha
        pdf_url = entry.guid
        fecha = entry.published_parsed

        # 3) Descargar y leer HTML
        texto = ""
        texto = descargar_html(entry.link, boe_id)

        # 4) Si falta contenido, fallback PDF
        if not texto.strip():
            pdf_path = descargar_pdf(pdf_url)
            if pdf_path:
                texto = leer_pdf(pdf_path)

        if not texto.strip():
            print(f"⚠️ {boe_id}: sin contenido legible en PDF ni HTML.")
            log_error(boe_id, "Sin PDF ni HTML disponible")
            continue

        # 5) Extraer campos y fecha de publicación
        campos = extract_fields(texto)
        fecha_publi = datetime(*fecha[:6]).strftime("%Y-%m-%d %H:%M:%S") if fecha else datetime.now().strftime("%Y-%m-%d %H:%M:%S")

        # 6) Preparar datos para DB
        titulo = entry.title
        datos_extra = {
            "órgano": campos['órgano'],
            "plazas": campos['número de plazas'],
            "localidad": campos['localidad/provincia'],
            "plazo": campos['plazo de solicitud'],
            "tipo": campos['tipo'],
            "warnings": campos['warnings']
        }
        for key in list(datos_extra):
            if not datos_extra[key]:
                del datos_extra[key]
            
            

        sql = """
        INSERT INTO CONVOCATORIA
        (boeId, titulo, texto, fechaPublicacion, enlace, datosExtra, createdAt, updatedAt)
        VALUES (%s, %s, %s, %s, %s, %s, NOW(), NOW())
        ON DUPLICATE KEY UPDATE
            titulo = VALUES(titulo),
            texto = VALUES(texto),
            fechaPublicacion = VALUES(fechaPublicacion),
            enlace = VALUES(enlace),
            datosExtra = VALUES(datosExtra),
            updatedAt = NOW()
        """

        cursor.execute(sql, (
            boe_id,
            titulo,
            texto,
            fecha_publi,
            entry.link,
            json.dumps(datos_extra, ensure_ascii=False)
        ))

        print(f"✅ {boe_id} insertado correctamente.")

    conn.commit()
    cursor.close()
    conn.close()

if __name__ == "__main__":
    main()
```
file path: `bin/generar_embeddings_local`
```
#!/usr/bin/env python3

import mysql.connector
from sentence_transformers import SentenceTransformer
from lib.embeddings import get_modelo_id, obtener_etiquetas_y_embeddings, guardar_embedding
from lib.db_config import db_config


# Nombre del modelo de embedding en tu tabla MODELO
NOMBRE_MODELO_EMBEDDING = 'all-MiniLM-L6-v2'  # Ejemplo rápido y pequeño, puedes usar otro

# Cargar modelo local una vez
model = SentenceTransformer(NOMBRE_MODELO_EMBEDDING)

def generar_embedding_local(texto):
    # Devuelve un numpy array de floats
    return model.encode(texto, convert_to_numpy=True)

def main():
    conn = mysql.connector.connect(**db_config)
    modeloId = get_modelo_id(conn, NOMBRE_MODELO_EMBEDDING)
    etiquetas = obtener_etiquetas_y_embeddings(conn, modeloId)

    actualizados = 0
    saltados = 0

    for e in etiquetas:
        etiqueta_updated = e['etiquetaUpdatedAt']
        embedding_updated = e['embeddingUpdatedAt']

        actualizar = False
        if embedding_updated is None:
            actualizar = True
        else:
            if etiqueta_updated > embedding_updated:
                actualizar = True

        if actualizar:
            texto = (e['nombre'] or '') + ' ' + (e['descripcion'] or '')
            print(f"Generando embedding para etiqueta {e['etiquetaId']}: {texto[:50]}...")
            try:
                embedding = generar_embedding_local(f"{texto}")
                guardar_embedding(conn, modeloId, e['etiquetaId'], embedding)
                actualizados += 1
            except Exception as ex:
                print(f"Error generando/guardando embedding para etiquetaId {e['etiquetaId']}: {ex}")
        else:
            saltados += 1

    print(f"Embeddings actualizados: {actualizados}")
    print(f"Embeddings saltados (actualizados y al día): {saltados}")

    conn.close()

if __name__ == '__main__':
    main()
```
file path: `bin/generar_embeddings_openai`
```
#!/usr/bin/env python3

import os
import openai
import numpy as np
import mysql.connector
from lib.embeddings import get_modelo_id, obtener_etiquetas_y_embeddings, guardar_embedding
from lib.db_config import db_config

# Configura la API Key de OpenAI
openai_client = openai.OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# Nombre del modelo en tabla MODELO para este embedding
NOMBRE_MODELO_EMBEDDING = "text-embedding-3-large"

def generar_embedding_openai(texto):
    response = openai_client.embeddings.create(
        input=texto,
        model=NOMBRE_MODELO_EMBEDDING
    )
    return np.array(response.data[0].embedding)

def main():
    conn = mysql.connector.connect(**db_config)
    modeloId = get_modelo_id(conn, NOMBRE_MODELO_EMBEDDING)
    etiquetas = obtener_etiquetas_y_embeddings(conn, modeloId)

    actualizados = 0
    saltados = 0

    for e in etiquetas:
        etiqueta_updated = e['etiquetaUpdatedAt']
        embedding_updated = e['embeddingUpdatedAt']
        actualizar = False
        if embedding_updated is None or etiqueta_updated > embedding_updated:
            actualizar = True

        if actualizar:
            texto = (e['nombre'] or '') + ' ' + (e['descripcion'] or '')
            print(f"Generando embedding para etiqueta {e['etiquetaId']}: {texto[:50]}...")
            try:
                embedding = generar_embedding_openai(texto)
                guardar_embedding(conn, modeloId, e['etiquetaId'], embedding)
                actualizados += 1
            except Exception as ex:
                print(f"Error generando/guardando embedding para etiquetaId {e['etiquetaId']}: {ex}")
        else:
            saltados += 1

    print(f"Embeddings actualizados: {actualizados}")
    print(f"Embeddings saltados (actualizados y al día): {saltados}")
    conn.close()

if __name__ == '__main__':
    main()
```
file path: `bin/git-to-llm.sh`
```
#!/usr/bin/env bash
set -euo pipefail

# Ruta a tu git compilado, si quieres forzar uno en particular:
GIT=${GIT:-git}

# Devuelve al final el listado de archivos (uno por línea), respetando .gitignore
collect_files() {
  # 1) Si estamos dentro de un repo Git, lista directamente
  if $GIT -C . rev-parse --is-inside-work-tree &>/dev/null; then
    $GIT ls-files --cached --others --exclude-standard
    return
  fi

  # 2) Si hay .gitignore pero NO hay .git: creamos un índice temporal
  if [ -f .gitignore ]; then
    tmpidx=$(mktemp -d)
    export GIT_DIR="$tmpidx"
    # Git necesita GIT_WORK_TREE o -C . para saber dónde está el código:
    export GIT_WORK_TREE="$(pwd)"

    # Inicializamos y añadimos TODO (obligamos a rastrear para después
    # aplicar --exclude-standard que respeta .gitignore)
    $GIT init -q
    # Añadimos todos los ficheros (pero el --exclude-standard de ls-files
    # filtrará los que estén en .gitignore)
    $GIT add -f -A &>/dev/null

    # Listamos
    $GIT ls-files --cached --others --exclude-standard

    # Limpiamos
    rm -rf "$tmpidx"
    return
  fi

  # 3) Ningún .git ni .gitignore
  echo "Error: ni repositorio Git ni .gitignore encontrado." >&2
  exit 1
}

# Recorremos la lista y sacamos path + contenido
collect_files | while IFS= read -r file; do
  printf 'file path: `%s`\n' "$file"
  echo '```'
  cat "$file"
  echo '```'
done

```
file path: `bin/lib/__pycache__/db_config.cpython-312.pyc`
```
�
    ��!h�   �                   �   � d dddd�Z y)�db�dev�oposiciones)�host�user�password�databaseN)�	db_config� �    �/workspace/bin/lib/db_config.py�<module>r      s   �� ����	�	r   ```
file path: `bin/lib/db_config.py`
```
# Configuración de la base de datos MySQL
db_config = {
    "host": "db",
    "user": "dev",
    "password": "dev",
    "database": "oposiciones"
}```
file path: `bin/lib/embeddings.py`
```
import json
import numpy as np

def get_modelo_id(conn, nombre):
    cursor = conn.cursor()
    cursor.execute("SELECT modeloId FROM MODELO WHERE nombre = %s", (nombre,))
    row = cursor.fetchone()
    cursor.close()
    if row:
        return row[0]
    else:
        raise ValueError(f"No se encontró modelo con nombre '{nombre}'")

def obtener_etiquetas_y_embeddings(conn, modeloId):
    cursor = conn.cursor(dictionary=True)
    query = """
        SELECT e.etiquetaId, e.nombre, e.descripcion, e.updatedAt AS etiquetaUpdatedAt,
               me.embedding, me.updatedAt AS embeddingUpdatedAt
        FROM ETIQUETA e
        LEFT JOIN MODELO_EMBEDDING me ON me.etiquetaId = e.etiquetaId AND me.modeloId = %s
    """
    cursor.execute(query, (modeloId,))
    resultados = cursor.fetchall()
    cursor.close()
    return resultados

def guardar_embedding(conn, modeloId, etiquetaId, embedding):
    cursor = conn.cursor()
    # Convertir numpy array a lista para JSON serialization
    embedding_list = embedding.tolist()
    embedding_json = json.dumps(embedding_list)

    query = """
    INSERT INTO MODELO_EMBEDDING (modeloId, etiquetaId, embedding)
    VALUES (%s, %s, %s)
    ON DUPLICATE KEY UPDATE embedding = %s
    """
    cursor.execute(query, (modeloId, etiquetaId, embedding_json, embedding_json))
    conn.commit()
    cursor.close()

def get_modelo_id(conn, nombre):
    cursor = conn.cursor()
    cursor.execute("SELECT modeloId FROM MODELO WHERE nombre = %s", (nombre,))
    row = cursor.fetchone()
    cursor.close()
    if row:
        return row[0]
    else:
        raise ValueError(f"No se encontró modelo con nombre '{nombre}'")

def cargar_embeddings_etiquetas(conn, modeloId):
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT etiquetaId, embedding
        FROM MODELO_EMBEDDING
        WHERE modeloId = %s
    """, (modeloId,))
    filas = cursor.fetchall()
    cursor.close()

    etiquetas = []
    embeddings = []
    for fila in filas:
        etiquetas.append(fila['etiquetaId'])
        vec = json.loads(fila['embedding'])
        embeddings.append(np.array(vec, dtype=np.float32))
    embeddings = np.vstack(embeddings) if embeddings else np.array([])
    return etiquetas, embeddings

def cargar_convocatoria(conn, convocatoriaId):
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT convocatoriaId, titulo, texto
        FROM CONVOCATORIA
        WHERE convocatoriaId = %s
    """, (convocatoriaId,))
    fila = cursor.fetchone()
    cursor.close()
    if fila is None:
        raise ValueError(f"No existe convocatoria con convocatoriaId = {convocatoriaId}")
    return fila

def similaridad_coseno(v1, matriz):
    v1_norm = v1 / np.linalg.norm(v1)
    matriz_norm = matriz / np.linalg.norm(matriz, axis=1, keepdims=True)
    return np.dot(matriz_norm, v1_norm)

def insertar_actualizar_etiquetado(conn, convocatoriaId, etiquetaId, modeloId, confianza):
    cursor = conn.cursor()
    query = """
    INSERT INTO ETIQUETADO (convocatoriaId, etiquetaId, modeloId, confianza, createdAt, updatedAt)
    VALUES (%s, %s, %s, %s, NOW(), NOW())
    ON DUPLICATE KEY UPDATE confianza = %s, updatedAt = NOW()
    """
    cursor.execute(query, (convocatoriaId, etiquetaId, modeloId, confianza, confianza))
    cursor.close()
```
file path: `mvnw`
```
#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup batch script, version 3.3.2
#
# Optional ENV vars
# -----------------
#   JAVA_HOME - location of a JDK home dir, required when download maven via java source
#   MVNW_REPOURL - repo url base for downloading maven distribution
#   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
#   MVNW_VERBOSE - true: enable verbose log; debug: trace the mvnw script; others: silence the output
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

# OS specific support.
native_path() { printf %s\\n "$1"; }
case "$(uname)" in
CYGWIN* | MINGW*)
  [ -z "${JAVA_HOME-}" ] || JAVA_HOME="$(cygpath --unix "$JAVA_HOME")"
  native_path() { cygpath --path --windows "$1"; }
  ;;
esac

# set JAVACMD and JAVACCMD
set_java_home() {
  # For Cygwin and MinGW, ensure paths are in Unix format before anything is touched
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
      JAVACCMD="$JAVA_HOME/jre/sh/javac"
    else
      JAVACMD="$JAVA_HOME/bin/java"
      JAVACCMD="$JAVA_HOME/bin/javac"

      if [ ! -x "$JAVACMD" ] || [ ! -x "$JAVACCMD" ]; then
        echo "The JAVA_HOME environment variable is not defined correctly, so mvnw cannot run." >&2
        echo "JAVA_HOME is set to \"$JAVA_HOME\", but \"\$JAVA_HOME/bin/java\" or \"\$JAVA_HOME/bin/javac\" does not exist." >&2
        return 1
      fi
    fi
  else
    JAVACMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v java
    )" || :
    JAVACCMD="$(
      'set' +e
      'unset' -f command 2>/dev/null
      'command' -v javac
    )" || :

    if [ ! -x "${JAVACMD-}" ] || [ ! -x "${JAVACCMD-}" ]; then
      echo "The java/javac command does not exist in PATH nor is JAVA_HOME set, so mvnw cannot run." >&2
      return 1
    fi
  fi
}

# hash string like Java String::hashCode
hash_string() {
  str="${1:-}" h=0
  while [ -n "$str" ]; do
    char="${str%"${str#?}"}"
    h=$(((h * 31 + $(LC_CTYPE=C printf %d "'$char")) % 4294967296))
    str="${str#?}"
  done
  printf %x\\n $h
}

verbose() { :; }
[ "${MVNW_VERBOSE-}" != true ] || verbose() { printf %s\\n "${1-}"; }

die() {
  printf %s\\n "$1" >&2
  exit 1
}

trim() {
  # MWRAPPER-139:
  #   Trims trailing and leading whitespace, carriage returns, tabs, and linefeeds.
  #   Needed for removing poorly interpreted newline sequences when running in more
  #   exotic environments such as mingw bash on Windows.
  printf "%s" "${1}" | tr -d '[:space:]'
}

# parse distributionUrl and optional distributionSha256Sum, requires .mvn/wrapper/maven-wrapper.properties
while IFS="=" read -r key value; do
  case "${key-}" in
  distributionUrl) distributionUrl=$(trim "${value-}") ;;
  distributionSha256Sum) distributionSha256Sum=$(trim "${value-}") ;;
  esac
done <"${0%/*}/.mvn/wrapper/maven-wrapper.properties"
[ -n "${distributionUrl-}" ] || die "cannot read distributionUrl property in ${0%/*}/.mvn/wrapper/maven-wrapper.properties"

case "${distributionUrl##*/}" in
maven-mvnd-*bin.*)
  MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/
  case "${PROCESSOR_ARCHITECTURE-}${PROCESSOR_ARCHITEW6432-}:$(uname -a)" in
  *AMD64:CYGWIN* | *AMD64:MINGW*) distributionPlatform=windows-amd64 ;;
  :Darwin*x86_64) distributionPlatform=darwin-amd64 ;;
  :Darwin*arm64) distributionPlatform=darwin-aarch64 ;;
  :Linux*x86_64*) distributionPlatform=linux-amd64 ;;
  *)
    echo "Cannot detect native platform for mvnd on $(uname)-$(uname -m), use pure java version" >&2
    distributionPlatform=linux-amd64
    ;;
  esac
  distributionUrl="${distributionUrl%-bin.*}-$distributionPlatform.zip"
  ;;
maven-mvnd-*) MVN_CMD=mvnd.sh _MVNW_REPO_PATTERN=/maven/mvnd/ ;;
*) MVN_CMD="mvn${0##*/mvnw}" _MVNW_REPO_PATTERN=/org/apache/maven/ ;;
esac

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
[ -z "${MVNW_REPOURL-}" ] || distributionUrl="$MVNW_REPOURL$_MVNW_REPO_PATTERN${distributionUrl#*"$_MVNW_REPO_PATTERN"}"
distributionUrlName="${distributionUrl##*/}"
distributionUrlNameMain="${distributionUrlName%.*}"
distributionUrlNameMain="${distributionUrlNameMain%-bin}"
MAVEN_USER_HOME="${MAVEN_USER_HOME:-${HOME}/.m2}"
MAVEN_HOME="${MAVEN_USER_HOME}/wrapper/dists/${distributionUrlNameMain-}/$(hash_string "$distributionUrl")"

exec_maven() {
  unset MVNW_VERBOSE MVNW_USERNAME MVNW_PASSWORD MVNW_REPOURL || :
  exec "$MAVEN_HOME/bin/$MVN_CMD" "$@" || die "cannot exec $MAVEN_HOME/bin/$MVN_CMD"
}

if [ -d "$MAVEN_HOME" ]; then
  verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  exec_maven "$@"
fi

case "${distributionUrl-}" in
*?-bin.zip | *?maven-mvnd-?*-?*.zip) ;;
*) die "distributionUrl is not valid, must match *-bin.zip or maven-mvnd-*.zip, but found '${distributionUrl-}'" ;;
esac

# prepare tmp dir
if TMP_DOWNLOAD_DIR="$(mktemp -d)" && [ -d "$TMP_DOWNLOAD_DIR" ]; then
  clean() { rm -rf -- "$TMP_DOWNLOAD_DIR"; }
  trap clean HUP INT TERM EXIT
else
  die "cannot create temp dir"
fi

mkdir -p -- "${MAVEN_HOME%/*}"

# Download and Install Apache Maven
verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
verbose "Downloading from: $distributionUrl"
verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

# select .zip or .tar.gz
if ! command -v unzip >/dev/null; then
  distributionUrl="${distributionUrl%.zip}.tar.gz"
  distributionUrlName="${distributionUrl##*/}"
fi

# verbose opt
__MVNW_QUIET_WGET=--quiet __MVNW_QUIET_CURL=--silent __MVNW_QUIET_UNZIP=-q __MVNW_QUIET_TAR=''
[ "${MVNW_VERBOSE-}" != true ] || __MVNW_QUIET_WGET='' __MVNW_QUIET_CURL='' __MVNW_QUIET_UNZIP='' __MVNW_QUIET_TAR=v

# normalize http auth
case "${MVNW_PASSWORD:+has-password}" in
'') MVNW_USERNAME='' MVNW_PASSWORD='' ;;
has-password) [ -n "${MVNW_USERNAME-}" ] || MVNW_USERNAME='' MVNW_PASSWORD='' ;;
esac

if [ -z "${MVNW_USERNAME-}" ] && command -v wget >/dev/null; then
  verbose "Found wget ... using wget"
  wget ${__MVNW_QUIET_WGET:+"$__MVNW_QUIET_WGET"} "$distributionUrl" -O "$TMP_DOWNLOAD_DIR/$distributionUrlName" || die "wget: Failed to fetch $distributionUrl"
elif [ -z "${MVNW_USERNAME-}" ] && command -v curl >/dev/null; then
  verbose "Found curl ... using curl"
  curl ${__MVNW_QUIET_CURL:+"$__MVNW_QUIET_CURL"} -f -L -o "$TMP_DOWNLOAD_DIR/$distributionUrlName" "$distributionUrl" || die "curl: Failed to fetch $distributionUrl"
elif set_java_home; then
  verbose "Falling back to use Java to download"
  javaSource="$TMP_DOWNLOAD_DIR/Downloader.java"
  targetZip="$TMP_DOWNLOAD_DIR/$distributionUrlName"
  cat >"$javaSource" <<-END
	public class Downloader extends java.net.Authenticator
	{
	  protected java.net.PasswordAuthentication getPasswordAuthentication()
	  {
	    return new java.net.PasswordAuthentication( System.getenv( "MVNW_USERNAME" ), System.getenv( "MVNW_PASSWORD" ).toCharArray() );
	  }
	  public static void main( String[] args ) throws Exception
	  {
	    setDefault( new Downloader() );
	    java.nio.file.Files.copy( java.net.URI.create( args[0] ).toURL().openStream(), java.nio.file.Paths.get( args[1] ).toAbsolutePath().normalize() );
	  }
	}
	END
  # For Cygwin/MinGW, switch paths to Windows format before running javac and java
  verbose " - Compiling Downloader.java ..."
  "$(native_path "$JAVACCMD")" "$(native_path "$javaSource")" || die "Failed to compile Downloader.java"
  verbose " - Running Downloader.java ..."
  "$(native_path "$JAVACMD")" -cp "$(native_path "$TMP_DOWNLOAD_DIR")" Downloader "$distributionUrl" "$(native_path "$targetZip")"
fi

# If specified, validate the SHA-256 sum of the Maven distribution zip file
if [ -n "${distributionSha256Sum-}" ]; then
  distributionSha256Result=false
  if [ "$MVN_CMD" = mvnd.sh ]; then
    echo "Checksum validation is not supported for maven-mvnd." >&2
    echo "Please disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  elif command -v sha256sum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | sha256sum -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  elif command -v shasum >/dev/null; then
    if echo "$distributionSha256Sum  $TMP_DOWNLOAD_DIR/$distributionUrlName" | shasum -a 256 -c >/dev/null 2>&1; then
      distributionSha256Result=true
    fi
  else
    echo "Checksum validation was requested but neither 'sha256sum' or 'shasum' are available." >&2
    echo "Please install either command, or disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties." >&2
    exit 1
  fi
  if [ $distributionSha256Result = false ]; then
    echo "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised." >&2
    echo "If you updated your Maven version, you need to update the specified distributionSha256Sum property." >&2
    exit 1
  fi
fi

# unzip and move
if command -v unzip >/dev/null; then
  unzip ${__MVNW_QUIET_UNZIP:+"$__MVNW_QUIET_UNZIP"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -d "$TMP_DOWNLOAD_DIR" || die "failed to unzip"
else
  tar xzf${__MVNW_QUIET_TAR:+"$__MVNW_QUIET_TAR"} "$TMP_DOWNLOAD_DIR/$distributionUrlName" -C "$TMP_DOWNLOAD_DIR" || die "failed to untar"
fi
printf %s\\n "$distributionUrl" >"$TMP_DOWNLOAD_DIR/$distributionUrlNameMain/mvnw.url"
mv -- "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" "$MAVEN_HOME" || [ -d "$MAVEN_HOME" ] || die "fail to move MAVEN_HOME"

clean || :
exec_maven "$@"
```
file path: `mvnw.cmd`
```
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__% %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace '^.*'+$MVNW_REPO_PATTERN,'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''
$MAVEN_HOME_PARENT = "$HOME/.m2/wrapper/dists/$distributionUrlNameMain"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_HOME_PARENT = "$env:MAVEN_USER_HOME/wrapper/dists/$distributionUrlNameMain"
}
$MAVEN_HOME_NAME = ([System.Security.Cryptography.MD5]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
```
file path: `pom.xml`
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.4.5</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>software.sebastian.oposiciones</groupId>
	<artifactId>oposiciones</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>Oposiciones</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.thymeleaf.extras</groupId>
			<artifactId>thymeleaf-extras-springsecurity6</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.webjars</groupId>
	    	<artifactId>bootstrap</artifactId>
			<version>5.3.0</version>
		</dependency>
		<dependency>
			<groupId>org.webjars.npm</groupId>
			<artifactId>fortawesome__fontawesome-free</artifactId>
			<version>6.7.2</version>
		</dependency>
		<dependency>
			<groupId>org.webjars.npm</groupId>
			<artifactId>hotwired__stimulus</artifactId>
			<version>3.2.2</version>
		</dependency>
		<dependency>
			<groupId>org.webjars.npm</groupId>
			<artifactId>hotwired__turbo</artifactId>
			<version>8.0.13</version>
		</dependency>
		<dependency>
    		<groupId>nz.net.ultraq.thymeleaf</groupId>
    		<artifactId>thymeleaf-layout-dialect</artifactId>
		</dependency>
		<dependency>
			<groupId>org.flywaydb</groupId>
			<artifactId>flyway-core</artifactId>
		</dependency>
		<dependency>  
			<groupId>org.flywaydb</groupId>  
			<artifactId>flyway-mysql</artifactId>  
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
			<plugin>
				<groupId>org.flywaydb</groupId>
				<artifactId>flyway-maven-plugin</artifactId>
				<version>10.20.1</version>
				<configuration>
					<url>jdbc:mysql://${MYSQL_HOST}:3306/oposiciones</url>
					<user>${MYSQL_USER}</user>
					<password>${MYSQL_PASSWORD}</password>
					<locations>classpath:db/migration</locations>
					<baselineOnMigrate>true</baselineOnMigrate>
					<validateOnMigrate>true</validateOnMigrate>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```
file path: `project.md`
```
