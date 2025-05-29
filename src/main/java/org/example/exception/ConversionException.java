package org.example.exception;

/**
 * Исключение, выбрасываемое при ошибках конвертации XML в CSV.
 * Используется для обработки ошибок в процессе преобразования формата данных.
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
public class ConversionException extends RuntimeException {
    
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке
     * @param cause исходное исключение
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}