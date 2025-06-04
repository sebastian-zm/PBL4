#!/usr/bin/env bash
# reset-environment.sh - Limpia y reinicia todo el entorno

set -e

echo "\U0001f504 RESETEANDO ENTORNO DEVCONTAINER"
echo "=================================="

# Confirmar que el usuario quiere proceder
read -p "\u26a0\ufe0f  Esto eliminar� todos los vol�menes y datos. �Continuar? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Operaci�n cancelada"
    exit 1
fi

echo ""
echo "\U0001f6d1 Parando todos los contenedores..."
docker-compose down --volumes --remove-orphans

echo ""
echo "\U0001f9f9 Eliminando vol�menes..."
docker volume rm pbl4-db-data pbl4-db-slave-data pbl4-m2-cache pbl4-npm-cache pbl4-venv-cache pbl4-java-src-main pbl4-java-src-test pbl4-java-target 2>/dev/null || echo "Algunos vol�menes ya estaban eliminados"

echo ""
echo "\U0001f9fd Limpiando im�genes..."
docker-compose build --no-cache

echo ""
echo "\U0001f3d7\ufe0f  Recreando contenedores..."
docker-compose up -d --force-recreate

echo ""
echo "\u23f3 Esperando a que los servicios est�n listos..."
sleep 30

echo ""
echo "\U0001f50d Verificando estado..."
docker-compose ps

echo ""
echo "\u2705 Entorno reseteado. Los servicios deber�an estar inici�ndose."
echo "   Usa 'docker-compose logs -f' para ver los logs en tiempo real."