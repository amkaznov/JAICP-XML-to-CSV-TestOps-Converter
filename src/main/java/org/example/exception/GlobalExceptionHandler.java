package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Преобразует различные исключения в структурированные HTTP-ответы.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибки валидации входных параметров.
     *
     * @param ex исключение валидации аргументов метода
     * @return карта с описаниями ошибок валидации
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }

    /**
     * Обрабатывает ошибки конвертации XML в CSV.
     *
     * @param ex исключение процесса конвертации
     * @return карта с описанием ошибки
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConversionException.class)
    public Map<String, String> handleConversionException(ConversionException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }

    /**
     * Обрабатывает ошибки валидации XML.
     *
     * @param ex исключение валидации XML
     * @return карта с описанием ошибки
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(XmlValidationException.class)
    public Map<String, String> handleXmlValidationException(XmlValidationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        return errors;
    }
}
