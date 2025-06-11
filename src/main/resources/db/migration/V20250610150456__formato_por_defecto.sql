-- Flyway migration script
-- Created: Tue Jun 10 03:04:56 PM UTC 2025

-- Write your SQL below this line

ALTER TABLE ETIQUETA ALTER COLUMN formato SET DEFAULT "Convocatorias de oposiciones y anuncios relevantes a %s";