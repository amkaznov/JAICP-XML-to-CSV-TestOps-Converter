package org.example.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Класс, содержащий параметры для конвертации XML в CSV.
 * Хранит метаданные и дополнительную информацию для процесса конвертации.
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
public class ConversionParams {
    /** Тег для маркировки теста */
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
    
    /** Компонент, к которому относится тест */
    @Builder.Default private String component = "";
    
    /** История пользователя */
    @Builder.Default private String story = "";
    
    /** Функциональность */
    @Builder.Default private String feature = "";
    
    /** Эпик */
    @Builder.Default private String epic = "";
    
    /** Имя исходного файла */
    private String fileName;
}