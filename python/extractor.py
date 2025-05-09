# python/boe/extractor.py

import re

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
