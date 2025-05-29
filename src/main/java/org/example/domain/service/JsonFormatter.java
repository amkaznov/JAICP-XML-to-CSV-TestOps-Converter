package org.example.domain.service;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Сервис для форматирования JSON строк.
 * Предоставляет функциональность для красивого форматирования JSON контента
 * с отступами и переносами строк.
 */
@Service
public class JsonFormatter {
    /** ObjectMapper для форматирования JSON с отступами */
    private static final ObjectMapper prettyObjectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    
    /** ObjectMapper для компактного представления JSON */
    private static final ObjectMapper compactObjectMapper = new ObjectMapper();

    /**
     * Форматирует JSON строку, добавляя отступы и переносы строк.
     * Если входная строка не является JSON объектом, возвращает её без изменений.
     *
     * @param text строка для форматирования
     * @return отформатированный JSON или исходная строка, если форматирование невозможно
     */
    public static String formatJson(String text) {
        if (!isJsonObject(text)) {
            return text;
        }

        try {
            // Сначала читаем JSON в объект с помощью компактного маппера
            Object jsonObject = compactObjectMapper.readValue(text, Object.class);
            
            // Всегда возвращаем красиво отформатированный JSON
            return prettyObjectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            // Если произошла ошибка парсинга, возвращаем исходный текст
            return text;
        }
    }

    /**
     * Проверяет, является ли текст JSON объектом или массивом.
     * Проверка основана на наличии фигурных или квадратных скобок в начале и конце строки.
     *
     * @param text проверяемая строка
     * @return true если текст похож на JSON объект или массив, false в противном случае
     */
    private static boolean isJsonObject(String text) {
        String trimmed = text.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) || 
               (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }
}