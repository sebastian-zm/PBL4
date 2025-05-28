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
# Comprobar que se pasa el nombre del archivo zip
if [ -z "$1" ]; then
  echo "Uso: $0 archivo.zip"
  exit 1
fi

zipfile="$1"

# Leer nombres de archivos desde stdin y añadirlos al zip
# Elimina el archivo zip previo si existe para no acumular
rm -f "$zipfile"

# Crear zip vacío para comenzar
zip -r "$zipfile" /dev/null

# Recorremos la lista y sacamos path + contenido
collect_files | while IFS= read -r line; do
  if [ -n "$line" ]; then
    zip -ur "$zipfile" "$line"
  fi
done
done