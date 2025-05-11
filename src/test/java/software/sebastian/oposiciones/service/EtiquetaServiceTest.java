package software.sebastian.oposiciones.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.sebastian.oposiciones.model.ArbolEtiqueta;
import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;

@ExtendWith(MockitoExtension.class)
class EtiquetaServiceTest {

    @Mock
    private EtiquetaRepository etiquetaRepo;

    @Mock
    private ArbolEtiquetaRepository arbolRepo;

    @InjectMocks
    private EtiquetaService service;

    private Etiqueta toDelete;

    @BeforeEach
    void setUp() {
        toDelete = new Etiqueta();
        toDelete.setEtiquetaId(1);
    }

    @Test
    void delete_NotFound_Throws() {
        when(etiquetaRepo.findById(1)).thenReturn(Optional.empty());
        try {
            service.delete(1);
        } catch (IllegalArgumentException ex) {
            // esperado
        }
        verify(etiquetaRepo).findById(1);
        verifyNoMoreInteractions(arbolRepo, etiquetaRepo);
    }

    @Test
    void delete_RootWithoutChildren() {
        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of());
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of());
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of());
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of());

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(etiquetaRepo).findById(1);
        o.verify(arbolRepo).findDirectChildrenIds(1);
        o.verify(arbolRepo).findDescendantsIds(1);
        o.verify(arbolRepo).findAncestorsOf(1);
        o.verify(arbolRepo).findRelationsByAncestor(1);
        verify(arbolRepo, never()).deleteExternalAncestors(anyList());
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());
        o.verify(arbolRepo).deleteAll(List.of());
        o.verify(arbolRepo).deleteAll(List.of());
        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }

    @Test
    void delete_LeafWithParent_NoChildrenBelow() {
        Etiqueta parent = new Etiqueta(); parent.setEtiquetaId(10);
        ArbolEtiqueta rel = new ArbolEtiqueta(parent, toDelete, 1);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of());
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of());
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of());

        service.delete(1);

        verify(arbolRepo).findAncestorsOf(1);
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());
        verify(arbolRepo).deleteAll(List.of(rel));
        verify(arbolRepo).deleteAll(List.of());
        verify(etiquetaRepo).delete(toDelete);
    }

    @Test
    void delete_WithChildrenAndParent_ReparentCorrectly() {
        // toDelete(1) tiene padre=10 y también otro ancestro a distancia 2
        Etiqueta parent = new Etiqueta(); parent.setEtiquetaId(10);
        ArbolEtiqueta rel1 = new ArbolEtiqueta(parent, toDelete,1);
        ArbolEtiqueta rel2 = new ArbolEtiqueta(new Etiqueta() {{ setEtiquetaId(100); }}, toDelete,2);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel1, rel2));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of(2,3));
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of(2,3,4));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of(
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(2); }},1),
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(4); }},2)
        ));

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(etiquetaRepo).findById(1);
        o.verify(arbolRepo).findDirectChildrenIds(1);
        o.verify(arbolRepo).findDescendantsIds(1);
        o.verify(arbolRepo).findAncestorsOf(1);

        o.verify(arbolRepo).deleteExternalAncestors(List.of(2,3,4));

        // reparent de cada hijo
        o.verify(arbolRepo).insertDirectParentChild(10, 2);
        o.verify(arbolRepo).bulkReparent(10, 2);
        o.verify(arbolRepo).insertDirectParentChild(10, 3);
        o.verify(arbolRepo).bulkReparent(10, 3);

        // limpieza closure‐table de todas las relaciones de toDelete
        o.verify(arbolRepo).findRelationsByAncestor(1);
        // primero borra ancestros (rel1, rel2)
        o.verify(arbolRepo).deleteAll(argThat((Iterable<ArbolEtiqueta> list) -> {
            int count = 0;
            for (ArbolEtiqueta ae : list) {
                if (ae.getDescendiente().getEtiquetaId().equals(1)) count++;
            }
            return count == 2;
        }));
        // luego borra descendientes directos e indirectos
        o.verify(arbolRepo).deleteAll(argThat((Iterable<ArbolEtiqueta> list) -> {
            // comprobamos que la lista no esté vacía
            return list.iterator().hasNext();
        }));

        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }

    @Test
    void delete_RootWithChildren_NoParentSoChildrenBecomeRoots() {
        // toDelete no tiene ancestro distancia=1, pero sí uno a distancia=2
        ArbolEtiqueta rel = new ArbolEtiqueta(new Etiqueta() {{ setEtiquetaId(50); }}, toDelete,2);

        when(etiquetaRepo.findById(1)).thenReturn(Optional.of(toDelete));
        when(arbolRepo.findAncestorsOf(1)).thenReturn(List.of(rel));
        when(arbolRepo.findDirectChildrenIds(1)).thenReturn(List.of(5));
        when(arbolRepo.findDescendantsIds(1)).thenReturn(List.of(5,6));
        when(arbolRepo.findRelationsByAncestor(1)).thenReturn(List.of(
            new ArbolEtiqueta(toDelete, new Etiqueta() {{ setEtiquetaId(5); }},1)
        ));

        service.delete(1);

        InOrder o = inOrder(arbolRepo, etiquetaRepo);
        o.verify(arbolRepo).deleteExternalAncestors(List.of(5,6));

        // al no haber padre directo, no se llama a reparent
        verify(arbolRepo, never()).insertDirectParentChild(anyInt(), anyInt());
        verify(arbolRepo, never()).bulkReparent(anyInt(), anyInt());

        // borrado closure‐table
        o.verify(arbolRepo).deleteAll(List.of(rel));
        o.verify(arbolRepo).deleteAll(anyIterable());

        o.verify(etiquetaRepo).delete(toDelete);
        o.verifyNoMoreInteractions();
    }
}
