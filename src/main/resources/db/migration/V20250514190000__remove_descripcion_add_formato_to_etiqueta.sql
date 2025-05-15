-- Flyway migration script
-- Created: Wed May 14 2025 19:00:00

-- Remove the "descripcion" column from ETIQUETA and add a new "formato" column
ALTER TABLE ETIQUETA
    DROP COLUMN descripcion,
    ADD COLUMN formato VARCHAR(255)
        NULL
        COMMENT 'Formato para el nombre de la etiqueta en embeddings';
