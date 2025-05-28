
#!/usr/bin/env bash
set -e

echo "⏳ Esperando a que MySQL maestro (db) esté listo..."
for i in {1..30}; do
  mysqladmin ping -h db -u root -proot &>/dev/null && break
  sleep 1
done

echo "⚙️  Configurando usuarios y base de datos en maestro (db)..."
mysql -h db -u root -proot <<EOF
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';

CREATE DATABASE IF NOT EXISTS oposiciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'dev'@'%' IDENTIFIED WITH mysql_native_password BY 'dev';
GRANT ALL PRIVILEGES ON oposiciones.* TO 'dev'@'%';

CREATE USER IF NOT EXISTS 'replica'@'%' IDENTIFIED WITH mysql_native_password BY 'replica_pass';
GRANT REPLICATION SLAVE ON *.* TO 'replica'@'%';

FLUSH PRIVILEGES;
EOF

echo "⏳ Esperando a que MySQL esclavo (db_slave) esté listo..."
for i in {1..30}; do
  mysqladmin ping -h db_slave -u root -proot &>/dev/null && break
  sleep 1
done

echo "🔧 Ajustando método de autenticación en esclavo (db_slave)..."
mysql -h db_slave -u root -proot <<EOF
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
FLUSH PRIVILEGES;
EOF

echo "🔍 Obteniendo estado del maestro (db)..."
MASTER_STATUS=$(mysql -h db -u root -proot -e "SHOW MASTER STATUS\G")
LOG_FILE=$(echo "$MASTER_STATUS" | grep File: | awk '{print $2}')
LOG_POS=$(echo "$MASTER_STATUS" | grep Position: | awk '{print $2}')

if [[ -z "$LOG_FILE" || -z "$LOG_POS" ]]; then
  echo "❌ ERROR: No se pudo obtener el estado del maestro correctamente"
  exit 1
fi

echo "🔁 Configurando replicación en esclavo (db_slave)..."
mysql -h db_slave -u root -proot <<EOF
STOP SLAVE;
RESET SLAVE ALL;

CHANGE MASTER TO
  MASTER_HOST='db',
  MASTER_USER='replica',
  MASTER_PASSWORD='replica_pass',
  MASTER_LOG_FILE='$LOG_FILE',
  MASTER_LOG_POS=$LOG_POS,
  GET_MASTER_PUBLIC_KEY = 1;

START SLAVE;
EOF

echo "✅ Replicación maestro-esclavo configurada correctamente"
