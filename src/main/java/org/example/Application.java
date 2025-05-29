package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для конвертации XML в CSV.
 * Использует Spring Boot для запуска веб-приложения.
 */
@SpringBootApplication
public class Application {
    
    /**
     * Точка входа в приложение.
     * Запускает Spring Boot приложение с настроенными компонентами.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}