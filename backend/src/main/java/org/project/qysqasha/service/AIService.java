package org.project.qysqasha.service;

import lombok.RequiredArgsConstructor;
import org.project.qysqasha.config.AIConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final AIConfig aiConfig;
    private final RestTemplate restTemplate;

    /**
     * Отправляет запрос к API искусственного интеллекта
     */
    public String generateContent(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getApiKey());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", aiConfig.getModel());
        requestBody.put("messages", Collections.singletonList(
                Map.of("role", "user",
                        "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                aiConfig.getEndpoint(),
                HttpMethod.POST,
                entity,
                Map.class
        );

        // Обработка ответа в зависимости от конкретного API
        // Пример для OpenAI API
        Map responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("choices")) {
            return ((Map)((Map)((java.util.List)responseBody.get("choices")).get(0)).get("message")).get("content").toString();
        }

        return "Не удалось получить ответ от ИИ.";
    }
}
