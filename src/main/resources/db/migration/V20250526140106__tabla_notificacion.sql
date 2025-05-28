-- Flyway migration script
-- Created: Mon May 26 02:01:06 PM UTC 2025

-- Write your SQL below this line
CREATE TABLE NOTIFICACION (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuarioId INT NOT NULL,
    message TEXT NOT NULL,
    isRead BOOLEAN DEFAULT FALSE,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_usuario FOREIGN KEY (usuarioId) REFERENCES USUARIO(usuarioId) ON DELETE CASCADE
);
