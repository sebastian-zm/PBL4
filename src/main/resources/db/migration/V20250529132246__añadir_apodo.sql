-- Flyway migration script
-- Created: Thu May 29 01:22:46 PM UTC 2025

-- Write your SQL below this line
-- 1. Añadir la columna sin restricciones
ALTER TABLE USUARIO
ADD COLUMN apodo VARCHAR(255);

-- 2. Rellenar la columna con el valor del email
UPDATE USUARIO
SET apodo = email;

-- 3. Establecerla como NOT NULL
ALTER TABLE USUARIO
MODIFY COLUMN apodo VARCHAR(255) NOT NULL;

-- 4. Añadir la restricción UNIQUE
ALTER TABLE USUARIO
ADD CONSTRAINT unique_apodo UNIQUE (apodo);

