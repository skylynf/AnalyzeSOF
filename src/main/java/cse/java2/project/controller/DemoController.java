package cse.java2.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class DemoController {
    // 从本地文件系统读取 JSON 文件(路径在src/main/resources下)
    ClassPathResource resource = new ClassPathResource("result.json");
    byte[] fileBytes = Files.readAllBytes(Path.of(resource.getURI()));
    // 使用Jackson库解析JSON数据
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(fileBytes);
    public DemoController() throws IOException {
    }

    /**
     * This method is called when the user requests the root URL ("/") or "/demo".
     * In this demo, you can visit localhost:9090 or localhost:9090/demo to see the result.
     * @return the name of the view to be rendered
     * You can find the static HTML file in src/main/resources/templates/demo.html
     */
    @GetMapping({"/", "/demo"})
    public String demo() {
        return "demo";
    }


    /**
     * This method is called when the user requests the JSON file ("/result").
     * It reads the JSON file from the local filesystem and sends it as a response.
     * @return the JSON file as a response entity
     * @throws IOException if there is an error reading the file
     */
    @GetMapping("/result")
    // 前端爆栈
    public ResponseEntity<byte[]> getFile() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "result.json");
        return ResponseEntity.ok().headers(headers).body(fileBytes);
    }

    @GetMapping("/PercentageWithoutAnswers")
    public ResponseEntity<Double> getPercentageWithoutAnswers() throws IOException {
        double percentageWithoutAnswers = jsonNode.get("percentage_without_answers").asDouble();
        return ResponseEntity.ok(percentageWithoutAnswers);
    }
    @GetMapping("/AverageAnswers")
    public ResponseEntity<Double> getAverageAnswers() throws IOException {
        double averageAnswers = jsonNode.get("average_answers").asDouble();
        return ResponseEntity.ok(averageAnswers);
    }
    @GetMapping("/MaxAnswers")
    public ResponseEntity<Integer> getMaxAnswers() throws IOException {
        Integer maxAnswers = jsonNode.get("max_answers").asInt();
        return ResponseEntity.ok(maxAnswers);
    }
    @GetMapping("/AnswerDistribution")
    public ResponseEntity<JsonNode> getAnswerDistribution() throws IOException {
        JsonNode answerDistributionNode = jsonNode.get("answer_distribution");
        return ResponseEntity.ok(answerDistributionNode);
    }




}
