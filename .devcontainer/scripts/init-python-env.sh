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

