package org.example.domain.model;

import lombok.Builder;
import lombok.Data;
import java.util.List;

import org.example.domain.service.JsonFormatter;

/**
 * Модель тест-кейса.
 * Представляет структуру тест-кейса с его метаданными и шагами.
 * Поддерживает преобразование в CSV формат.
 */
@Data
@Builder
public class TestCase {
    /** Имя тест-кейса */
    private final String name;
    
    /** Предусловия тест-кейса */
    private final String precondition;
    
    /** Список шагов тест-кейса */
    private final List<Step> steps;
    
    /** Имя файла, из которого был загружен тест-кейс */
    private final String fileName;

    // Необязательные метаданные
    /** Тег тест-кейса */
    @Builder.Default private String tag = "";
    
    /** Ссылка на связанный ресурс */
    @Builder.Default private String link = "";
    
    /** Дополнительный параметр */
    @Builder.Default private String parameter = "";
    
    /** Ответственный за тест */
    @Builder.Default private String lead = "";
    
    /** Владелец теста */
    @Builder.Default private String owner = "";
    
    /** Набор тестов */
    @Builder.Default private String suite = "";
    
    /** Компонент */
    @Builder.Default private String component = "";
    
    /** История */
    @Builder.Default private String story = "";
    
    /** Функциональность */
    @Builder.Default private String feature = "";
    
    /** Эпик */
    @Builder.Default private String epic = "";

    /**
     * Получает последний ожидаемый результат из всех шагов теста.
     *
     * @return строка с последним ожидаемым результатом или пустая строка
     */
    public String getFinalExpectedResult() {
        if (steps == null || steps.isEmpty()) {
            return "";
        }
        
        // Ищем последний шаг с ожидаемым результатом
        for (int i = steps.size() - 1; i >= 0; i--) {
            Step step = steps.get(i);
            if (step.hasExpectedResults()) {
                return step.getLastExpectedResult();
            }
        }
        return "";
    }

    /**
     * Получает полное имя тест-кейса.
     *
     * @return полное имя тест-кейса с префиксом
     */
    public String getFullName() {
        return "JAICP " + name;
    }

    /**
     * Форматирует шаги тест-кейса в удобочитаемый вид.
     *
     * @return отформатированная строка со всеми шагами
     */
    public String formatSteps() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            
            // Основной шаг
            result.append(String.format("[step %d] %s\n", i + 1, step.getDescription()));
            
            // Подшаги
            if (step.hasSubSteps()) {
                for (int j = 0; j < step.getSubSteps().size(); j++) {
                    result.append(String.format("\t[step %d.%d] %s\n", 
                        i + 1, j + 1, JsonFormatter.formatJson(step.getSubSteps().get(j))));
                }
            }
            
            // Ожидаемые результаты
            if (step.hasExpectedResults()) {
                result.append(String.format("\t[expected %d.1] Expected Result\n", i + 1));
                for (int j = 0; j < step.getExpectedResults().size(); j++) {
                    result.append(String.format("\t\t[expected.step %d.1.%d] %s\n", 
                        i + 1, j + 1, JsonFormatter.formatJson(step.getExpectedResults().get(j))));
                }
            }
        }
        return result.toString().trim();
    }

    /**
     * Получает значение suite с учетом имени файла.
     *
     * @return значение suite или сгенерированное значение на основе имени файла
     */
    private String getSuiteValue() {
        if (suite != null && !suite.isEmpty()) {
            return suite;
        }
        if (fileName != null && !fileName.isEmpty()) {
            int lastDot = fileName.lastIndexOf(".");
            return lastDot > 0 ? "JIACP" + fileName.substring(0, lastDot) : "JIACP" + fileName;
        }
        return "";
    }

    /**
     * Получает значение story с учетом имени файла.
     *
     * @return значение story или сгенерированное значение на основе имени файла
     */
    private String getStoryValue() {
        if (story != null && !story.isEmpty()) {
            return story;
        }
        if (fileName != null && !fileName.isEmpty()) {
            int lastDot = fileName.lastIndexOf(".");
            return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
        }
        return "";
    }

    /**
     * Преобразует тест-кейс в массив строк для CSV.
     *
     * @return массив строк для записи в CSV файл
     */
    public String[] toCsvRow() {
        return new String[]{
                name,                    // name
                getFullName(),           // full_name
                "",                      // description 
                precondition,            // precondition
                getFinalExpectedResult(),// expected_result
                formatSteps(),           // scenario
                tag,                     // tag
                link,                    // link
                parameter,               // parameter
                lead,                    // Lead 
                owner,                   // Owner 
                getSuiteValue(),         // Suite
                component,               // Component
                getStoryValue(),         // Story
                feature,                 // Feature
                epic                     // Epic
        };
    }
}