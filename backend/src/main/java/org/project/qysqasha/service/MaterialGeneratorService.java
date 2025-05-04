package org.project.qysqasha.service;

import lombok.RequiredArgsConstructor;
import org.project.qysqasha.model.request.ChatRequest;
import org.project.qysqasha.model.response.ChatResponse;
import org.project.qysqasha.util.AIPromptBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaterialGeneratorService {

    private final AIService aiService;
    private final FileProcessorService fileProcessorService;
    private final ExportService exportService;
    private final AIPromptBuilder promptBuilder;

    public ChatResponse generateMaterial(ChatRequest request) {
        String content;

        // Получаем содержимое для обработки
        if (request.getFileId() != null && !request.getFileId().isEmpty()) {
            content = fileProcessorService.getFileText(request.getFileId());
        }
        else {
            content = request.getMessage();
        }

        // Формируем промпт для ИИ
        String prompt = promptBuilder.buildMaterialPrompt(content, request.getOptions());

        // Генерируем структуру материала с помощью ИИ
        String generatedStructure = aiService.generateContent(prompt);

        // Создаем материал в нужном формате (PDF/PPTX)
        String format = request.getOptions().getFormat();
        String fileId = UUID.randomUUID().toString();
        String fileUrl = exportService.createMaterial(generatedStructure, format, fileId);

        // Формируем ответ
        return ChatResponse.builder()
                .type(ChatResponse.ResponseType.MATERIAL_READY)
                .message("Ваш материал готов к скачиванию.")
                .fileUrl(fileUrl)
                .build();
    }
}
