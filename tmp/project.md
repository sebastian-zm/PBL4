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
*.class

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

/tmp
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
```
file path: `requirements.txt`
```
jupyterlab
numpy>=1.24.2
scipy>=1.10.0
matplotlib>=3.6.3
tiktoken
openai
requests
feedparser
pdfplumber
mysql-connector-python
dotenv
sentence-transformers
beautifulsoup4```
file path: `rubrica.md`
```
# Rúbrica

## RGI219 (Front-end)

**Nivel 0:**
- No cumple los estándares W3C o la validación no está documentada.
- No funciona correctamente en Chrome o Firefox, o la validación no está documentada.
- La parte front-end no tiene suficiente complejidad para demostrar que se han comprendido bien los conceptos aprendidos.

**Nivel 1:**
- Cumplir los estándares W3C:
  - Validar HTML y CSS utilizando la herramienta validadora W3C.
  - CUIDADO, no validar el código Thymeleaf, debéis validar el HTML después de que el servidor lo haya ejecutado.
  - Documentar los errores recibidos.
  - Documentar cómo se han solucionado.
- Validar que funciona con diferentes navegadores:
  - Al menos Chrome y Firefox.
  - ¿Todos los elementos funcionan en diferentes navegadores (móvil y escritorio)?
  - Escribir qué partes y cómo se han validado.

**Nivel 2:**
- Utilizar frameworks o librerías front-end:
  - (por ejemplo, Bootstrap, SemanticUI, MaterialUI, Leaflet, APIs...)
- Desarrollar una aplicación web completamente "responsive":
  - Adaptarse a diferentes pantallas.
  - Desarrollar con enfoque "Mobile First Design".
  - Documentar los logros y pruebas realizadas.

**Nivel 3:**
- Proporcionar una apariencia de alta calidad:
  - Utilizar iconos (por ejemplo, poder ver a primera vista qué se debe hacer).
  - Ser visualmente coherente y seguir una imagen de marca.
  - Sin problemas de "responsive".
  - Cargas suaves (sin golpes).
  - Limpieza de código.
  - Animaciones cuando añaden valor.
  - Personalizar los frameworks frontend para dar vuestro estilo personal (por ejemplo, ¿usáis Bootstrap directamente o le habéis hecho modificaciones?)
  - ...

## RGI218 (Back-end)

**Nivel 0:**
- No utiliza Spring framework u otro framework contrastado con el profesor.
- No utiliza ORM.
- No utiliza Thymeleaf-spring-layouts o Tiles u otra herramienta de layout contrastada con el profesor.
- La parte back-end no tiene suficiente complejidad para demostrar que se ha comprendido bien su funcionamiento.

**Nivel 1:**
- Utilizar Spring framework:
  - Spring Boot
  - Spring MVC
- Utilizar un ORM:
  - (por ejemplo, Spring JPA/Hibernate)
- Utilizar un framework de plantillas avanzado:
  - (por ejemplo, Thymeleaf-spring-layouts, Tiles...).
  - ¡CUIDADO! No es suficiente con usar include y replace.

