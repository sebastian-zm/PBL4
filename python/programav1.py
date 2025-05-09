import requests
import feedparser
import pdfplumber
import json
import re
import urllib.parse
from datetime import datetime
from pathlib import Path
import mysql.connector
import csv

from extractor import extract_fields

# ----------------------------
# CONFIGURACIÓN
# ----------------------------
RSS_URL = "https://www.boe.es/rss/canal_per.php?l=p&c=140"
PDF_DIR = Path("python/boe/pdfs")
PDF_DIR.mkdir(parents=True, exist_ok=True)
LOG_CSV = Path("python/boe/log_fallos.csv")

# Configuración de la base de datos MySQL
db_config = {
    "host": "db",
    "user": "dev",
    "password": "dev",
    "database": "oposiciones"
}

# ----------------------------
# FUNCIONES AUXILIARES
# ----------------------------

def extraer_fecha_publicacion(text):
    """
    Extrae la fecha de publicación del texto del BOE en formato YYYY-MM-DD.
    """
    match = re.search(r"\n([A-ZÁÉÍÓÚÑ][^,]+), (\d{1,2}) de (\w+) de (\d{4})", text)
    if match:
        dia, mes_texto, anio = match.group(2), match.group(3).lower(), match.group(4)
        meses = {
            "enero": "01", "febrero": "02", "marzo": "03", "abril": "04",
            "mayo": "05", "junio": "06", "julio": "07", "agosto": "08",
            "septiembre": "09", "octubre": "10", "noviembre": "11", "diciembre": "12"
        }
        mes_num = meses.get(mes_texto, "01")
        return f"{anio}-{mes_num.zfill(2)}-{dia.zfill(2)}"
    return None


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


def descargar_txt(link, boe_id):
    """
    Intenta descargar el TXT con la fecha dada.
    """
    res = requests.get(link)
    texto = res.text if res.status_code == 200 else ""
    if res.status_code == 200 and "boe" in texto.lower() and "disposición" in texto.lower():
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

    for entry in feed.entries[0:50]:
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

        # 3) Descargar y leer PDF
        texto = ""
        texto = descargar_txt(entry.link, boe_id)

        # 4) Si falta contenido, fallback PDF
        # if not texto.strip():
        #     pdf_path = descargar_pdf(pdf_url)
        #     if pdf_path:
        #         texto = leer_pdf(pdf_path)

        if not texto.strip():
            print(f"⚠️ {boe_id}: sin contenido legible en PDF ni TXT.")
            log_error(boe_id, "Sin PDF ni TXT disponible")
            continue

        # 5) Extraer campos y fecha de publicación
        campos = extract_fields(texto)
        fecha_publi = extraer_fecha_publicacion(texto) or datetime.today().strftime("%Y-%m-%d")

        # 6) Preparar datos para DB
        titulo = f"{campos['órgano']}: {campos['denominación']}".strip(": ")
        datos_extra = {
            "órgano": campos['órgano'],
            "plazas": campos['número de plazas'],
            "localidad": campos['localidad/provincia'],
            "plazo": campos['plazo de solicitud'],
            "tipo": campos['tipo'],
            "warnings": campos['warnings']
        }

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
