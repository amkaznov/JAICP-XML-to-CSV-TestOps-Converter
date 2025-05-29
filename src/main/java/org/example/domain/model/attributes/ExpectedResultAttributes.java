package org.example.domain.model.attributes;

import lombok.Builder;
import lombok.Data;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * Атрибуты ожидаемого результата шага тест-кейса.
 * Содержит информацию о состоянии, ответе бота и других параметрах результата.
 */
@Data
@Builder
public class ExpectedResultAttributes {
    /** Ожидаемое состояние */
    private String state;
    
    /** Ожидаемый ответ бота */
    private String botResponse;
    
    /** Поле для проверки */
    private String field;
    
    /** Ожидаемые данные ответа */
    private String responseData;
    
    /**
     * Проверяет, является ли узел XML узлом результата.
     *
     * @param nodeName имя узла
     * @return true если узел является результатом, false в противном случае
     */
    public static boolean isResultNode(String nodeName) {
        return switch (nodeName) {
            case "a", "responseData" -> true;
            default -> false;
        };
    }
    
    /**
     * Создает объект атрибутов результата из XML элемента.
     *
     * @param element XML элемент
     * @return объект с атрибутами результата
     */
    public static ExpectedResultAttributes fromElement(Element element) {
        String nodeName = element.getNodeName();
        String text = element.getTextContent().trim();
        
        return switch (nodeName) {
            case "a" -> ExpectedResultAttributes.builder()
                .state(element.getAttribute("state"))
                .botResponse(text)
                .build();
            case "responseData" -> ExpectedResultAttributes.builder()
                .field(element.getAttribute("field"))
                .responseData(text)
                .build();
            default -> null;
        };
    }
    
    /**
     * Преобразует атрибуты в список строк с ожидаемыми результатами.
     *
     * @return список строк с ожидаемыми результатами
     */
    public List<String> toExpectedResults() {
        List<String> results = new ArrayList<>();
        
        if (state != null && !state.isEmpty()) {
            results.add("state = '" + state + "'");
        }
        
        if (botResponse != null && !botResponse.isEmpty()) {
            results.add("Ответ бота:\n" + botResponse);
        }
        
        if (field != null) {
            if ("replies".equals(field)) {
                results.add("Ожидаемое тело:\n" + responseData);
            } else if (responseData == null || responseData.isEmpty()) {
                results.add("Ключ " + field + " не равен NULL/существует в ответе");
            } else {
                results.add("Элемент тела\n " + field + " имеет значение\n" + responseData);
            }
        }
        
        return results;
    }
}
