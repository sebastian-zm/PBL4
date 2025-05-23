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

