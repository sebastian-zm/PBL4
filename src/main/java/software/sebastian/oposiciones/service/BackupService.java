package software.sebastian.oposiciones.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BackupService {

    private static final String NOMBRE_BD = "oposiciones";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "root";
    private static final String MASTER_HOST = "db";
    private static final String SLAVE_HOST = "db_slave";

    private static final Path BACKUP_DIR = Paths.get(System.getProperty("user.dir"), "copias");

    /**
     * Crea un archivo de backup usando mysqldump desde el esclavo
     */
    public String crearBackup() throws IOException, InterruptedException {
        if (Files.notExists(BACKUP_DIR)) {
            Files.createDirectories(BACKUP_DIR);
        }

        String timestamp = ZonedDateTime.now(ZoneId.of("Europe/Madrid"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String nombreArchivo = "backup_" + timestamp + ".sql";
        Path destino = BACKUP_DIR.resolve(nombreArchivo);

        // Verifica si db_slave está accesible antes de ejecutar mysqldump
        List<String> comando = List.of("bash", "-c",
            String.format("mysqldump -h %s -u %s -p%s %s > %s",
                SLAVE_HOST, USUARIO, PASSWORD, NOMBRE_BD, destino.toAbsolutePath()));

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.directory(new File(System.getProperty("user.dir")));
        Process proceso = pb.start();
        int exitCode = proceso.waitFor();

        if (exitCode == 0) {
            return "✅ Backup creado correctamente: " + destino.getFileName();
        } else {
            throw new IOException("❌ Error al ejecutar: " + String.join(" ", comando));
        }
    }

    /**
     * Lista todos los archivos .sql en la carpeta de backups
     */
    public List<String> listarCopiasDisponibles() throws IOException {
        if (Files.notExists(BACKUP_DIR)) return List.of();

        return Files.list(BACKUP_DIR)
                .filter(p -> p.toString().endsWith(".sql"))
                .map(p -> p.getFileName().toString())
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    /**
     * Restaura un archivo .sql sobre el maestro y espera replicación automática
     */
    public String restaurarCopia(String nombreArchivo) throws IOException, InterruptedException {
        Path archivo = BACKUP_DIR.resolve(nombreArchivo);

        if (!Files.exists(archivo)) {
            throw new IOException("No se encontró el archivo: " + nombreArchivo);
        }

        String restoreCmd = String.format("mysql -h %s -u %s -p%s %s < %s",
                MASTER_HOST, USUARIO, PASSWORD, NOMBRE_BD, archivo.toAbsolutePath());

        ProcessBuilder pb = new ProcessBuilder("bash", "-c", restoreCmd);
        pb.directory(new File(System.getProperty("user.dir")));
        Process proceso = pb.start();
        int exitCode = proceso.waitFor();

        if (exitCode != 0) {
            throw new IOException("❌ Error al restaurar. Código de salida: " + exitCode);
        }

        // Espera a que la replicación alcance el esclavo
        Thread.sleep(3000);

        return "✅ Base de datos restaurada desde: " + nombreArchivo + " y replicada en el esclavo";
    }

    /**
     * Elimina un archivo de backup del sistema
     */
    public String borrarCopia(String nombreArchivo) throws IOException {
        Path archivo = BACKUP_DIR.resolve(nombreArchivo);

        if (!Files.exists(archivo)) {
            throw new IOException("El archivo no existe: " + nombreArchivo);
        }

        Files.delete(archivo);
        return "🗑️ Backup eliminado: " + nombreArchivo;
    }
}
