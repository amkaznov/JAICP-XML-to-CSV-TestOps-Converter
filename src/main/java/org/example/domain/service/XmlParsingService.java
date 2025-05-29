package org.example.domain.service;

import org.example.domain.model.attributes.StepAttributes;
import org.example.exception.XmlValidationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для парсинга и валидации XML документов.
 * Обеспечивает корректную загрузку и проверку структуры XML файлов тест-кейсов.
 */
@Service
public class XmlParsingService {

    /**
     * Парсит XML из входного потока и выполняет валидацию документа.
     *
     * @param inputStream поток с XML данными
     * @return распарсенный XML документ
     * @throws Exception при ошибках парсинга или валидации
     */
    public Document parseXml(InputStream inputStream) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(inputStream);
        validateDocument(doc);
        return doc;
    }

    /**
     * Проверяет корректность структуры XML документа.
     * Проверяет наличие корневого элемента test и элементов test-case.
     *
     * @param doc XML документ для валидации
     * @throws XmlValidationException если документ не соответствует ожидаемой структуре
     */
    private void validateDocument(Document doc) {
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        
        // Проверка корневого элемента
        if (!"test".equals(root.getNodeName())) {
            throw new XmlValidationException("Некорректный XML: ожидается корневой элемент <test>, но найден <" + root.getNodeName() + ">");
        }

        // Проверка наличия test-case элементов
        NodeList testCases = root.getElementsByTagName("test-case");
        if (testCases.getLength() == 0) {
            throw new XmlValidationException("XML не содержит тест-кейсов (<test-case>)");
        }

        // Валидация каждого тест-кейса
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < testCases.getLength(); i++) {
            Element testCase = (Element) testCases.item(i);
            validateTestCase(testCase, errors);
        }

        if (!errors.isEmpty()) {
            throw new XmlValidationException("Обнаружены ошибки в XML:\n" + String.join("\n", errors));
        }
    }

    /**
     * Проверяет корректность отдельного тест-кейса.
     * Проверяет наличие ID и шагов в тест-кейсе.
     *
     * @param testCase элемент тест-кейса для валидации
     * @param errors список для накопления ошибок
     */
    private void validateTestCase(Element testCase, List<String> errors) {
        String id = testCase.getAttribute("id");
        if (id.isEmpty()) {
            errors.add("Test case без ID");
        }

        NodeList children = testCase.getChildNodes();
        boolean hasValidContent = false;
        
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) continue;
            
            String nodeName = node.getNodeName();
            if (StepAttributes.isStepNode(nodeName)) {
                hasValidContent = true;
                break;
            }
        }

        if (!hasValidContent) {
            errors.add("Test case " + id + " не содержит шагов");
        }
    }
}
