package software.sebastian.oposiciones.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.sebastian.oposiciones.model.Etiquetado;
import software.sebastian.oposiciones.model.Modelo;
import software.sebastian.oposiciones.repository.ArbolEtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetaRepository;
import software.sebastian.oposiciones.repository.EtiquetadoRepository;
import software.sebastian.oposiciones.repository.ModeloRepository;

@ExtendWith(MockitoExtension.class)
class EtiquetadoServiceTest {

    @Mock
    private EtiquetadoRepository etiquetadoRepo;
    
    @Mock
    private ModeloRepository modeloRepo;
    
    @Mock
    private EtiquetaEmbeddingService etiquetaEmbeddingService;
    
    @Mock
    private ConvocatoriaEmbeddingService convocatoriaEmbeddingService;
    
    @Mock
    private EtiquetaRepository etiRepo;
    
    @Mock
    private SuscripcionEtiquetaService SEservice;
    
    @Mock
    private ArbolEtiquetaRepository arbolRepo;
    
    @InjectMocks
    private EtiquetadoService etiquetadoService;
    
    private static final String EMBEDDING_MODEL = "text-embedding-3-large";
    private static final double UMBRAL_GUARDADO = 0.4;
    
    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(etiquetadoRepo, modeloRepo, etiquetaEmbeddingService, 
              convocatoriaEmbeddingService, etiRepo, SEservice, arbolRepo);
    }
    
    @Test
    void testTagConvocatoria_SuccessfulTagging() {
        // Given
        Integer convocatoriaId = 1;
        Integer modeloId = 10;
        
        // Mock embedding for convocatoria
        double[] convocatoriaEmbedding = {0.5, 0.5, 0.5};
        when(convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId))
            .thenReturn(convocatoriaEmbedding);
        
        // Mock modelo
        Modelo modelo = new Modelo();
        modelo.setModeloId(modeloId);
        modelo.setNombre(EMBEDDING_MODEL);
        when(modeloRepo.findByNombre(EMBEDDING_MODEL))
            .thenReturn(Optional.of(modelo));
        
        // Mock etiqueta embeddings with calculated similarities
        Map<Integer, double[]> embeddingsPorEtiqueta = new HashMap<>();
        // Similarity with [0.5, 0.5, 0.5] will be 1.0 (identical normalized vectors)
        embeddingsPorEtiqueta.put(100, new double[]{0.5, 0.5, 0.5}); 
        // Similarity will be ~0.577 (above threshold of 0.4)
        embeddingsPorEtiqueta.put(101, new double[]{1.0, 0.0, 0.0}); 
        // Similarity will be ~0.69 (above threshold)
        embeddingsPorEtiqueta.put(102, new double[]{0.9, 0.1, 0.1});
        
        when(etiquetaEmbeddingService.cargarEmbeddingsPorModelo(modeloId))
            .thenReturn(embeddingsPorEtiqueta);
        
        // When
        assertDoesNotThrow(() -> {
            // Use reflection to call private method
            var method = EtiquetadoService.class.getDeclaredMethod("tagConvocatoria", Integer.class);
            method.setAccessible(true);
            method.invoke(etiquetadoService, convocatoriaId);
        });
        
        // Then
        ArgumentCaptor<Etiquetado> etiquetadoCaptor = ArgumentCaptor.forClass(Etiquetado.class);
        verify(etiquetadoRepo, times(3)).save(etiquetadoCaptor.capture());
        
        var savedEtiquetados = etiquetadoCaptor.getAllValues();
        assertEquals(3, savedEtiquetados.size());
        
        // Verify that only high similarity etiquetas were saved
        assertTrue(savedEtiquetados.stream()
            .allMatch(e -> e.getConfianza() >= UMBRAL_GUARDADO));
        
        // Verify correct convocatoria and modelo IDs
        assertTrue(savedEtiquetados.stream()
            .allMatch(e -> e.getConvocatoriaId().equals(convocatoriaId)));
        assertTrue(savedEtiquetados.stream()
            .allMatch(e -> e.getModeloId().equals(modeloId)));
    }
    
    @Test
    void testTagConvocatoria_ModeloNotFound() {
        // Given
        Integer convocatoriaId = 1;
        double[] convocatoriaEmbedding = {0.5, 0.5, 0.5};
        
        when(convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId))
            .thenReturn(convocatoriaEmbedding);
        
        when(modeloRepo.findByNombre(EMBEDDING_MODEL))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            var method = EtiquetadoService.class.getDeclaredMethod("tagConvocatoria", Integer.class);
            method.setAccessible(true);
            try {
                method.invoke(etiquetadoService, convocatoriaId);
            } catch (Exception e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });
        
        verify(etiquetadoRepo, never()).save(any());
    }
    
    @Test
    void testTagConvocatoria_NoEtiquetasAboveThreshold() {
        // Given
        Integer convocatoriaId = 1;
        Integer modeloId = 10;
        
        double[] convocatoriaEmbedding = {0.5, 0.5, 0.5};
        when(convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId))
            .thenReturn(convocatoriaEmbedding);
        
        Modelo modelo = new Modelo();
        modelo.setModeloId(modeloId);
        modelo.setNombre(EMBEDDING_MODEL);
        when(modeloRepo.findByNombre(EMBEDDING_MODEL))
            .thenReturn(Optional.of(modelo));
        
        // All etiquetas have low similarity
        Map<Integer, double[]> embeddingsPorEtiqueta = new HashMap<>();
        // These vectors will have similarity < 0.4 with [0.5, 0.5, 0.5]
        embeddingsPorEtiqueta.put(100, new double[]{1.0, -0.5, -0.5}); // similarity ~0.33
        embeddingsPorEtiqueta.put(101, new double[]{-0.5, 1.0, -0.5}); // similarity ~0.33
        
        when(etiquetaEmbeddingService.cargarEmbeddingsPorModelo(modeloId))
            .thenReturn(embeddingsPorEtiqueta);
        
        // When
        assertDoesNotThrow(() -> {
            var method = EtiquetadoService.class.getDeclaredMethod("tagConvocatoria", Integer.class);
            method.setAccessible(true);
            method.invoke(etiquetadoService, convocatoriaId);
        });
        
        // Then
        verify(etiquetadoRepo, never()).save(any());
    }
    
    @Test
    void testTagConvocatoria_ExceptionInEmbeddingGeneration() {
        // Given
        Integer convocatoriaId = 1;
        
        when(convocatoriaEmbeddingService.generarYGuardarEmbedding(convocatoriaId))
            .thenThrow(new RuntimeException("Error generating embedding"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            var method = EtiquetadoService.class.getDeclaredMethod("tagConvocatoria", Integer.class);
            method.setAccessible(true);
            try {
                method.invoke(etiquetadoService, convocatoriaId);
            } catch (Exception e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw new RuntimeException(e);
            }
        });
        
        verify(modeloRepo, never()).findByNombre(any());
        verify(etiquetadoRepo, never()).save(any());
    }
    
    @Test
    void testCosineSimilarity() throws Exception {
        // Test cosine similarity calculation using reflection
        var method = EtiquetadoService.class.getDeclaredMethod("cosineSimilarity", double[].class, double[].class);
        method.setAccessible(true);
        
        // Test case 1: Identical vectors
        double[] vec1 = {1.0, 0.0, 0.0};
        double[] vec2 = {1.0, 0.0, 0.0};
        double similarity1 = (double) method.invoke(etiquetadoService, vec1, vec2);
        assertEquals(1.0, similarity1, 0.0001);
        
        // Test case 2: Orthogonal vectors
        double[] vec3 = {1.0, 0.0, 0.0};
        double[] vec4 = {0.0, 1.0, 0.0};
        double similarity2 = (double) method.invoke(etiquetadoService, vec3, vec4);
        assertEquals(0.0, similarity2, 0.0001);
        
        // Test case 3: Opposite vectors
        double[] vec5 = {1.0, 0.0, 0.0};
        double[] vec6 = {-1.0, 0.0, 0.0};
        double similarity3 = (double) method.invoke(etiquetadoService, vec5, vec6);
        assertEquals(-1.0, similarity3, 0.0001);
        
        // Test case 4: Normalized vectors with angle
        double[] vec7 = {0.6, 0.8, 0.0};
        double[] vec8 = {0.8, 0.6, 0.0};
        double similarity4 = (double) method.invoke(etiquetadoService, vec7, vec8);
        assertEquals(0.96, similarity4, 0.0001);
    }
}