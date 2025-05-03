package org.project.qysqasha.util;

import org.project.qysqasha.model.request.GenerationOptions;
import org.springframework.stereotype.Component;

@Component
public class AIPromptBuilder {

    /**
     * Строит промпт для генерации конспекта
     */
    public String buildNotesPrompt(String content, GenerationOptions options) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Ты - умный помощник для обучения. ");
        prompt.append("Создай краткий и информативный конспект на основе следующего учебного материала. ");

        if (options != null) {
            if (options.getStyle() != null && !options.getStyle().isEmpty()) {
                prompt.append("Используй следующий стиль: ").append(options.getStyle()).append(". ");
            }

            if (options.getComplexity() != null && !options.getComplexity().isEmpty()) {
                prompt.append("Уровень сложности: ").append(options.getComplexity()).append(". ");
            }

            if (options.isIncludeTests()) {
                prompt.append("В конце конспекта добавь 3-5 тестовых вопросов для самопроверки. ");
            }
        }

        prompt.append("\n\nУчебный материал:\n").append(content);

        return prompt.toString();
    }

    /**
     * Строит промпт для генерации учебного материала
     */
    public String buildMaterialPrompt(String content, GenerationOptions options) {
        StringBuilder prompt = new StringBuilder();

        String format = options != null && options.getFormat() != null ? options.getFormat() : "PDF";

        prompt.append("Ты - умный помощник для обучения. ");
        prompt.append("Создай структурированный учебный материал в формате ").append(format).append(" на основе следующего контента. ");

        if (format.equalsIgnoreCase("PPTX")) {
            prompt.append("Структурируй материал в виде слайдов. ");
            prompt.append("Каждый новый слайд отмечай строкой '--- SLIDE ---'. ");
            prompt.append("Первая строка каждого слайда - это заголовок, остальное - содержимое. ");
            prompt.append("Используй маркированные списки с '- ' для перечислений. ");
        } else {
            prompt.append("Структурируй материал в виде глав и разделов. ");
            prompt.append("Используй заголовки разных уровней для организации контента. ");
        }

        if (options != null) {
            if (options.getStyle() != null && !options.getStyle().isEmpty()) {
                prompt.append("Используй следующий стиль: ").append(options.getStyle()).append(". ");
            }

            if (options.getComplexity() != null && !options.getComplexity().isEmpty()) {
                prompt.append("Уровень сложности: ").append(options.getComplexity()).append(". ");
            }
        }

        prompt.append("\n\nИсходный контент:\n").append(content);

        return prompt.toString();
    }
}