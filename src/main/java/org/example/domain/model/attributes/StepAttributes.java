package org.example.domain.model.attributes;

import lombok.Builder;
import lombok.Data;

/**
 * Атрибуты шага тест-кейса.
 * Хранит различные типы шагов: события (event), запросы (request)
 * и вопросы (question).
 */
@Data
@Builder
public class StepAttributes {
    /** Событие для обработки */
    private String event;
    
    /** Запрос для выполнения */
    private String request;
    
    /** Вопрос для отправки */
    private String question;
    
    /**
     * Проверяет, является ли имя узла допустимым шагом.
     *
     * @param nodeName имя узла XML
     * @return true если узел является шагом, false в противном случае
     */
    public static boolean isStepNode(String nodeName) {
        return switch (nodeName) {
            case "event", "request", "q" -> true;
            default -> false;
        };
    }
    
    /**
     * Создает атрибуты шага на основе имени узла и его содержимого.
     *
     * @param nodeName имя узла XML
     * @param content содержимое узла
     * @return объект атрибутов шага или null
     */
    public static StepAttributes fromNodeName(String nodeName, String content) {
        return switch (nodeName) {
            case "event" -> StepAttributes.builder()
                .event(content)
                .build();
            case "request" -> StepAttributes.builder()
                .request(content)
                .build();
            case "q" -> StepAttributes.builder()
                .question(content)
                .build();
            default -> null;
        };
    }
    
    /**
     * Получает описание шага в зависимости от его типа.
     *
     * @return строка с описанием шага
     */
    public String getDescription() {
        if (event != null) {
            return "Вызвать ивент:\n" + event;
        }
        if (request != null) {
            return "Отправить запрос:";
        }
        if (question != null) {
            return "Отправить текст в бота:\n" + question;
        }
        return "";
    }
    
    /**
     * Проверяет, имеет ли шаг подшаги.
     * Только шаги типа request могут иметь подшаги.
     *
     * @return true если шаг имеет подшаги, false в противном случае
     */
    public boolean hasSubSteps() {
        return request != null; // Только request имеет подшаги
    }
    
    /**
     * Получает массив подшагов.
     *
     * @return массив строк с подшагами или пустой массив
     */
    public String[] getSubSteps() {
        if (request != null) {
            return new String[]{request};
        }
        return new String[0];
    }
}
