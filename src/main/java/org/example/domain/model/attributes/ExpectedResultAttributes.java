package org.example.domain.model.attributes;

import lombok.Data;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

/**
 * Атрибуты ожидаемого результата шага тест-кейса.
 * Содержит список ожидаемых результатов для одного шага.
 * Поддерживает множественные проверки responseData и комбинацию state+botResponse.
 * Каждый responseData сохраняется как отдельный результат.
 */
@Data
public class ExpectedResultAttributes {
    /** Список ожидаемых результатов */
    private final List<ExpectedResult> results = new ArrayList<>();
    
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
        
        ExpectedResultAttributes attributes = new ExpectedResultAttributes();
        ExpectedResult result = switch (nodeName) {
            case "a" -> ExpectedResult.builder()
                .state(element.getAttribute("state"))
                .botResponse(text)
                .build();
            case "responseData" -> ExpectedResult.builder()
                .field(element.getAttribute("field"))
                .fieldValue(text)
                .build();
            default -> null;
        };
        
        if (result != null) {
            attributes.results.add(result);
        }
        
        return attributes;
    }
    
    /**
     * Преобразует атрибуты в список строк с ожидаемыми результатами.
     *
     * @return список строк с ожидаемыми результатами
     */
    public List<String> toExpectedResults() {
        return results.stream()
            .map(ExpectedResult::format)
            .toList();
    }
}
