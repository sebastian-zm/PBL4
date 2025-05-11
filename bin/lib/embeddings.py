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
