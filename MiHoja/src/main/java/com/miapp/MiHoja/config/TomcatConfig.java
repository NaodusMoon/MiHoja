package com.miapp.MiHoja.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setProperty("maxParameterCount", "10000");
            connector.setProperty(
                "org.apache.tomcat.util.http.fileupload.FileUploadBase.fileCountMax", 
                "50"
            ); // Límite de cantidad de archivos
        });
    }
}