**Nivel 2:**
- Resolver cuestiones de seguridad:
  - Almacenar contraseñas de usuarios encriptadas (por ejemplo, Bcrypt).
  - Permisos (por ejemplo, Spring Security).
  - HTTPS (por ejemplo, Let's Encrypt).
- Actualizar el contenido de la aplicación sin recargar la página:
  - (Ajax, Web Sockets)
- Gestionar adecuadamente relaciones '1 to N' y 'N to N' utilizando Hibernate.

**Nivel 3:**
- Complejidad (se evaluará la complejidad de la aplicación):
  - ¿Qué complejidad tiene la base de datos (número de tablas, relaciones, 1toN, NtoN...)?
  - ¿El código es limpio? (Dividido en paquetes, coherente en diferentes clases, funcionalidades bien distribuidas, baja complejidad de código...)
  - ¿La aplicación web solo tiene funcionalidades CRUD o tiene lógicas más complejas?
  - ¿Qué complejidad tienen los web sockets? (¿Gestiona permisos? ¿Se gestionan "salas"? ¿Se han implementado solo ejemplos que aparecen en los tutoriales o algo más significativo?)
```
file path: `src/main/java/software/sebastian/oposiciones/OposicionesApplication.java`
```
package software.sebastian.oposiciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OposicionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(OposicionesApplication.class, args);
	}

}
```
file path: `src/main/java/software/sebastian/oposiciones/config/SecurityConfig.java`
```
package software.sebastian.oposiciones.config;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import software.sebastian.oposiciones.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider ap = new DaoAuthenticationProvider();
        ap.setUserDetailsService(userDetailsService);
        ap.setPasswordEncoder(passwordEncoder());
        return ap;
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) qué rutas son públicas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/css/**", "/js/**", "/webjars/**")
                        .permitAll()
                        // 2) a /etiquetas/** sólo ADMIN
                        .requestMatchers(HttpMethod.GET, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/etiquetas/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/etiquetas/**").hasRole("ADMIN")
                        // el resto de URLs (p.ej. /suscripciones, /) autenticado (USER o ADMIN)
                        .anyRequest().authenticated())
                // 3) login/logout
                .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/etiquetas", true)
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
                // 4) CSRF (por defecto ON); vamos a usar cookie repo para poder leerlo en JS/fetch
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}
```
file path: `src/main/java/software/sebastian/oposiciones/controller/AuthController.java`
```
package software.sebastian.oposiciones.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```
file path: `src/main/java/software/sebastian/oposiciones/controller/EtiquetaController.java`
```
package software.sebastian.oposiciones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import software.sebastian.oposiciones.service.EtiquetaService;
import software.sebastian.oposiciones.service.EtiquetaService.TreeNode;

@Controller
@RequestMapping("/etiquetas")
public class EtiquetaController {

    private final EtiquetaService svc;
    public EtiquetaController(EtiquetaService svc) { this.svc = svc; }

    @GetMapping
    public String tree(Model m) {
        List<TreeNode> forest = svc.getTree();
        m.addAttribute("forest", forest);
        return "etiquetas/tree";
    }

    @PostMapping
    public String create(@RequestParam String nombre,
                         @RequestParam(required=false) String descripcion,
                         @RequestParam(required=false) Integer parentId) {
        svc.createWithParent(nombre, descripcion, parentId);
        return "redirect:/etiquetas";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Integer id,
                       @RequestParam String nombre,
                       @RequestParam(required=false) String descripcion) {
        svc.update(id, nombre, descripcion);
        return "redirect:/etiquetas";
    }

    @PostMapping("/{id}/move")
    @ResponseBody
    public Map<String,String> move(@PathVariable Integer id,
            @RequestParam(required=false) Integer parentId) {
      svc.moveSubtree(id, parentId);
      return Map.of("status","ok");
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        svc.delete(id);
        return "redirect:/etiquetas";
    }
}
```
file path: `src/main/java/software/sebastian/oposiciones/model/ArbolEtiqueta.java`
```
package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ARBOL_ETIQUETAS")
public class ArbolEtiqueta {

    @EmbeddedId
    private ArbolEtiquetaId id;

    @MapsId("ancestroId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ancestroId")
    private Etiqueta ancestro;

    @MapsId("descendienteId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "descendienteId")
    private Etiqueta descendiente;

    @Column(nullable = false)
    private Integer distancia;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ArbolEtiqueta() {}

    public ArbolEtiqueta(Etiqueta a, Etiqueta d, Integer dist) {
        this.ancestro = a;
        this.descendiente = d;
        this.distancia = dist;
        this.id = new ArbolEtiquetaId(a.getEtiquetaId(), d.getEtiquetaId());
    }

    // getters & setters
    public ArbolEtiquetaId getId() { return id; }
    public Etiqueta getAncestro() { return ancestro; }
    public Etiqueta getDescendiente() { return descendiente; }
    public Integer getDistancia() { return distancia; }
}
```
file path: `src/main/java/software/sebastian/oposiciones/model/ArbolEtiquetaId.java`
```
package software.sebastian.oposiciones.model;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class ArbolEtiquetaId implements Serializable {
    private Integer ancestroId;
    private Integer descendienteId;

    public ArbolEtiquetaId() {}
    public ArbolEtiquetaId(Integer a, Integer d) {
        this.ancestroId = a;
        this.descendienteId = d;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArbolEtiquetaId)) return false;
        ArbolEtiquetaId that = (ArbolEtiquetaId) o;
        return Objects.equals(ancestroId, that.ancestroId)
            && Objects.equals(descendienteId, that.descendienteId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(ancestroId, descendienteId);
    }
}
```
file path: `src/main/java/software/sebastian/oposiciones/model/Etiqueta.java`
```
package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ETIQUETA")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer etiquetaId;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Closure‐table links
    @OneToMany(mappedBy = "ancestro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArbolEtiqueta> descendientes = new HashSet<>();

    @OneToMany(mappedBy = "descendiente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ArbolEtiqueta> ancestros = new HashSet<>();

    // getters & setters

    public Integer getEtiquetaId() { return etiquetaId; }
    public void setEtiquetaId(Integer etiquetaId) { this.etiquetaId = etiquetaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Set<ArbolEtiqueta> getDescendientes() { return descendientes; }
    public Set<ArbolEtiqueta> getAncestros() { return ancestros; }
}
```
file path: `src/main/java/software/sebastian/oposiciones/model/Usuario.java`
```
package software.sebastian.oposiciones.model;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;

@Entity
@Table(name = "USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuarioId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    /**
     * Ejemplo de bitmask:
     *   1 → ROLE_USER
     *   2 → ROLE_ADMIN
     */
    @Column(nullable = false)
    private Integer permisos;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // getters & setters

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Integer getPermisos() { return permisos; }
    public void setPermisos(Integer permisos) { this.permisos = permisos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```
file path: `src/main/java/software/sebastian/oposiciones/repository/ArbolEtiquetaRepository.java`
```
package software.sebastian.oposiciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.ArbolEtiquetaId;

public interface ArbolEtiquetaRepository extends JpaRepository<ArbolEtiqueta, ArbolEtiquetaId> {

  /** Recupera sólo relaciones directas (distancia = 1). Una sola query. */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.distancia = 1
      """)
  List<ArbolEtiqueta> findAllDirectRelations();

  /** Recupera TODOS los descendientes directos e indirectos (ids) */
  @Query("""
        select ae.descendiente.etiquetaId
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
      """)
  List<Integer> findDescendantsIds(@Param("id") Integer ancestroId);

  /** Borra relaciones externas al subtree */
  @Modifying
  @Transactional
  @Query("""
        delete from ArbolEtiqueta ae
        where ae.descendiente.etiquetaId in :subtree
          and ae.ancestro.etiquetaId not in :subtree
      """)
  void deleteExternalAncestors(@Param("subtree") List<Integer> subtreeIds);

  /** Inserta padre→nodo directo */
  @Modifying
  @Transactional
  @Query(value = """
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES (:parentId, :nodeId, 1)
      """, nativeQuery = true)
  void insertDirectParentChild(@Param("parentId") Integer parentId,
      @Param("nodeId") Integer nodeId);

  @Modifying
  @Transactional
  @Query(value = "CALL move_subtree(:node, :parent)", nativeQuery = true)
  void bulkReparent(@Param("node") Integer nodeId, @Param("parent") Integer parentId);

  /** Todos los ancestros de un nodo dado */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.descendiente.etiquetaId = :id
      """)
  List<ArbolEtiqueta> findAncestorsOf(@Param("id") Integer descendienteId);

  /** Hijos directos (distancia=1) de un ancestro */
  @Query("""
        select ae.descendiente.etiquetaId
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
          and ae.distancia = 1
      """)
  List<Integer> findDirectChildrenIds(@Param("id") Integer ancestroId);

  /** Todas las filas donde esta etiqueta es ancestro */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
      """)
  List<ArbolEtiqueta> findRelationsByAncestor(@Param("id") Integer ancestroId);
}
```
file path: `src/main/java/software/sebastian/oposiciones/repository/EtiquetaRepository.java`
```
package software.sebastian.oposiciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> { }
```
file path: `src/main/java/software/sebastian/oposiciones/repository/UsuarioRepository.java`
```
package software.sebastian.oposiciones.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import software.sebastian.oposiciones.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
}
```
file path: `src/main/java/software/sebastian/oposiciones/service/CustomUserDetailsService.java`
```
package software.sebastian.oposiciones.service;

import java.util.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import software.sebastian.oposiciones.model.Usuario;
import software.sebastian.oposiciones.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public CustomUserDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Mapear permisos a roles
        List<GrantedAuthority> roles = new ArrayList<>();
        if ((u.getPermisos() & 1) != 0) { roles.add(new SimpleGrantedAuthority("ROLE_USER")); }
        if ((u.getPermisos() & 2) != 0) { roles.add(new SimpleGrantedAuthority("ROLE_ADMIN")); }

        return new User(u.getEmail(), u.getPasswordHash(), roles);
    }
}
```
file path: `src/main/java/software/sebastian/oposiciones/service/EtiquetaService.java`
```
package software.sebastian.oposiciones.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;


@Service
public class EtiquetaService {
    private final EtiquetaRepository etiquetaRepo;
    private final ArbolEtiquetaRepository arbolRepo;

    public EtiquetaService(EtiquetaRepository er, ArbolEtiquetaRepository ar) {
        this.etiquetaRepo = er;
        this.arbolRepo = ar;
    }

    public List<Etiqueta> findAll() {
        return etiquetaRepo.findAll();
    }

    @Transactional
    public Etiqueta create(String nombre, String descripcion) {
        Etiqueta e = new Etiqueta();
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        Etiqueta eSaved = etiquetaRepo.save(e);
        arbolRepo.save(new ArbolEtiqueta(eSaved, eSaved, 0));
        return eSaved;
    }

    @Transactional
    public Etiqueta createWithParent(String nombre, String descripcion, Integer parentId) {
        Etiqueta e = create(nombre, descripcion);
        if (parentId != null) {
            addRelation(parentId, e.getEtiquetaId());
        }
        return e;
    }

    @Transactional
    public void moveSubtree(Integer nodeId, Integer parentId) {
        arbolRepo.bulkReparent(nodeId, parentId);
    }

    public Etiqueta update(Integer id, String nombre, String descripcion) {
        Etiqueta e = etiquetaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe: " + id));
        e.setNombre(nombre);
        e.setDescripcion(descripcion);
        return etiquetaRepo.save(e);
    }

    /**
     * Elimina la etiqueta id y reparenta su sub‐árbol: - sus hijos directos pasan a colgar de su
     * padre (si existía). - si era raíz, sus hijos se convierten en raíces (sin ancestros).
     */
    @Transactional
    public void delete(Integer id) {
        Etiqueta toDelete = etiquetaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe etiqueta " + id));

        // 1) Hijos directos y todo el sub‐árbol de 'id'
        List<Integer> directChildren = arbolRepo.findDirectChildrenIds(id);

        // 2) Padre directo (si existía)
        Optional<Integer> parentOpt =
                arbolRepo.findAncestorsOf(id).stream().filter(r -> r.getDistancia() == 1)
                        .map(r -> r.getAncestro().getEtiquetaId()).findFirst();

        // 3) Si tenía padre, muevo cada hijo directamente
        // al nuevo padre (o al root si parentOpt está vacío)
        Integer parentId = parentOpt.orElse(null);
        for (Integer childId : directChildren) {
            // mover el subtree cuyo root es childId para que cuelgue de parentId
            arbolRepo.bulkReparent(childId, parentId);
        }

        // 4) Borro todas las filas de la closure‐table de / hacia 'id'
        List<ArbolEtiqueta> ancestorsRels = arbolRepo.findAncestorsOf(id);
        List<ArbolEtiqueta> descendantsRels = arbolRepo.findRelationsByAncestor(id);
        arbolRepo.deleteAll(ancestorsRels);
        arbolRepo.deleteAll(descendantsRels);

        // 5) Finalmente, borro la propia entidad
        etiquetaRepo.delete(toDelete);
    }

    public static class TreeNode {
        public Etiqueta etiqueta;
        public List<TreeNode> children = new ArrayList<>();

        public TreeNode(Etiqueta e) {
            this.etiqueta = e;
        }
    }

    /**
     * Lee todas las etiquetas y relaciones distancia=1, y monta un forest (listado de raíces
     * TreeNode).
     */
    public List<TreeNode> getTree() {
        List<Etiqueta> all = etiquetaRepo.findAll();
        List<ArbolEtiqueta> rels = arbolRepo.findAllDirectRelations();

        // Mapa etiquetaId → nodo
        Map<Integer, TreeNode> nodes =
                all.stream().collect(Collectors.toMap(Etiqueta::getEtiquetaId, TreeNode::new));

        // Conjunto de hijos (para luego identificar raíces)
        Set<Integer> childrenIds = new HashSet<>();

        // Construyo parent→children
        for (ArbolEtiqueta r : rels) {
            Integer p = r.getAncestro().getEtiquetaId();
            Integer c = r.getDescendiente().getEtiquetaId();
            TreeNode parent = nodes.get(p);
            TreeNode child = nodes.get(c);
            parent.children.add(child);
            childrenIds.add(c);
        }

        // Ordeno los hijos de cada nodo alfabéticamente por nombre
        for (TreeNode node : nodes.values()) {
            node.children.sort(Comparator.comparing(n -> n.etiqueta.getNombre()));
        }

        // Raíces = todas menos los que son hijos
        return nodes.values().stream()
                .filter(n -> !childrenIds.contains(n.etiqueta.getEtiquetaId()))
                .sorted(Comparator.comparing(o -> o.etiqueta.getNombre()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addRelation(Integer parentId, Integer childId) {
        if (parentId.equals(childId))
            throw new IllegalArgumentException("No self‐loops");
        Etiqueta p = etiquetaRepo.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Padre no existe"));
        Etiqueta c = etiquetaRepo.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("Hijo no existe"));

        // 1) inserta p→c (dist=1)
        arbolRepo.save(new ArbolEtiqueta(p, c, 1));
        // 2) para cada ancestro A de p, inserta A→c con distancia+1
        arbolRepo.findAncestorsOf(parentId).forEach(rel -> arbolRepo
                .save(new ArbolEtiqueta(rel.getAncestro(), c, rel.getDistancia() + 1)));
    }


}
```
file path: `src/main/resources/application.properties`
```
spring.application.name=Oposiciones

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# MySQL Database Connection
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:3306/oposiciones
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Properties
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true```
file path: `src/main/resources/db/migration/V20250509105603__initial_schema.sql`
```
-- Flyway migration script
-- Created: Fri 09 May 2025 10:56:03 AM UTC

-- Write your SQL below this line
-- Table: USUARIO
CREATE TABLE USUARIO (
    usuarioId INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    permisos INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: ETIQUETA
CREATE TABLE ETIQUETA (
    etiquetaId INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: ARBOL_ETIQUETAS
CREATE TABLE ARBOL_ETIQUETAS (
    ancestroId INT NOT NULL,
    descendienteId INT NOT NULL,
    distancia INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (ancestroId, descendienteId),
    FOREIGN KEY (ancestroId) REFERENCES ETIQUETA(etiquetaId),
    FOREIGN KEY (descendienteId) REFERENCES ETIQUETA(etiquetaId)
);

-- Table: CONVOCATORIA
CREATE TABLE CONVOCATORIA (
    convocatoriaId INT AUTO_INCREMENT PRIMARY KEY,
    boeId VARCHAR(255) NOT NULL UNIQUE,
    titulo VARCHAR(255) NOT NULL,
    texto TEXT NOT NULL,
    fechaPublicacion DATETIME NOT NULL,
    enlace VARCHAR(255) NOT NULL,
    datosExtra JSON,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: MODELO
CREATE TABLE MODELO (
    modeloId INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: ETIQUETADO
CREATE TABLE ETIQUETADO (
    convocatoriaId INT NOT NULL,
    etiquetaId INT NOT NULL,
    modeloId INT NOT NULL,
    valoracion INT,
    confianza FLOAT,
    status INT,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (convocatoriaId, etiquetaId, modeloId),
    FOREIGN KEY (convocatoriaId) REFERENCES CONVOCATORIA(convocatoriaId),
    FOREIGN KEY (etiquetaId) REFERENCES ETIQUETA(etiquetaId),
    FOREIGN KEY (modeloId) REFERENCES MODELO(modeloId)
);

-- Table: FEEDBACK
CREATE TABLE FEEDBACK (
    usuarioId INT NOT NULL,
    convocatoriaId INT NOT NULL,
    etiquetaId INT NOT NULL,
    aprobado BOOLEAN NOT NULL,
    fecha DATETIME NOT NULL,
    comentario VARCHAR(255),
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (usuarioId, convocatoriaId, etiquetaId),
    FOREIGN KEY (usuarioId) REFERENCES USUARIO(usuarioId),
    FOREIGN KEY (convocatoriaId, etiquetaId) REFERENCES ETIQUETADO(convocatoriaId, etiquetaId)
);

-- Table: SUSCRIPCION
CREATE TABLE SUSCRIPCION (
    suscripcionId INT AUTO_INCREMENT PRIMARY KEY,
    usuarioId INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuarioId) REFERENCES USUARIO(usuarioId)
);

-- Table: SUSCRIPCION_ETIQUETA
CREATE TABLE SUSCRIPCION_ETIQUETA (
    suscripcionId INT NOT NULL,
    etiquetaId INT NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (suscripcionId, etiquetaId),
    FOREIGN KEY (suscripcionId) REFERENCES SUSCRIPCION(suscripcionId),
    FOREIGN KEY (etiquetaId) REFERENCES ETIQUETA(etiquetaId)
);

-- Create indexes for better performance
CREATE INDEX idx_arbol_etiquetas_ancestro ON ARBOL_ETIQUETAS(ancestroId);
CREATE INDEX idx_arbol_etiquetas_descendiente ON ARBOL_ETIQUETAS(descendienteId);
CREATE INDEX idx_etiquetado_convocatoria ON ETIQUETADO(convocatoriaId);
CREATE INDEX idx_etiquetado_etiqueta ON ETIQUETADO(etiquetaId);
CREATE INDEX idx_etiquetado_modelo ON ETIQUETADO(modeloId);
CREATE INDEX idx_feedback_usuario ON FEEDBACK(usuarioId);
CREATE INDEX idx_feedback_convocatoria_etiqueta ON FEEDBACK(convocatoriaId, etiquetaId);
CREATE INDEX idx_suscripcion_usuario ON SUSCRIPCION(usuarioId);
CREATE INDEX idx_suscripcion_etiqueta_suscripcion ON SUSCRIPCION_ETIQUETA(suscripcionId);
CREATE INDEX idx_suscripcion_etiqueta_etiqueta ON SUSCRIPCION_ETIQUETA(etiquetaId);
```
file path: `src/main/resources/db/migration/V20250509160153__texto_convocatoria_longtext.sql`
```
-- Flyway migration script
-- Created: Fri May  9 04:01:53 PM UTC 2025

-- Write your SQL below this line

ALTER TABLE CONVOCATORIA
MODIFY texto LONGTEXT NOT NULL;
```
file path: `src/main/resources/db/migration/V20250510235642__move_subtree_procedure.sql`
```
-- Flyway migration script
-- Created: Sat May 10 11:56:42 PM UTC 2025

-- Write your SQL below this line
DELIMITER //
CREATE DEFINER=`dev`@`%` PROCEDURE `move_subtree`(
  IN p_node   INT,
  IN p_parent INT
)
BEGIN
   -- 1. Delete outdated paths that connect subtree descendants to old ancestors (not within subtree)
  DELETE a FROM ARBOL_ETIQUETAS AS a
  JOIN ARBOL_ETIQUETAS AS d ON a.descendienteId = d.descendienteId
  LEFT JOIN ARBOL_ETIQUETAS AS x ON x.ancestroId = d.ancestroId AND x.descendienteId = a.ancestroId
  WHERE d.ancestroId = p_node AND x.ancestroId IS NULL;

  -- 2. Insert new paths connecting all ancestors of new parent to all descendants of subtree
  INSERT INTO ARBOL_ETIQUETAS (ancestroId, descendienteId, distancia)
  SELECT supertree.ancestroId, subtree.descendienteId,
         supertree.distancia + subtree.distancia + 1
  FROM ARBOL_ETIQUETAS AS supertree
  JOIN ARBOL_ETIQUETAS AS subtree
  WHERE subtree.ancestroId = p_node
    AND supertree.descendienteId = p_parent;
END//
DELIMITER ;
```
file path: `src/main/resources/db/migration/V20250511104234__modelo_embedding.sql`
```
-- Flyway migration script
-- Created: Sun May 11 10:42:33 AM UTC 2025

-- Write your SQL below this line
CREATE TABLE MODELO_EMBEDDING (
    modeloId INT NOT NULL,
    etiquetaId INT NOT NULL,
    embedding LONGBLOB NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (modeloId, etiquetaId),
    FOREIGN KEY (modeloId) REFERENCES MODELO(modeloId),
    FOREIGN KEY (etiquetaId) REFERENCES ETIQUETA(etiquetaId)
);
```
file path: `src/main/resources/db/migration/V20250511140200__titulo_text.sql`
```
-- Flyway migration script
-- Created: Sun May 11 02:02:00 PM UTC 2025

-- Write your SQL below this line
ALTER TABLE CONVOCATORIA
  MODIFY titulo TEXT;```
file path: `src/main/resources/db/migration/V20250511201526__insertar_etiquetas_procedimiento.sql`
```
-- Flyway migration script
-- Created: Sun May 11 08:15:24 PM UTC 2025

-- Write your SQL below this line
DELIMITER $$

CREATE DEFINER=`dev`@`%` PROCEDURE insertar_etiquetas(IN p_json JSON)
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE j INT DEFAULT 0;
  DECLARE k INT DEFAULT 0;
  DECLARE n INT;
  DECLARE m INT;
  DECLARE r INT;
  DECLARE keyName VARCHAR(255);
  DECLARE subkeyName VARCHAR(255);
  DECLARE elem VARCHAR(255);
  DECLARE parentId INT;
  DECLARE childId INT;
  DECLARE grandChildId INT;
  DECLARE val JSON;
  DECLARE valType VARCHAR(10);
  DECLARE rootKeys JSON;
  DECLARE subKeys JSON;
  DECLARE subVal JSON;

  -- 1) obtenemos las claves del nivel raíz
  SET rootKeys = JSON_KEYS(p_json);
  SET n        = JSON_LENGTH(rootKeys);
  SET i        = 0;

  root_loop: LOOP
    IF i >= n THEN
      LEAVE root_loop;
    END IF;

    -- nombre de la key de primer nivel (p.ej. "Andalucía")
    SET keyName = JSON_UNQUOTE(JSON_EXTRACT(rootKeys, CONCAT('$[', i, ']')));
    SET val     = JSON_EXTRACT(p_json, CONCAT('$."', keyName, '"'));
    SET valType = JSON_TYPE(val);

    -- 2) insertamos/modificamos la etiqueta de primer nivel
    INSERT IGNORE INTO ETIQUETA(nombre) VALUES (keyName);
    SELECT etiquetaId INTO parentId
      FROM ETIQUETA WHERE nombre = keyName;

    -- reflexiva (padre → padre, distancia 0)
    INSERT IGNORE INTO ARBOL_ETIQUETAS
      (ancestroId, descendienteId, distancia)
    VALUES
      (parentId, parentId, 0);

    -- 3) si es un array de strings ⇒ hojas directas
    IF valType = 'ARRAY' THEN
      SET m = JSON_LENGTH(val);
      SET j = 0;
      array_loop: LOOP
        IF j >= m THEN
          LEAVE array_loop;
        END IF;
        SET elem = JSON_UNQUOTE(JSON_EXTRACT(val, CONCAT('$[', j, ']')));

        -- insertamos la etiqueta de provincia
        INSERT IGNORE INTO ETIQUETA(nombre) VALUES (elem);
        SELECT etiquetaId INTO childId
          FROM ETIQUETA WHERE nombre = elem;

        -- reflexiva para la provincia
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES
          (childId, childId, 0);

        -- clausura transitiva desde todos los ancestros de "parentId"
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        SELECT a.ancestroId, childId, a.distancia + 1
        FROM ARBOL_ETIQUETAS AS a
        WHERE a.descendienteId = parentId;

        SET j = j + 1;
      END LOOP array_loop;

    -- 4) si es un objeto ⇒ anidamos un nivel más
    ELSEIF valType = 'OBJECT' THEN
      SET subKeys = JSON_KEYS(val);
      SET m       = JSON_LENGTH(subKeys);
      SET j       = 0;

      object_loop: LOOP
        IF j >= m THEN
          LEAVE object_loop;
        END IF;
        SET subkeyName = JSON_UNQUOTE(JSON_EXTRACT(subKeys, CONCAT('$[', j, ']')));
        SET subVal     = JSON_EXTRACT(val, CONCAT('$."', subkeyName, '"'));

        -- insertamos la etiqueta de nivel intermedio
        INSERT IGNORE INTO ETIQUETA(nombre) VALUES (subkeyName);
        SELECT etiquetaId INTO childId
          FROM ETIQUETA WHERE nombre = subkeyName;

        -- reflexiva para este nodo
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES
          (childId, childId, 0);

        -- clausura transitiva desde ancestros de parentId
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        SELECT a.ancestroId, childId, a.distancia + 1
        FROM ARBOL_ETIQUETAS AS a
        WHERE a.descendienteId = parentId;

        -- suponemos que subVal es un array de hojas
        IF JSON_TYPE(subVal) = 'ARRAY' THEN
          SET r = JSON_LENGTH(subVal);
          SET k = 0;
          inner_array_loop: LOOP
            IF k >= r THEN
              LEAVE inner_array_loop;
            END IF;
            SET elem = JSON_UNQUOTE(JSON_EXTRACT(subVal, CONCAT('$[', k, ']')));

            INSERT IGNORE INTO ETIQUETA(nombre) VALUES (elem);
            SELECT etiquetaId INTO grandChildId
              FROM ETIQUETA WHERE nombre = elem;

            -- reflexiva para la hoja
            INSERT IGNORE INTO ARBOL_ETIQUETAS
              (ancestroId, descendienteId, distancia)
            VALUES
              (grandChildId, grandChildId, 0);

            -- clausura transitiva desde ancestros de childId
            INSERT IGNORE INTO ARBOL_ETIQUETAS
              (ancestroId, descendienteId, distancia)
            SELECT a.ancestroId, grandChildId, a.distancia + 1
            FROM ARBOL_ETIQUETAS AS a
            WHERE a.descendienteId = childId;

            SET k = k + 1;
          END LOOP inner_array_loop;
        END IF;

        SET j = j + 1;
      END LOOP object_loop;
    END IF;

    SET i = i + 1;
  END LOOP root_loop;
END$$

DELIMITER ;
```
file path: `src/main/resources/static/js/tree.js`
```
let draggedId = null;

const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
const csrfParam  = document.querySelector('meta[name="_csrf_parameterName"]').content;
const csrfToken  = document.querySelector('meta[name="_csrf"]').content;

function dragStart(e) {
  draggedId = e.currentTarget.dataset.id;
  if (draggedId) {
    e.stopPropagation();
  }
  e.dataTransfer.effectAllowed = 'move';
}

function dragOver(e) {
  e.preventDefault();
  e.dataTransfer.dropEffect = 'move';
}

function drop(e) {
  e.preventDefault();
  let targetId = e.currentTarget.dataset.id;
  if (targetId) {
    e.stopPropagation();
  }
  if (draggedId && draggedId !== targetId) {
    fetch(`/etiquetas/${draggedId}/move?parentId=${targetId}`, {
      method: 'POST',
      headers: { 
        [csrfHeader]: csrfToken 
      }
    }).then(r => {
      if (r.ok) location.reload();
    });
  }
}

// Mostrar/ocultar formularios
function showEditForm(btn) {
  let li = btn.closest('li');
  li.querySelector('.form-edit').classList.toggle('d-none');
}
function showAddChildForm(btn) {
  let li = btn.closest('li');
  li.querySelector('.form-add-child').classList.toggle('d-none');
}
function deleteNode(btn) {
  let id = btn.closest('li').dataset.id;
  if (confirm('¿Borrar etiqueta ' + id + ' y reparentar?')) {
    fetch(`/etiquetas/${id}/delete`, {
      method:'POST', 
      headers: { 
        [csrfHeader]: csrfToken 
      }
    })
      .then(r => r.redirected ? location.href = r.url : location.reload());
  }
}

// Creación de raíz
document.addEventListener('DOMContentLoaded', () => {

  document.getElementById('btnAddRoot')
    .addEventListener('click', () => {
      let name = document.getElementById('newName').value.trim();
      if (!name) return alert('Nombre vacío');
      let form = document.createElement('form');
      form.method = 'post';
      form.action = '/etiquetas';
      form.innerHTML = `
        <input type="hidden" name="${csrfParam}" value="${csrfToken}"/>
        <input name="nombre" value="${name}"/>
      `;
      document.body.appendChild(form);
      form.submit();
    });
});
```
file path: `src/main/resources/templates/etiquetas/fragments.html`
```
<!-- src/main/resources/templates/etiquetas/fragments.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
  <body>
    <!-- Fragmento que genera UN <li> completo,
         recibe por parámetro una variable llamada 'node' -->
    <li th:fragment="node(node)" 
        class="mb-1"
        th:attr="data-id=${node.etiqueta.etiquetaId}"
        draggable="true"
        ondragstart="dragStart(event)"
        ondragover="dragOver(event)"
        ondrop="drop(event)">
      
      <span class="me-2"><i class="fas fa-arrows-alt"></i></span>
      <span th:text="${node.etiqueta.nombre}">Etiqueta</span>
      <button class="btn btn-sm btn-primary ms-2" onclick="showEditForm(this)">
        <i class="fas fa-edit"></i>
      </button>
      <button class="btn btn-sm btn-danger ms-1" onclick="deleteNode(this)">
        <i class="fas fa-trash"></i>
      </button>
      <button class="btn btn-sm btn-success ms-1" onclick="showAddChildForm(this)">
        <i class="fas fa-plus"></i>
      </button>

      <!-- form editar -->
      <form class="d-none form-edit mt-1"
            th:action="@{'/etiquetas/' + ${node.etiqueta.etiquetaId} + '/edit'}"
            method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
        <div class="input-group input-group-sm">
          <input name="nombre" class="form-control" th:value="${node.etiqueta.nombre}">
          <input name="descripcion" class="form-control" th:value="${node.etiqueta.descripcion}">
          <button class="btn btn-primary btn-sm"><i class="fas fa-check"></i></button>
        </div>
      </form>

      <!-- form añadir hijo -->
      <form class="d-none form-add-child mt-1"
            th:action="@{/etiquetas}" method="post">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
        <input type="hidden" name="parentId" th:value="${node.etiqueta.etiquetaId}"/>
        <div class="input-group input-group-sm">
          <input name="nombre" class="form-control" placeholder="Nueva…">
          <button class="btn btn-success btn-sm"><i class="fas fa-check"></i></button>
        </div>
      </form>

      <!-- recursión -->
      <ul class="list-unstyled ms-4 mt-1">
        <th:block th:each="child : ${node.children}">
          <li th:replace="~{etiquetas/fragments :: node (${child})}"></li>
        </th:block>
      </ul>      
    </li>
  </body>
</html>
```
file path: `src/main/resources/templates/etiquetas/tree.html`
```
<!-- src/main/resources/templates/etiquetas/tree.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout">
<head>
  <title layout:fragment="title">Árbol de Etiquetas</title>
</head>
<body>
<section layout:fragment="content">
  <div class="d-flex mb-3">
    <input id="newName" class="form-control me-2" placeholder="Nueva etiqueta (raíz)">
    <button id="btnAddRoot" class="btn btn-success">
      <i class="fas fa-plus"></i> Añadir raíz
    </button>
  </div>

  <ul class="list-unstyled" id="treeRoot">
    <li ondragover="dragOver(event)" ondrop="drop(event)" class="mb-1" data-id="">
      <span class="me-2"><i class="fas fa-earth-europe"></i></span>
      <span>Todas las etiquetas</span>
      <ul class="list-unstyled ms-4 mt-1">
        <th:block th:each="node : ${forest}">
          <li th:replace="~{etiquetas/fragments :: node (${node})}"></li>
        </th:block>
      </ul>
  </ul>
  
</section>

<!-- Con esto Thymeleaf inyecta este bloque al final del layout -->
<th:block layout:fragment="scriptExtras">
  <script th:src="@{/js/tree.js}"></script>
</th:block>
</body>
</html>

```
file path: `src/main/resources/templates/layout.html`
```
<!DOCTYPE html>
<html lang="es"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:fragment="layout"> 
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title layout:title-pattern="$CONTENT_TITLE - Oposiciones">Oposiciones</title>

    <!-- Bootstrap CSS from WebJars -->
    <link th:href="@{/webjars/bootstrap/5.3.0/css/bootstrap.min.css}" rel="stylesheet" />

    <!-- FontAwesome CSS from WebJars -->
    <link th:href="@{/webjars/fortawesome__fontawesome-free/6.7.2/css/all.min.css}" rel="stylesheet" />

    <!-- Custom CSS -->
    <link th:href="@{/css/custom.css}" rel="stylesheet" />

    <!-- Thymeleaf will inject additional CSS/JS via fragments if defined in content pages -->
    <th:block layout:fragment="headExtras"></th:block>

    <meta th:name="_csrf"        th:content="${_csrf.token}"/>
    <meta th:name="_csrf_header"  th:content="${_csrf.headerName}"/>
    <meta th:name="_csrf_parameterName"  th:content="${_csrf.parameterName}"/>

</head>

<body>

<!-- Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary sticky-top">
    <div class="container-fluid">
        <a class="navbar-brand" th:href="@{/}"><i class="fas fa-university me-2"></i> Oposiciones</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain"
                aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                  <a class="nav-link" th:href="@{/}"><i class="fas fa-home me-1"></i> Inicio</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" th:href="@{/etiquetas}"><i class="fas fa-tags me-1"></i> Etiquetas</a>
                </li>
                <li class="nav-item">
                  <a class="nav-link" th:href="@{/suscripciones}"><i class="fas fa-bell me-1"></i> Suscripciones</a>
                </li>
                <!-- Añade más enlaces según tus rutas -->
            </ul>
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                <!-- Aquí puedes poner login, perfil, etc -->
                <li class="nav-item">
                    <a class="nav-link" th:href="@{/login}"><i class="fas fa-sign-in-alt me-1"></i> Entrar</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<!-- Main Content -->
<main class="container my-4" role="main" layout:fragment="content">
    <!-- Aquí se inyectará el contenido específico de cada página -->
</main>

<!-- Footer -->
<footer class="bg-light text-center text-muted py-3 mt-auto">
    <div class="container">
        &copy; 2025 Oposiciones
    </div>
</footer>

<!-- Bootstrap Bundle JS (includes Popper) -->
<script th:src="@{/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js}"></script>

<!-- FontAwesome JS (opcional si usas solo CSS) -->
<script th:src="@{/webjars/fortawesome__fontawesome-free/6.7.2/js/all.min.js}"></script>

<!-- Aquí puedes incluir tus scripts personalizados -->
<script th:src="@{/js/app.js}"></script>

<!-- Fragmento para scripts extra -->
<th:block layout:fragment="scriptExtras"></th:block>

</body>
</html>
```
file path: `src/main/resources/templates/login.html`
```
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="layout">
<head>
  <title layout:fragment="title">Entrar</title>
</head>
<body>
<section layout:fragment="content" class="d-flex justify-content-center align-items-center" style="height:80vh;">
  <div class="card p-4" style="min-width:300px;">
    <h4 class="card-title mb-3 text-center">Login</h4>
    <form th:action="@{/login}" method="post">
      <div class="mb-3">
        <label for="username" class="form-label">Email</label>
        <input type="text" id="username" name="username"
               class="form-control" autofocus />
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">Contraseña</label>
        <input type="password" id="password" name="password"
               class="form-control" />
      </div>
      <div th:if="${param.error}" class="alert alert-danger p-2">
        Credenciales inválidas
      </div>
      <div th:if="${param.logout}" class="alert alert-success p-2">
        Has cerrado sesión correctamente
      </div>
      <div class="d-grid">
        <button type="submit" class="btn btn-primary">Entrar</button>
      </div>
    </form>
  </div>
</section>
</body>
</html>
```
file path: `src/test/java/software/sebastian/oposiciones/OposicionesApplicationTests.java`
```
package software.sebastian.oposiciones;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OposicionesApplicationTests {

	@Test
	void contextLoads() {
	}

}
```
file path: `src/test/java/software/sebastian/oposiciones/service/EtiquetaServiceTest.java`
```
package software.sebastian.oposiciones.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;

@ExtendWith(MockitoExtension.class)
class EtiquetaServiceTest {

    @Mock
    private EtiquetaRepository etiquetaRepo;

    @Mock
    private ArbolEtiquetaRepository arbolRepo;

    @InjectMocks
    private EtiquetaService service;

    private Etiqueta toDelete;

    @BeforeEach
    void setUp() {
        toDelete = new Etiqueta();
        toDelete.setEtiquetaId(1);
    }

    @Test
    void delete_NotFound_Throws() {
        when(etiquetaRepo.findById(1)).thenReturn(Optional.empty());
        try {
            service.delete(1);
        } catch (IllegalArgumentException ex) {
            // esperado
        }
        verify(etiquetaRepo).findById(1);
        verifyNoMoreInteractions(arbolRepo, etiquetaRepo);
    }

    @Test
    void delete_RootWithoutChildren() {
        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of());
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of());
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of());
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of());

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(etiquetaRepo).findById(1);
        o.verify(arbolRepo).findDirectChildrenIds(1);
        o.verify(arbolRepo).findDescendantsIds(1);
        o.verify(arbolRepo).findAncestorsOf(1);
        o.verify(arbolRepo).findRelationsByAncestor(1);
        verify(arbolRepo, never()).deleteExternalAncestors(anyList());
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());
        o.verify(arbolRepo).deleteAll(List.of());
        o.verify(arbolRepo).deleteAll(List.of());
        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }

    @Test
    void delete_LeafWithParent_NoChildrenBelow() {
        Etiqueta parent = new Etiqueta(); parent.setEtiquetaId(10);
        ArbolEtiqueta rel = new ArbolEtiqueta(parent, toDelete, 1);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of());
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of());
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of());

        service.delete(1);

        verify(arbolRepo).findAncestorsOf(1);
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());
        verify(arbolRepo).deleteAll(List.of(rel));
        verify(arbolRepo).deleteAll(List.of());
        verify(etiquetaRepo).delete(toDelete);
    }

    @Test
    void delete_WithChildrenAndParent_ReparentCorrectly() {
        // toDelete(1) tiene padre=10 y también otro ancestro a distancia 2
        Etiqueta parent = new Etiqueta(); parent.setEtiquetaId(10);
        ArbolEtiqueta rel1 = new ArbolEtiqueta(parent, toDelete,1);
        ArbolEtiqueta rel2 = new ArbolEtiqueta(new Etiqueta() {{ setEtiquetaId(100); }}, toDelete,2);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel1, rel2));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of(2,3));
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of(2,3,4));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of(
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(2); }},1),
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(4); }},2)
        ));

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(etiquetaRepo).findById(1);
        o.verify(arbolRepo).findDirectChildrenIds(1);
        o.verify(arbolRepo).findDescendantsIds(1);
        o.verify(arbolRepo).findAncestorsOf(1);

        o.verify(arbolRepo).deleteExternalAncestors(List.of(2,3,4));

        // reparent de cada hijo
        o.verify(arbolRepo).insertDirectParentChild(10, 2);
        o.verify(arbolRepo).bulkReparent(10, 2);
        o.verify(arbolRepo).insertDirectParentChild(10, 3);
        o.verify(arbolRepo).bulkReparent(10, 3);

        // limpieza closure‐table de todas las relaciones de toDelete
        o.verify(arbolRepo).findRelationsByAncestor(1);
        // primero borra ancestros (rel1, rel2)
        o.verify(arbolRepo).deleteAll(argThat((Iterable<ArbolEtiqueta> list) -> {
            int count = 0;
            for (ArbolEtiqueta ae : list) {
                if (ae.getDescendiente().getEtiquetaId().equals(1)) count++;
            }
            return count == 2;
        }));
        // luego borra descendientes directos e indirectos
        o.verify(arbolRepo).deleteAll(argThat((Iterable<ArbolEtiqueta> list) -> {
            // comprobamos que la lista no esté vacía
            return list.iterator().hasNext();
        }));

        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }

    @Test
    void delete_RootWithChildren_NoParentSoChildrenBecomeRoots() {
        // toDelete no tiene ancestro distancia=1, pero sí uno a distancia=2
        ArbolEtiqueta rel = new ArbolEtiqueta(new Etiqueta() {{ setEtiquetaId(50); }}, toDelete,2);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of(5));
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of(5,6));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of(
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(5); }},1)
        ));

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(arbolRepo).deleteExternalAncestors(List.of(5,6));

        // al no haber padre directo, no se llama a reparent
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());

        // borrado closure‐table
        o.verify(arbolRepo).deleteAll(List.of(rel));
        o.verify(arbolRepo).deleteAll(anyIterable());

        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }
}
```
