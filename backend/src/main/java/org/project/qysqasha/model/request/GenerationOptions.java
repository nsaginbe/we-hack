package org.project.qysqasha.model.request;

import lombok.Data;

@Data
public class GenerationOptions {
    private String style; // стиль генерации (например, "с эмодзи", "формальный" и т.д.)
    private String format; // формат выходных данных (PDF, PPTX)
    private String complexity; // сложность материала (базовый, продвинутый)
    private boolean includeTests; // включать ли тесты (для опции конспекта)
}
