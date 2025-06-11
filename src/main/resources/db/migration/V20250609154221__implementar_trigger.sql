-- Flyway migration script
-- Created: Mon Jun  9 03:42:21 PM UTC 2025

-- Write your SQL below this line

-- Flyway migration script
-- Created: Fri May 23 02:17:23 PM UTC 2025

DELIMITER //

CREATE TRIGGER actualizar_valoracion_global
AFTER INSERT ON FEEDBACK
FOR EACH ROW
BEGIN
    IF NEW.aprobado = true THEN
        UPDATE ETIQUETADO 
        SET valoracion = valoracion + 1
        WHERE convocatoriaId = NEW.convocatoriaId 
        AND etiquetaId = NEW.etiquetaId;
    ELSE 
        UPDATE ETIQUETADO
        SET valoracion = valoracion - 1
        WHERE convocatoriaId = NEW.convocatoriaId 
        AND etiquetaId = NEW.etiquetaId;
    END IF;
END//

CREATE TRIGGER actualizar_valoracion_global_update
AFTER UPDATE ON FEEDBACK
FOR EACH ROW
BEGIN
    IF NEW.aprobado != OLD.aprobado THEN
        IF NEW.aprobado = true THEN
            UPDATE ETIQUETADO 
            SET valoracion = valoracion + 1
            WHERE convocatoriaId = NEW.convocatoriaId 
            AND etiquetaId = NEW.etiquetaId;
        ELSE
            UPDATE ETIQUETADO
            SET valoracion = valoracion - 1
            WHERE convocatoriaId = NEW.convocatoriaId 
            AND etiquetaId = NEW.etiquetaId;
        END IF;
    END IF;
END//

DELIMITER;
