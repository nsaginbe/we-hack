package org.project.qysqasha.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private String fileId;              // уникальный идентификатор файла
    private String originalFilename;    // исходное имя файла
    private String extractedText;       // извлеченный из файла текст
    private String status;              // статус обработки
    private String error;               // сообщение об ошибке (если есть)
}
