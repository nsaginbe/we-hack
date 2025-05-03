package org.project.qysqasha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AIConfig {

    private String apiKey;
    private String endpoint;
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
