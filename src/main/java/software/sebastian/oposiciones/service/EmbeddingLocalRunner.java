package software.sebastian.oposiciones.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmbeddingLocalRunner {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingLocalRunner.class);

    /**
     * Si quieres que se ejecute una vez al arrancar:
     */
    @PostConstruct
    public void onStartup() {
        runScript();
    }

    /**
     * Si prefieres que sea un job programado (aquí cada día a las 2am):
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "Europe/Madrid")
    public void scheduledRun() {
        runScript();
    }

    private void runScript() {
        try {
            // Asegúrate de que bin/generar_embeddings_local es ejecutable
            ProcessBuilder pb = new ProcessBuilder("./bin/generar_embeddings_local");
            // Opcional: define el directorio de trabajo si tu script
            // asume rutas relativas
            pb.directory(new File(System.getProperty("user.dir")));
            // hereda stdout/stderr para ver logs en tu consola
            pb.inheritIO();

            log.info("Lanzando script de embeddings locales...");
            Process p = pb.start();
            int exit = p.waitFor();

            if (exit != 0) {
                log.error("El script bin/generar_embeddings_local terminó con código {}", exit);
            } else {
                log.info("Script bin/generar_embeddings_local ejecutado con éxito.");
            }
        } catch (Exception e) {
            log.error("Error ejecutando bin/generar_embeddings_local", e);
        }
    }
}
