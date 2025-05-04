package org.project.qysqasha.service;

import lombok.RequiredArgsConstructor;
import org.project.qysqasha.model.request.ChatRequest;
import org.project.qysqasha.model.response.ChatResponse;
import org.project.qysqasha.util.AIPromptBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteGeneratorService {

    private final AIService aiService;
    private final FileProcessorService fileProcessorService;
    private final AIPromptBuilder promptBuilder;

    public ChatResponse generateNotes(ChatRequest request) {
        String content;

        // Получаем содержимое для обработки (из текста сообщения или файла)
        if (request.getFileId() != null && !request.getFileId().isEmpty()) {
            content = fileProcessorService.getFileText(request.getFileId());
        }
        else {
            content = request.getMessage();
        }

        // Формируем промпт для ИИ
        String prompt = promptBuilder.buildNotesPrompt(content, request.getOptions());

        // Генерируем конспект с помощью ИИ
        String generatedNotes = aiService.generateContent(prompt);

        // Формируем ответ
        return ChatResponse.builder()
                .type(ChatResponse.ResponseType.NOTES)
                .message("Вот ваш конспект:")
                .generatedContent(generatedNotes)
                .build();
    }
}
