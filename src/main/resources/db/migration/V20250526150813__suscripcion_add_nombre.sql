-- Flyway migration script
-- Created: Mon May 26 03:08:13 PM UTC 2025

-- Write your SQL below this line
ALTER TABLE SUSCRIPCION ADD COLUMN nombre VARCHAR(255) NOT NULL DEFAULT '';
