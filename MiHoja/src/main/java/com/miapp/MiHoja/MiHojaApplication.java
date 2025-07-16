package com.miapp.MiHoja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class MiHojaApplication {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(MiHojaApplication.class, args);

        abrirNavegador("http://localhost:8080");
    }

    private static void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    String[] browsers = {"xdg-open", "google-chrome", "firefox"};
                    boolean opened = false;
                    for (String browser : browsers) {
                        try {
                            Runtime.getRuntime().exec(new String[]{browser, url});
                            opened = true;
                            break;
                        } catch (Exception e) {
                            // intentar siguiente navegador
                        }
                    }
                    if (!opened) {
                        System.out.println("No se pudo abrir el navegador automáticamente.");
                    }
                } else {
                    System.out.println("Sistema operativo no soportado para abrir navegador automáticamente.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al abrir el navegador: " + e.getMessage());
        }
    }

    // Controlador para apagar la app al cerrar la pestaña
    @RestController
    public static class ShutdownController {

        @PostMapping("/shutdown")
        public void shutdown() {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(1000); // Espera 1 segundo para terminar procesos
                    context.close();   // Cierra el contexto de Spring Boot
                    System.exit(0);    // Apaga la app
                } catch (InterruptedException e) {
                    // Ignorar interrupciones
                }
            });
            thread.setDaemon(false);
            thread.start();
        }
    }
}
 