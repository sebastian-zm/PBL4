package software.sebastian.oposiciones.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.sebastian.oposiciones.model.Etiqueta;
import software.sebastian.oposiciones.repository.EtiquetaRepository;

@Service
public class EtiquetaEmbeddingBatchService {
    private final EtiquetaRepository etiquetaRepository;
    private final EtiquetaEmbeddingService etiquetaEmbeddingService;

    public EtiquetaEmbeddingBatchService(
            EtiquetaRepository etiquetaRepository,
            EtiquetaEmbeddingService etiquetaEmbeddingService
    ) {
        this.etiquetaRepository = etiquetaRepository;
        this.etiquetaEmbeddingService = etiquetaEmbeddingService;
    }

    @Transactional
    public void generarEmbeddingsParaTodasLasEtiquetas() {
        int page = 0, size = 500;
        long totalProcesadas = 0;
        Page<Etiqueta> bloque;

        do {
            // ← Aquí los imports importan
            bloque = etiquetaRepository.findAll(PageRequest.of(page++, size));
            for (Etiqueta e : bloque) {
                etiquetaEmbeddingService.generarYGuardarEmbedding(e.getEtiquetaId());
                totalProcesadas++;
            }
        } while (bloque.hasNext());

        System.out.println("Total de embeddings generados: " + totalProcesadas);
    }
}
