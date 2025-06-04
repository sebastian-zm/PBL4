-- Flyway migration script
-- Created: Wed May 21 03:52:23 PM UTC 2025

-- Write your SQL below this line
CREATE TABLE HILO (
    hiloId INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    contenido TEXT NOT NULL,
    creadorId INT NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creadorId) REFERENCES USUARIO(usuarioId)
);

CREATE TABLE MENSAJE (
    mensajeId INT PRIMARY KEY AUTO_INCREMENT,
    hiloId INT NOT NULL,
    usuarioId INT NOT NULL,
    contenido TEXT NOT NULL,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hiloId) REFERENCES HILO(hiloId),
    FOREIGN KEY (usuarioId) REFERENCES USUARIO(usuarioId)
);