package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.domain.service.XmlToCsvConverter;
import org.example.dto.ConversionParams;
import org.example.dto.request.ConversionRequest;
import org.example.exception.ConversionException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

/**
 * REST контроллер для конвертации XML файлов в CSV формат.
 * Предоставляет два основных эндпоинта:
 * <ul>
 *     <li>/api/convert/file - для конвертации XML файла</li>
 *     <li>/api/convert/xml - для конвертации XML строки</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/convert")
@Tag(name = "XML to CSV Converter")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ConvertController {
    private final XmlToCsvConverter converter;

    /**
     * Конвертирует XML файл в CSV формат.
     *
     * @param file XML файл для конвертации
     * @param tag дополнительный тег для маркировки теста
     * @param link ссылка на связанный ресурс
     * @param parameter дополнительный параметр
     * @param lead ответственный за тест
     * @param owner владелец теста
     * @param suite набор тестов
     * @param component компонент, к которому относится тест
     * @param story пользовательская история
     * @param feature функциональность
     * @param epic эпик, к которому относится тест
     * @return ResponseEntity с CSV файлом
     * @throws ConversionException если произошла ошибка при конвертации
     */
    @Operation(
        summary = "Конвертирует XML файл в CSV",
        description = "Принимает XML файл и возвращает CSV файл"
    )
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> convertFile(
        @Parameter(description = "XML файл") @RequestPart("file") MultipartFile file,
        @Parameter(description = "Тег") @RequestParam(required = false) String tag,
        @Parameter(description = "Ссылка") @RequestParam(required = false) String link,
        @Parameter(description = "Параметр") @RequestParam(required = false) String parameter,
        @Parameter(description = "Ответственный") @RequestParam(required = false) String lead,
        @Parameter(description = "Владелец") @RequestParam(required = false) String owner,
        @Parameter(description = "Набор тестов") @RequestParam(required = false) String suite,
        @Parameter(description = "Компонент") @RequestParam(required = false) String component,
        @Parameter(description = "История") @RequestParam(required = false) String story,
        @Parameter(description = "Функциональность") @RequestParam(required = false) String feature,
        @Parameter(description = "Эпик") @RequestParam(required = false, defaultValue = "JAICP") String epic
    ) {
        try {
            String xmlContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            String originalFileName = file.getOriginalFilename();

            ConversionParams params = buildParams(tag, link, parameter, lead, owner, suite, component, story, feature, epic, originalFileName);
            byte[] csvContent = converter.convert(xmlContent, params);
            
            return createCsvResponse(csvContent, generateOutputFileName(originalFileName));
        } catch (Exception e) {
            log.error("Ошибка при конвертации XML файла", e);
            throw new ConversionException("Ошибка при конвертации файла: " + e.getMessage());
        }
    }

    /**
     * Конвертирует XML строку в CSV формат.
     *
     * @param xmlRequest объект с XML строкой
     * @param tag дополнительный тег для маркировки теста
     * @param link ссылка на связанный ресурс
     * @param parameter дополнительный параметр
     * @param lead ответственный за тест
     * @param owner владелец теста
     * @param suite набор тестов
     * @param component компонент, к которому относится тест
     * @param story пользовательская история
     * @param feature функциональность
     * @param epic эпик, к которому относится тест
     * @return ResponseEntity с CSV файлом
     * @throws ConversionException если произошла ошибка при конвертации
     */
    @Operation(
        summary = "Конвертирует XML строку в CSV",
        description = "Принимает XML в теле запроса и возвращает CSV файл"
    )
    @PostMapping(value = "/xml", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<Resource> convertXml(
        @Parameter(description = "XML в теле запроса") @RequestBody ConversionRequest xmlRequest,
        @Parameter(description = "Тег") @RequestParam(required = false) String tag,
        @Parameter(description = "Ссылка") @RequestParam(required = false) String link,
        @Parameter(description = "Параметр") @RequestParam(required = false) String parameter,
        @Parameter(description = "Ответственный") @RequestParam(required = false) String lead,
        @Parameter(description = "Владелец") @RequestParam(required = false) String owner,
        @Parameter(description = "Набор тестов") @RequestParam(required = false) String suite,
        @Parameter(description = "Компонент") @RequestParam(required = false) String component,
        @Parameter(description = "История") @RequestParam(required = false) String story,
        @Parameter(description = "Функциональность") @RequestParam(required = false) String feature,
        @Parameter(description = "Эпик") @RequestParam(required = false, defaultValue = "JAICP") String epic
    ) {
        try {
            ConversionParams params = buildParams(tag, link, parameter, lead, owner, suite, component, story, feature, epic, null);
            byte[] csvContent = converter.convert(xmlRequest.getXmlContent(), params);
            
            return createCsvResponse(csvContent, "conversion-result.csv");
        } catch (Exception e) {
            log.error("Ошибка при конвертации XML", e);
            throw new ConversionException("Ошибка при конвертации: " + e.getMessage());
        }
    }

    /**
     * Создает параметры конвертации на основе входных данных.
     *
     * @param tag тег
     * @param link ссылка
     * @param parameter параметр
     * @param lead ответственный
     * @param owner владелец
     * @param suite набор тестов
     * @param component компонент
     * @param story история
     * @param feature функциональность
     * @param epic эпик
     * @param fileName имя файла
     * @return объект с параметрами конвертации
     */
    private ConversionParams buildParams(String tag, String link, String parameter, 
                                      String lead, String owner, String suite, String component, 
                                      String story, String feature, String epic, String fileName) {
        return ConversionParams.builder()
                .tag(tag)
                .link(link)
                .parameter(parameter)
                .lead(lead)
                .owner(owner)
                .suite(suite)
                .component(component)
                .story(story)
                .feature(feature)
                .epic(epic)
                .fileName(fileName)
                .build();
    }

    /**
     * Генерирует имя выходного CSV файла на основе имени входного файла.
     *
     * @param originalFileName оригинальное имя файла
     * @return имя выходного файла
     */
    private String generateOutputFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return "conversion-result.csv";
        }
        
        String baseName = originalFileName;
        int extIndex = originalFileName.lastIndexOf('.');
        if (extIndex > 0) {
            baseName = originalFileName.substring(0, extIndex);
        }
        
        return baseName + ".csv";
    }

    /**
     * Создает HTTP ответ с CSV файлом.
     *
     * @param content содержимое CSV файла
     * @param filename имя файла
     * @return ResponseEntity с файлом
     */
    private ResponseEntity<Resource> createCsvResponse(byte[] content, String filename) {
        ByteArrayResource resource = new ByteArrayResource(content);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                    ContentDisposition.attachment()
                        .filename(filename, StandardCharsets.UTF_8)
                        .build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(content.length)
                .body(resource);
    }
}