package org.example.domain.model.attributes;

import lombok.Builder;
import lombok.Data;

/**
 * Представляет один ожидаемый результат шага теста.
 * Может содержать:
 * 1. Комбинацию state и botResponse вместе (от тега &lt;a&gt;)
 * 2. Поле и его значение для проверки (от тега &lt;responseData&gt;)
 * В одном шаге может быть несколько таких результатов.
 */
@Data
@Builder
public class ExpectedResult {
    /** Ожидаемое состояние */
    private String state;
    
    /** Ожидаемый ответ бота */
    private String botResponse;
    
    /** Поле для проверки */
    private String field;
    
    /** Значение для проверки поля */
    private String fieldValue;
    
    /**
     * Преобразует результат в строковое представление.
     *
     * @return строковое представление результата
     */
    public String format() {
        if (field != null) {
            if ("replies".equals(field)) {
                return "Ожидаемое тело:\n" + fieldValue;
            } else if (fieldValue == null || fieldValue.isEmpty()) {
                return "Ключ " + field + " не равен NULL/существует в ответе";
            } else {
                return "Элемент тела\n " + field + "\nимеет значение\n" + fieldValue;
            }
        }
        
        StringBuilder result = new StringBuilder();
        if (state != null && !state.isEmpty()) {
            result.append("state = '").append(state).append("'");
        }
        
        if (botResponse != null && !botResponse.isEmpty()) {
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append("Ответ бота:\n").append(botResponse);
        }
        
        return result.toString();
    }
}
