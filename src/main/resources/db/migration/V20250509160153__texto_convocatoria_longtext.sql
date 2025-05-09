-- Flyway migration script
-- Created: Fri May  9 04:01:53 PM UTC 2025

-- Write your SQL below this line

ALTER TABLE CONVOCATORIA
MODIFY texto LONGTEXT NOT NULL;
