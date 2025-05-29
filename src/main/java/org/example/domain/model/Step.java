package org.example.domain.model;

import lombok.Builder;
import lombok.Data;
import org.example.domain.model.attributes.StepAttributes;

import java.util.List;
import java.util.ArrayList;

/**
 * Модель шага тест-кейса.
 * Представляет отдельный шаг теста с его атрибутами, подшагами
 * и ожидаемыми результатами.
 */
@Data
@Builder
public class Step {
    /** Атрибуты шага */
    private final StepAttributes attributes;
    
    /** Список подшагов */
    @Builder.Default
    private final List<String> subSteps = new ArrayList<>();
    
    /** Список ожидаемых результатов */
    @Builder.Default
    private final List<String> expectedResults = new ArrayList<>();

    /**
     * Проверяет, имеет ли шаг подшаги.
     *
     * @return true если есть подшаги, false в противном случае
     */
    public boolean hasSubSteps() {
        return attributes != null && attributes.hasSubSteps();
    }

    /**
     * Проверяет, имеет ли шаг ожидаемые результаты.
     *
     * @return true если есть ожидаемые результаты, false в противном случае
     */
    public boolean hasExpectedResults() {
        return expectedResults != null && !expectedResults.isEmpty();
    }

    /**
     * Получает описание шага.
     *
     * @return строка с описанием шага или пустая строка
     */
    public String getDescription() {
        return attributes != null ? attributes.getDescription() : "";
    }

    /**
     * Получает последний ожидаемый результат шага.
     *
     * @return строка с последним ожидаемым результатом или null
     */
    public String getLastExpectedResult() {
        if (!hasExpectedResults()) {
            return null;
        }
        return expectedResults.getLast();
    }
}
