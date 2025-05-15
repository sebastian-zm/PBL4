-- Flyway migration script
-- Created: Wed May 15 00:00:00 UTC 2025

-- 1. Añadir columnas para tabla polimórfica
ALTER TABLE MODELO_EMBEDDING
    ADD COLUMN entidadId INT NOT NULL DEFAULT 0,
    ADD COLUMN entidadTipo VARCHAR(50) NOT NULL DEFAULT '';

-- 2. Copiar valores de etiquetaId a entidadId y poner entidadTipo='ETIQUETA'
UPDATE MODELO_EMBEDDING
SET entidadId = etiquetaId,
    entidadTipo = 'ETIQUETA';

-- 3. Quitar foreign key a ETIQUETA si existiera (no se detectó en consulta)
-- (Si hay foreign key explícita, descomenta y ajusta el nombre)
-- ALTER TABLE MODELO_EMBEDDING DROP FOREIGN KEY fk_modelo_embedding_etiqueta;

-- 4. Eliminar columna etiquetaId
ALTER TABLE MODELO_EMBEDDING
    DROP COLUMN etiquetaId;

-- 5. Modificar clave primaria para incluir entidadId y entidadTipo
ALTER TABLE MODELO_EMBEDDING
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (modeloId, entidadId, entidadTipo);
