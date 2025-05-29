package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки OpenAPI документации.
 * Предоставляет конфигурацию Swagger UI для визуализации REST API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Создает и настраивает bean OpenAPI для документации API.
     * Устанавливает основную информацию о API: название, версию и описание.
     *
     * @return настроенный объект OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("XML to CSV Converter API")
                        .version("1.0")
                        .description("API для конвертации XML файлов в CSV"));
    }
}