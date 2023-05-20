package cse.java2.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DataController {
    // 从本地文件系统读取 JSON 文件(路径在src/main/resources下)
    ClassPathResource resource = new ClassPathResource("result.json");
    byte[] fileBytes = Files.readAllBytes(Path.of(resource.getURI()));
    // 使用Jackson库解析JSON数据
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(fileBytes);
    public DataController() throws IOException {
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

    @GetMapping("/AcceptedAnswersPercentage")
    public ResponseEntity<Double> getAcceptedAnswersPercentage() {
        double percentage = jsonNode.get("accepted_answers_percentage").asDouble();
        return ResponseEntity.ok(percentage);
    }

    @GetMapping("/QuestionResolutionTimeDistribution")
    public ResponseEntity<List<Integer>> getQuestionResolutionTimeDistribution() {
        JsonNode resolutionTimeDistributionNode = jsonNode.get("question_resolution_time_distribution");
        List<Integer> resolutionTimeDistribution = new ArrayList<Integer>();
        for (JsonNode element : resolutionTimeDistributionNode) {
            int value = element.asInt();
            resolutionTimeDistribution.add(value);
        }
        return ResponseEntity.ok(resolutionTimeDistribution);
    }

    @GetMapping("/NonAcceptedAnswersUpvotesPercentage")
    public ResponseEntity<Double> getNonAcceptedAnswersUpvotesPercentage() {
        double percentage = jsonNode.get("non_accepted_answers_upvotes_percentage").asDouble();
        return ResponseEntity.ok(percentage);
    }

    @GetMapping("/FrequentlyCooccurringTags")
    public ResponseEntity<Map<String, Integer>> getFrequentlyCooccurringTags() {
        JsonNode cooccurringTagsJsonNode = jsonNode.get("sorted_cooccurrence");
        Map<String, Integer> cooccurringTags = new HashMap<String, Integer>();
        for (JsonNode element : cooccurringTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            cooccurringTags.put(key, value);
        }
        return ResponseEntity.ok(cooccurringTags);
    }

    @GetMapping("/MostUpvotedTags")
    public ResponseEntity<Map<String, Integer>> getMostUpvotedTags() {
        JsonNode upvotedTagsJsonNode = jsonNode.get("sorted_upvotes");
        Map<String, Integer> upvotedTags = new HashMap<String, Integer>();
        for (JsonNode element : upvotedTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            upvotedTags.put(key, value);
        }
        return ResponseEntity.ok(upvotedTags);
    }

    @GetMapping("/MostViewedTags")
    public ResponseEntity<Map<String, Integer>> getMostViewedTags() {
        JsonNode viewedTagsJsonNode = jsonNode.get("sorted_views");
        Map<String, Integer> viewedTags = new HashMap<String, Integer>();
        for (JsonNode element : viewedTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            viewedTags.put(key, value);
        }
        return ResponseEntity.ok(viewedTags);
    }

    @GetMapping("/QuestionUserNumber")
    public ResponseEntity<Integer> getQuestionUserDistribution() {
        int questionUserDistribution = jsonNode.get("number_of_question_users").asInt();
        return ResponseEntity.ok(questionUserDistribution);
    }

    @GetMapping("/AnswerUserNumber")
    public ResponseEntity<Integer> getAnswerUserNumber() {
        int answerUserNumber = jsonNode.get("number_of_answer_users").asInt();
        return ResponseEntity.ok(answerUserNumber);
    }

    @GetMapping("/CommentUserNumber")
    public ResponseEntity<Integer> getCommentUserNumber() {
        int commentUserNumber = jsonNode.get("number_of_comment_users").asInt();
        return ResponseEntity.ok(commentUserNumber);
    }

    @GetMapping("/SingleQuestionUserDistribution")
    public ResponseEntity<List<List<Integer>>> getSingleQuestionUserDistribution() {
        JsonNode data = jsonNode.get("single_question_user_distribution");
        List<List<Integer>> singleQuestionUserDistribution = new ArrayList<>();
        for (JsonNode element : data) {
            List<Integer> list = new ArrayList<>();
            for (JsonNode subElement : element) {
                int value = subElement.asInt();
                list.add(value);
            }
            singleQuestionUserDistribution.add(list);
        }
        return ResponseEntity.ok(singleQuestionUserDistribution);
    }

    @GetMapping("/NumberOfPostsWithoutUserID")
    public ResponseEntity<Integer> getNumberOfPostsWithoutUserID() {
        int numberOfPostsWithoutUserID = jsonNode.get("number_of_posts_without_user_id").asInt();
        return ResponseEntity.ok(numberOfPostsWithoutUserID);
    }

    @GetMapping("/TotalNumberOfUsers")
    public ResponseEntity<Integer> getTotalNumberOfUsers() {
        int totalNumberOfUsers = jsonNode.get("total_number_of_users").asInt();
        return ResponseEntity.ok(totalNumberOfUsers);
    }

    @GetMapping("/MostActiveUsers")
    public ResponseEntity<List<JsonNode>> getMostActiveUsers() throws IOException {
        JsonNode mostActiveUsersJsonNode = jsonNode.get("most_active_users");
        List<JsonNode> mostActiveUsers = new ArrayList<>();
        for (JsonNode element : mostActiveUsersJsonNode) {
            mostActiveUsers.add(element);
        }
        return ResponseEntity.ok(mostActiveUsers);
    }

    @GetMapping("/FrequentlyDiscussedClasses")
    public ResponseEntity<Map<String, Integer>> getFrequentlyDiscussedClasses() {
        JsonNode frequentlyDiscussedClassesJsonNode = jsonNode.get("frequent_classes");
        Map<String, Integer> frequentlyDiscussedClasses = new HashMap<>();
        for (JsonNode element : frequentlyDiscussedClassesJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            frequentlyDiscussedClasses.put(key, value);
        }
        return ResponseEntity.ok(frequentlyDiscussedClasses);
    }

    @GetMapping("/FrequentlyDiscussedMethods")
    public ResponseEntity<Map<List<String>, Integer>> getFrequentlyDiscussedMethods() {
        JsonNode frequentlyDiscussedMethodsJsonNode = jsonNode.get("frequent_methods");
        Map<List<String>, Integer> frequentlyDiscussedMethods = new HashMap<>();
        for (JsonNode element : frequentlyDiscussedMethodsJsonNode) {
            List<String> key = new ArrayList<>();
            key.add(element.get("method").asText());
            key.add(element.get("class").asText());
            int value = element.get("count").asInt();
            frequentlyDiscussedMethods.put(key, value);
        }
        return ResponseEntity.ok(frequentlyDiscussedMethods);
    }



    @GetMapping("/ReputationScoreRelation")
    public ResponseEntity<JsonNode> getReputationScoreRelation() {
        JsonNode reputationScoreRelation = jsonNode.get("reputation_score_relation");
        return ResponseEntity.ok(reputationScoreRelation);
    }

    @GetMapping("/ReputationAcceptanceRelation")
    public ResponseEntity<JsonNode> getReputationAcceptanceRelation() {
        JsonNode reputationAcceptanceRelation = jsonNode.get("reputation_acceptance_relation");
        return ResponseEntity.ok(reputationAcceptanceRelation);
    }



}
