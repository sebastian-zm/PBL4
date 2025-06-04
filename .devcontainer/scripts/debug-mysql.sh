#!/usr/bin/env bash
# debug-mysql.sh - Script para diagnosticar problemas con MySQL en devcontainer

echo "\U0001f50d DIAGN�STICO DE MYSQL"
echo "======================"

# Verificar que Docker Compose est� funcionando
echo "\U0001f4cb Estado de los servicios..."
docker-compose ps

echo ""
echo "\U0001f4ca Logs del servicio db (�ltimas 50 l�neas):"
echo "============================================"
docker-compose logs --tail=50 db

echo ""
echo "\U0001f4ca Logs del servicio db_slave (�ltimas 30 l�neas):"
echo "=================================================="
docker-compose logs --tail=30 db_slave

echo ""
echo "\U0001f50c Verificando conectividad de red..."
echo "====================================="

# Test de conectividad b�sica
echo "Ping a db desde app:"
docker-compose exec app ping -c 3 db || echo "\u274c No se puede hacer ping a db"

echo ""
echo "Ping a db_slave desde app:"
docker-compose exec app ping -c 3 db_slave || echo "\u274c No se puede hacer ping a db_slave"

echo ""
echo "\U0001f6aa Verificando puertos..."
echo "========================"

# Verificar que los puertos est�n abiertos
echo "Puerto 3306 en db:"
docker-compose exec app nc -z db 3306 && echo "\u2705 Puerto 3306 abierto" || echo "\u274c Puerto 3306 cerrado"

echo "Puerto 3306 en db_slave:"
docker-compose exec app nc -z db_slave 3306 && echo "\u2705 Puerto 3306 abierto" || echo "\u274c Puerto 3306 cerrado"

echo ""
echo "\U0001f510 Verificando MySQL directamente..."
echo "==================================="

# Intentar conectar directamente a MySQL
echo "Conectando a db:"
docker-compose exec db mysql -u root -proot -e "SELECT 'MySQL Master OK' as status;" 2>/dev/null && echo "\u2705 MySQL Master accesible" || echo "\u274c MySQL Master no accesible"

echo "Conectando a db_slave:"
docker-compose exec db_slave mysql -u root -proot -e "SELECT 'MySQL Slave OK' as status;" 2>/dev/null && echo "\u2705 MySQL Slave accesible" || echo "\u274c MySQL Slave no accesible"

echo ""
echo "\U0001f4be Verificando vol�menes..."
echo "=========================="

# Verificar el estado de los vol�menes
docker volume ls | grep pbl4

echo ""
echo "\U0001f527 Informaci�n del sistema..."
echo "============================="

# Informaci�n del sistema
echo "Memoria disponible:"
free -h

echo ""
echo "Espacio en disco:"
df -h

echo ""
echo "Procesos de MySQL en db:"
docker-compose exec db ps aux | grep mysql || echo "No hay procesos MySQL visibles"

echo ""
echo "Procesos de MySQL en db_slave:"
docker-compose exec db_slave ps aux | grep mysql || echo "No hay procesos MySQL visibles"

echo ""
echo "\U0001f3c1 Diagn�stico completado"