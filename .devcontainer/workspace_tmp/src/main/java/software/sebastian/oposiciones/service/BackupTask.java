package software.sebastian.oposiciones.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupTask {

    @Autowired
    private BackupService backupService;

    @Scheduled(cron = "0 0 0 * * *")
    public void backupDiario() {
        try {
            String resultado = backupService.crearBackup();
            System.out.println("✅ Backup realizado a las 00:00: " + resultado);
        } catch (Exception e) {
            System.err.println("❌ Error en backup: " + e.getMessage());
        }
    }
}

