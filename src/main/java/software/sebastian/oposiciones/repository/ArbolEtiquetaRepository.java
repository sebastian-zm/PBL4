package software.sebastian.oposiciones.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.ArbolEtiquetaId;

public interface ArbolEtiquetaRepository extends JpaRepository<ArbolEtiqueta, ArbolEtiquetaId> {

  /** Recupera sólo relaciones directas (distancia = 1). Una sola query. */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.distancia = 1
      """)
  List<ArbolEtiqueta> findAllDirectRelations();

  /** Recupera TODOS los descendientes directos e indirectos (ids) */
  @Query("""
        select ae.descendiente.etiquetaId
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
      """)
  List<Integer> findDescendantsIds(@Param("id") Integer ancestroId);

  /** Borra relaciones externas al subtree */
  @Modifying
  @Transactional
  @Query("""
        delete from ArbolEtiqueta ae
        where ae.descendiente.etiquetaId in :subtree
          and ae.ancestro.etiquetaId not in :subtree
      """)
  void deleteExternalAncestors(@Param("subtree") List<Integer> subtreeIds);

  /** Inserta padre→nodo directo */
  @Modifying
  @Transactional
  @Query(value = """
        INSERT IGNORE INTO ARBOL_ETIQUETAS
          (ancestroId, descendienteId, distancia)
        VALUES (:parentId, :nodeId, 1)
      """, nativeQuery = true)
  void insertDirectParentChild(@Param("parentId") Integer parentId,
      @Param("nodeId") Integer nodeId);

  @Modifying
  @Transactional
  @Query(value = "CALL move_subtree(:node, :parent)", nativeQuery = true)
  void bulkReparent(@Param("node") Integer nodeId, @Param("parent") Integer parentId);

  /** Todos los ancestros de un nodo dado */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.descendiente.etiquetaId = :id
      """)
  List<ArbolEtiqueta> findAncestorsOf(@Param("id") Integer descendienteId);

  /** Hijos directos (distancia=1) de un ancestro */
  @Query("""
        select ae.descendiente.etiquetaId
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
          and ae.distancia = 1
      """)
  List<Integer> findDirectChildrenIds(@Param("id") Integer ancestroId);

  /** Todas las filas donde esta etiqueta es ancestro */
  @Query("""
        select ae
        from ArbolEtiqueta ae
        where ae.ancestro.etiquetaId = :id
      """)
  List<ArbolEtiqueta> findRelationsByAncestor(@Param("id") Integer ancestroId);
}
