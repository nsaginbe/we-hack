package org.project.qysqasha.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AIConfig {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.endpoint}")
    private String endpoint;

    @Value("${ai.model}")
    private String model;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
