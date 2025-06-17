package org.example.domain.service;

import org.example.domain.model.Step;
import org.example.domain.model.TestCase;
import org.example.domain.model.attributes.StepAttributes;
import org.example.domain.model.attributes.ExpectedResultAttributes;
import org.example.dto.ConversionParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.w3c.dom.Node;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.*;

/**
 * Сервис для конвертации XML файлов тест-кейсов в формат CSV.
 * Обеспечивает основную логику преобразования XML документов в CSV формат
 * с учетом специфической структуры тест-кейсов.
 */
@Service
@RequiredArgsConstructor
public class XmlToCsvConverter {
    private static final Logger logger = LoggerFactory.getLogger(XmlToCsvConverter.class);
    
    private final XmlParsingService xmlParsingService;

    /**
     * Конвертирует XML строку в CSV формат.
     *
     * @param xmlContent строка с XML содержимым
     * @param params параметры конвертации
     * @return массив байт, представляющий CSV файл
     * @throws Exception при ошибках конвертации
     */
    public byte[] convert(String xmlContent, ConversionParams params) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes())) {
            return convert(inputStream, params);
        }
    }

    /**
     * Конвертирует XML из потока в CSV формат.
     *
     * @param inputStream поток с XML данными
     * @param params параметры конвертации
     * @return массив байт, представляющий CSV файл
     * @throws Exception при ошибках конвертации
     */
    public byte[] convert(InputStream inputStream, ConversionParams params) throws Exception {
        try {
            Document doc = xmlParsingService.parseXml(inputStream);
            List<TestCase> testCases = parseListTestCases(doc, params);
            return writeToCsvBytes(testCases);
        } catch (Exception e) {
            logger.error("Ошибка при конвертации XML", e);
            throw e;
        }
    }

    /**
     * Парсит список тест-кейсов из XML документа.
     *
     * @param doc XML документ
     * @param params параметры конвертации
     * @return список тест-кейсов
     */
    private List<TestCase> parseListTestCases(Document doc, ConversionParams params) {
        List<TestCase> testCases = new ArrayList<>();
        NodeList testCaseNodes = doc.getElementsByTagName("test-case");
        
        for (int i = 0; i < testCaseNodes.getLength(); i++) {
            Element testCaseElement = (Element) testCaseNodes.item(i);
            testCases.add(parseTestCase(testCaseElement, params));
        }
        
        return testCases;
    }

    /**
     * Парсит отдельный тест-кейс из XML элемента.
     *
     * @param testCaseElement XML элемент тест-кейса
     * @param params параметры конвертации
     * @return объект тест-кейса
     */
    private TestCase parseTestCase(Element testCaseElement, ConversionParams params) {
        String name = testCaseElement.getAttribute("id");
        String precondition = processMockData(testCaseElement);
        List<Step> steps = parseSteps(testCaseElement);

        return TestCase.builder()
            .name(name)
            .precondition(precondition)
            .steps(steps)
            .tag(params.getTag())
            .link(params.getLink())
            .parameter(params.getParameter())
            .suite(params.getSuite())
            .component(params.getComponent())
            .story(params.getStory())
            .feature(params.getFeature())
            .epic(params.getEpic())
            .fileName(params.getFileName())
            .build();
    }

    /**
     * Парсит шаги тест-кейса.
     *
     * @param testCaseElement XML элемент тест-кейса
     * @return список шагов тест-кейса
     */
    private List<Step> parseSteps(Element testCaseElement) {
        List<Step> steps = new ArrayList<>();
        Step.StepBuilder currentStep = null;
        List<String> currentExpectedResults = new ArrayList<>();
        
        NodeList nodes = testCaseElement.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            
            String nodeName = node.getNodeName();
            String text = node.getTextContent().trim();
            Element elem = (Element) node;

            if (StepAttributes.isStepNode(nodeName)) {
                // Если есть предыдущий шаг, завершаем его
                if (currentStep != null) {
                    currentStep.expectedResults(currentExpectedResults);
                    steps.add(currentStep.build());
                }
                // Начинаем новый шаг
                StepAttributes attributes = StepAttributes.fromNodeName(nodeName, text);
                currentStep = Step.builder().attributes(attributes);
                if (attributes.hasSubSteps()) {
                    currentStep.subSteps(List.of(attributes.getSubSteps()));
                }
                currentExpectedResults = new ArrayList<>();
            } else if (ExpectedResultAttributes.isResultNode(nodeName) && currentStep != null) {
                // Добавляем новые ожидаемые результаты к текущему шагу
                ExpectedResultAttributes resultAttributes = ExpectedResultAttributes.fromElement(elem);
                currentExpectedResults.addAll(resultAttributes.toExpectedResults());
            }
        }
        
        // Завершаем последний шаг, если он есть
        if (currentStep != null) {
            currentStep.expectedResults(currentExpectedResults);
            steps.add(currentStep.build());
        }

        return steps;
    }



    /**
     * Обрабатывает данные моков из тест-кейса.
     *
     * @param testCase XML элемент тест-кейса
     * @return строка с форматированными данными моков
     */
    private String processMockData(Element testCase) {
        return Optional.of(testCase)
            .map(tc -> tc.getElementsByTagName("mockData"))
            .map(mockDataList -> IntStream.range(0, mockDataList.getLength())
                .mapToObj(i -> processSingleMock((Element) mockDataList.item(i), i + 1))
                .collect(Collectors.joining("\n")))
            .orElse("");
    }

    /**
     * Обрабатывает отдельный мок из данных тест-кейса.
     *
     * @param mockData XML элемент с данными мока
     * @param index порядковый номер мока
     * @return строка с форматированными данными мока
     */
    private String processSingleMock(Element mockData, int index) {
        Element query = (Element) mockData.getElementsByTagName("query").item(0);
        Element response = (Element) mockData.getElementsByTagName("response").item(0);
        
        // Собираем все параметры в Map
        Map<String, String> parameters = Optional.ofNullable(mockData.getElementsByTagName("parameters").item(0))
            .map(Element.class::cast)
            .map(params -> StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new NodeListIterator(params.getChildNodes()),
                    Spliterator.ORDERED
                ),
                false
            )
            .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
            .map(Element.class::cast)
            .collect(Collectors.toMap(
                Element::getNodeName,
                elem -> elem.getTextContent().trim()
            )))
            .orElse(new HashMap<>());

        String method = query.getAttribute("method").toUpperCase();
        String url = query.getTextContent().trim();
        String statusCode = response.getAttribute("status");
        String responseBody = JsonFormatter.formatJson(response.getTextContent().trim());

        // Подставляем параметры в URL 
        String processedUrl = url;        
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String paramPlaceholder = "${" + entry.getKey() + "}";
            if (url.contains(paramPlaceholder)) {
                // Если параметр используется в URL как placeholder
                processedUrl = processedUrl.replace(paramPlaceholder, entry.getValue());
            } else {
                // Если параметр не используется в URL, добавляем его как query parameter
                String separator = processedUrl.contains("?") ? "&" : "?";
                processedUrl = processedUrl + separator + entry.getKey() + "=" + entry.getValue();
            }
        }

        return String.format("%d. **%s** %s\n**Статус:** %s \n**Ответ:** \n`%s`\n",
            index,
            method,
            processedUrl,
            statusCode,
            responseBody
        );
    }

    /**
     * Записывает тест-кейсы в CSV формат.
     *
     * @param testCases список тест-кейсов
     * @return массив байт, представляющий CSV файл
     * @throws IOException при ошибках записи
     */
    private byte[] writeToCsvBytes(List<TestCase> testCases) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);
        
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            writer.write("name;full_name;description;precondition;expected_result;scenario;tag;link;parameter;Lead;Owner;Suite;Component;Story;Feature;Epic");
            writer.newLine();

            for (TestCase testCase : testCases) {
                writer.write(formatCsvRow(testCase.toCsvRow()));
                writer.newLine();
            }
            writer.flush();
        }
        return out.toByteArray();
    }

    /**
     * Форматирует массив строк в строку CSV формата.
     *
     * @param row массив значений для строки CSV
     * @return форматированная строка CSV
     */
    private String formatCsvRow(String[] row) {
        return Arrays.stream(row)
            .map(value -> {
                if (value == null) return "";
                if (value.contains(";") || value.contains("\n") || value.contains("\"")) {
                    String processed = value.replace("\"", "\"\"");
                    return "\"" + processed + "\"";
                }
                return value;
            })
            .collect(Collectors.joining(";"));
    }

    /**
     * Итератор для удобной работы со списком узлов XML.
     */
    private static class NodeListIterator implements Iterator<Node> {
        private final NodeList nodeList;
        private int index;
        
        public NodeListIterator(NodeList nodeList) {
            this.nodeList = nodeList;
            this.index = 0;
        }
        
        @Override
        public boolean hasNext() {
            return index < nodeList.getLength();
        }
        
        @Override
        public Node next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return nodeList.item(index++);
        }
    }
}