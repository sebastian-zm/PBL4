-- Flyway migration script
-- Created: Sun May 11 10:42:33 AM UTC 2025

-- Write your SQL below this line
CREATE TABLE MODELO_EMBEDDING (
    modeloId INT NOT NULL,
    etiquetaId INT NOT NULL,
    embedding LONGBLOB NOT NULL,
    createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (modeloId, etiquetaId),
    FOREIGN KEY (modeloId) REFERENCES MODELO(modeloId),
    FOREIGN KEY (etiquetaId) REFERENCES ETIQUETA(etiquetaId)
);
