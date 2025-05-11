-- Flyway migration script
-- Created: Sat May 10 11:56:42 PM UTC 2025

-- Write your SQL below this line
DELIMITER //
CREATE DEFINER=`dev`@`%` PROCEDURE `move_subtree`(
  IN p_node   INT,
  IN p_parent INT
)
BEGIN
   -- 1. Delete outdated paths that connect subtree descendants to old ancestors (not within subtree)
  DELETE a FROM ARBOL_ETIQUETAS AS a
  JOIN ARBOL_ETIQUETAS AS d ON a.descendienteId = d.descendienteId
  LEFT JOIN ARBOL_ETIQUETAS AS x ON x.ancestroId = d.ancestroId AND x.descendienteId = a.ancestroId
  WHERE d.ancestroId = p_node AND x.ancestroId IS NULL;

  -- 2. Insert new paths connecting all ancestors of new parent to all descendants of subtree
  INSERT INTO ARBOL_ETIQUETAS (ancestroId, descendienteId, distancia)
  SELECT supertree.ancestroId, subtree.descendienteId,
         supertree.distancia + subtree.distancia + 1
  FROM ARBOL_ETIQUETAS AS supertree
  JOIN ARBOL_ETIQUETAS AS subtree
  WHERE subtree.ancestroId = p_node
    AND supertree.descendienteId = p_parent;
END//
DELIMITER ;
