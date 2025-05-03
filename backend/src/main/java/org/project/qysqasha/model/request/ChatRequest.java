package org.project.qysqasha.model.request;

import lombok.Data;

@Data
public class ChatRequest {
    private RequestType type; // тип запроса (NOTES или MATERIAL)
    private String message; // текстовое сообщение пользователя
    private String fileId; // идентификатор загруженного файла (если есть)
    private GenerationOptions options; // опции генерации

    public enum RequestType {
        NOTES, // генерация конспекта
        MATERIAL // генерация учебного материала
    }
}