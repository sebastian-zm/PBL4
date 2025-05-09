from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
import mysql.connector
import os
import re
from dotenv import load_dotenv
from pathlib import Path

# Cargar variables de entorno si las usas
load_dotenv()

# Configuración DB
db_config = {
    "host": os.getenv("DB_HOST", "db"),
    "user": os.getenv("DB_USER", "dev"),
    "password": os.getenv("DB_PASSWORD", "dev"),
    "database": os.getenv("DB_NAME", "oposiciones"),
    "charset": "utf8mb4"
}

# Inicializar modelo de embeddings
model = SentenceTransformer('all-MiniLM-L6-v2')

def retrieve_location_chunks(text, query=",  \\d+ de", chunk_size=500, top_k=1):
    """
    Fragmenta el texto, indexa embeddings y busca fragmentos relevantes
    según la query, devolviendo los 'top_k' fragmentos.
    """
    # Dividir en fragmentos
    chunks = [text[i:i+chunk_size] for i in range(0, len(text), chunk_size)]
    if not chunks:
        return []

    # Obtener embeddings
    embeddings = model.encode(chunks, convert_to_numpy=True, show_progress_bar=False)

    # Crear índice FAISS
    d = embeddings.shape[1]
    index = faiss.IndexFlatL2(d)
    index.add(embeddings)

    # Embedding de la consulta
    query_emb = model.encode([query], convert_to_numpy=True)

    # Buscar
    _, indices = index.search(query_emb, k=min(top_k, len(chunks)))
    return [chunks[idx] for idx in indices[0]]

def extract_location(text):
    """
    Primero intenta extraer 'Ciudad' de la línea de firma con regex.
    Si falla, usa embeddings para obtener un fragmento y extraer antes de la coma.
    """
    # Regex: línea que empieza con NombreLugar, dd de mes de yyyy
    match = re.search(r"^([A-ZÁÉÍÓÚÑ][^,]+), \d{1,2} de \w+ de \d{4}", text, flags=re.MULTILINE)
    if match:
        return match.group(1).strip()

    # Fallback embeddings
    frags = retrieve_location_chunks(text)
    if frags:
        frag = frags[0]
        # Tomar todo hasta la primera coma
        return frag.split(",")[0].strip()
    return None

def main():
    # Conectar a la base de datos
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT convocatoriaId, texto FROM CONVOCATORIA;")

    print("=== Ubicaciones extraídas ===")
    for row in cursor.fetchall():
        cid = row["convocatoriaId"]
        texto = row["texto"] or ""
        if len(texto) < 100:
            print(f"[ID {cid}] Texto insuficiente")
            continue

        lugar = extract_location(texto)
        print(f"[ID {cid}] -> {lugar or 'No encontrado'}")

    cursor.close()
    conn.close()

if __name__ == "__main__":
    main()
