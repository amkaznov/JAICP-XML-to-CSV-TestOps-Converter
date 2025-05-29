package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO класс для получения XML контента в теле запроса.
 * Используется в эндпоинте /api/convert/xml для получения XML данных.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRequest {
    
    /**
     * XML содержимое для конвертации.
     * Не может быть пустым или состоять только из пробельных символов.
     */
    @NotBlank(message = "XML содержимое не может быть пустым")
    private String xmlContent;
}
