-- Flyway migration script
-- Created: Sun May 11 08:15:24 PM UTC 2025

-- Write your SQL below this line
DELIMITER $$

CREATE DEFINER=`dev`@`%` PROCEDURE insertar_etiquetas(IN p_json JSON)
BEGIN
  DECLARE i INT DEFAULT 0;
  DECLARE j INT DEFAULT 0;
  DECLARE k INT DEFAULT 0;
  DECLARE n INT;
  DECLARE m INT;
  DECLARE r INT;
  DECLARE keyName VARCHAR(255);
  DECLARE subkeyName VARCHAR(255);
  DECLARE elem VARCHAR(255);
  DECLARE parentId INT;
  DECLARE childId INT;
  DECLARE grandChildId INT;
  DECLARE val JSON;
  DECLARE valType VARCHAR(10);
  DECLARE rootKeys JSON;
  DECLARE subKeys JSON;
  DECLARE subVal JSON;

  -- 1) obtenemos las claves del nivel raíz
  SET rootKeys = JSON_KEYS(p_json);
  SET n        = JSON_LENGTH(rootKeys);
  SET i        = 0;

  root_loop: LOOP
    IF i >= n THEN
      LEAVE root_loop;
    END IF;

    -- nombre de la key de primer nivel (p.ej. "Andalucía")
    SET keyName = JSON_UNQUOTE(JSON_EXTRACT(rootKeys, CONCAT('$[', i, ']')));
    SET val     = JSON_EXTRACT(p_json, CONCAT('$."', keyName, '"'));
    SET valType = JSON_TYPE(val);

    -- 2) insertamos/modificamos la etiqueta de primer nivel
    INSERT IGNORE INTO ETIQUETA(nombre) VALUES (keyName);
    SELECT etiquetaId INTO parentId
      FROM ETIQUETA WHERE nombre = keyName;

    -- reflexiva (padre → padre, distancia 0)
    INSERT IGNORE INTO ARBOL_ETIQUETAS
      (ancestroId, descendienteId, distancia)
    VALUES
      (parentId, parentId, 0);

    -- 3) si es un array de strings ⇒ hojas directas
    IF valType = 'ARRAY' THEN
      SET m = JSON_LENGTH(val);
      SET j = 0;
      array_loop: LOOP
        IF j >= m THEN
          LEAVE array_loop;
        END IF;
        SET elem = JSON_UNQUOTE(JSON_EXTRACT(val, CONCAT('$[', j, ']')));

        -- insertamos la etiqueta de provincia
        INSERT IGNORE INTO ETIQUETA(nombre) VALUES (elem);
        SELECT etiquetaId INTO childId
          FROM ETIQUETA WHERE nombre = elem;

        -- reflexiva para la provincia
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES
          (childId, childId, 0);

        -- clausura transitiva desde todos los ancestros de "parentId"
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        SELECT a.ancestroId, childId, a.distancia + 1
        FROM ARBOL_ETIQUETAS AS a
        WHERE a.descendienteId = parentId;

        SET j = j + 1;
      END LOOP array_loop;

    -- 4) si es un objeto ⇒ anidamos un nivel más
    ELSEIF valType = 'OBJECT' THEN
      SET subKeys = JSON_KEYS(val);
      SET m       = JSON_LENGTH(subKeys);
      SET j       = 0;

      object_loop: LOOP
        IF j >= m THEN
          LEAVE object_loop;
        END IF;
        SET subkeyName = JSON_UNQUOTE(JSON_EXTRACT(subKeys, CONCAT('$[', j, ']')));
        SET subVal     = JSON_EXTRACT(val, CONCAT('$."', subkeyName, '"'));

        -- insertamos la etiqueta de nivel intermedio
        INSERT IGNORE INTO ETIQUETA(nombre) VALUES (subkeyName);
        SELECT etiquetaId INTO childId
          FROM ETIQUETA WHERE nombre = subkeyName;

        -- reflexiva para este nodo
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES
          (childId, childId, 0);

        -- clausura transitiva desde ancestros de parentId
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        SELECT a.ancestroId, childId, a.distancia + 1
        FROM ARBOL_ETIQUETAS AS a
        WHERE a.descendienteId = parentId;

        -- suponemos que subVal es un array de hojas
        IF JSON_TYPE(subVal) = 'ARRAY' THEN
          SET r = JSON_LENGTH(subVal);
          SET k = 0;
          inner_array_loop: LOOP
            IF k >= r THEN
              LEAVE inner_array_loop;
            END IF;
            SET elem = JSON_UNQUOTE(JSON_EXTRACT(subVal, CONCAT('$[', k, ']')));

            INSERT IGNORE INTO ETIQUETA(nombre) VALUES (elem);
            SELECT etiquetaId INTO grandChildId
              FROM ETIQUETA WHERE nombre = elem;

            -- reflexiva para la hoja
            INSERT IGNORE INTO ARBOL_ETIQUETAS
              (ancestroId, descendienteId, distancia)
            VALUES
              (grandChildId, grandChildId, 0);

            -- clausura transitiva desde ancestros de childId
            INSERT IGNORE INTO ARBOL_ETIQUETAS
              (ancestroId, descendienteId, distancia)
            SELECT a.ancestroId, grandChildId, a.distancia + 1
            FROM ARBOL_ETIQUETAS AS a
            WHERE a.descendienteId = childId;

            SET k = k + 1;
          END LOOP inner_array_loop;
        END IF;

        SET j = j + 1;
      END LOOP object_loop;
    END IF;

    SET i = i + 1;
  END LOOP root_loop;
END$$

DELIMITER ;
