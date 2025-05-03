package org.project.qysqasha.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private String message; // текстовое сообщение от бота
    private String generatedContent; // сгенерированный контент (для конспектов)
    private String fileUrl; // ссылка на файл (для учебных материалов)
    private ResponseType type; // тип ответа

    public enum ResponseType {
        TEXT, // обычный текстовый ответ
        NOTES, // сгенерированный конспект
        MATERIAL_READY // ссылка на материал готова
    }
}
