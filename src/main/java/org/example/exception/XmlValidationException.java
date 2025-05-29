package org.example.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации XML.
 * Используется для обработки ошибок структуры и содержимого XML файлов.
 */
public class XmlValidationException extends RuntimeException {
    
    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message сообщение об ошибке валидации
     */
    public XmlValidationException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     *
     * @param message сообщение об ошибке валидации
     * @param cause исходное исключение
     */
    public XmlValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}