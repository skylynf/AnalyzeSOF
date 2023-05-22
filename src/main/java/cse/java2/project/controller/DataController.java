package cse.java2.project.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
     *
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
        double percentage = jsonNode.get("percentage_have_accepted_answer").asDouble();
        return ResponseEntity.ok(percentage);
    }

    @GetMapping("/QuestionResolutionTimeDistribution")
    public ResponseEntity<List<List<Object>>> getQuestionResolutionTimeDistribution() {
        JsonNode resolutionTimeDistributionNode = jsonNode.get("resolution_time_distribution");
        List<Integer> resolutionTimeDistribution = new ArrayList<>();
        for (JsonNode element : resolutionTimeDistributionNode) {
            int value = element.asInt();
            resolutionTimeDistribution.add(value);
        }
        // sort the list from small to big
        resolutionTimeDistribution.sort(Integer::compareTo);
        int max = resolutionTimeDistribution.get(resolutionTimeDistribution.size() - 1);
        int[] standard = {60, 120, 180, 360, 900, 1800, 3600, 7200,
                10800, 21600, 43200, 86400, 172800, 345600, 604800,
                1209600, 2419200, max};
        List<String> standardList = new ArrayList<>();
        standardList.add("1min");
        standardList.add("2min");
        standardList.add("3min");
        standardList.add("6min");
        standardList.add("15min");
        standardList.add("30min");
        standardList.add("1h");
        standardList.add("2h");
        standardList.add("3h");
        standardList.add("6h");
        standardList.add("12h");
        standardList.add("1d");
        standardList.add("2d");
        standardList.add("4d");
        standardList.add("1w");
        standardList.add("2w");
        standardList.add("4w");
        standardList.add("4w+");

        int[] count = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int now = 0;
        for (Integer integer : resolutionTimeDistribution) {
            if (integer > standard[now]) {
                while (integer > standard[now]){
                    now++;
                }
                count[now]++;
            } else {
                count[now]++;
            }
        }
        List<List<Object>> returnList = new ArrayList<>();
        for (int i = 0; i < standard.length; i++) {
            List<Object> adder = new ArrayList<>();
            adder.add(standardList.get(i));
            adder.add(count[i]);
            returnList.add(adder);
        }

        return ResponseEntity.ok().body(returnList);
    }

    @GetMapping("/NonAcceptedAnswersUpvotesPercentage")
    public ResponseEntity<Double> getNonAcceptedAnswersUpvotesPercentage() {
        double percentage = jsonNode.get("percentage_better_than_accepted").asDouble();
        return ResponseEntity.ok(percentage);
    }

    @GetMapping("/FrequentlyCooccurringTags")
    public ResponseEntity<List<Map<String, Object>>> getFrequentlyCooccurringTags() {
        JsonNode cooccurringTagsJsonNode = jsonNode.get("sorted_cooccurrence");
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> cooccurringTags = new HashMap<String, Integer>();
        for (JsonNode element : cooccurringTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            cooccurringTags.put(key, value);
            Map<String, Object> adder = new HashMap<>();
            adder.put("value", value);
            adder.put("name", key);
            result.add(adder);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/MostUpvotedTags")
    public ResponseEntity<List<Map<String, Object>>> getMostUpvotedTags() {
        JsonNode upvotedTagsJsonNode = jsonNode.get("sorted_upvotes");
        Map<String, Integer> upvotedTags = new HashMap<String, Integer>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode element : upvotedTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            upvotedTags.put(key, value);
            Map<String, Object> adder = new HashMap<>();
            adder.put("value", value);
            adder.put("name", key);
            result.add(adder);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/MostViewedTags")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedTags() {
        JsonNode viewedTagsJsonNode = jsonNode.get("sorted_views");
        Map<String, Integer> viewedTags = new HashMap<String, Integer>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode element : viewedTagsJsonNode) {
            String key = element.get(0).toString().replace("\"", "");
            int value = element.get(1).asInt();
            viewedTags.put(key, value);
            Map<String, Object> adder = new HashMap<>();
            adder.put("value", value);
            adder.put("name", key);
            result.add(adder);
//            System.out.println(adder);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/QuestionUserNumber") //参与问题的
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
    public ResponseEntity<List<List<Integer>>> getSingleQuestionUserDistribution(
            @RequestParam(value = "num", required = false, defaultValue = "1000") int num
    ) {
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
        num = Math.min(num, singleQuestionUserDistribution.size());
        return ResponseEntity.ok(singleQuestionUserDistribution.subList(0, num));
    }

    @GetMapping("/SingleQuestionUserDistributionGraph")
    public ResponseEntity<List<List<Object>>> getSingleQuestionUserDistributionGraph() {
        JsonNode data = jsonNode.get("single_question_user_distribution");
        List<List<Object>> singleQuestionUserDistribution = new ArrayList<>();
        int[] countList = new int[5];

        for (JsonNode element : data) {

            float rate;
            if (element.get(0).asInt() + element.get(1).asInt() == 0){
                continue;
            }
            rate = (float) element.get(0).asInt() / (element.get(0).asInt() + element.get(1).asInt());

            if (rate == 0){
                countList[0] = countList[0] + 1;
            } else if (rate == 1){
                countList[4] = countList[4] + 1;
            } else if (rate == 0.5) {
                countList[2] = countList[2] + 1;
            } else if (rate < 0.5) {
                countList[1] = countList[1] + 1;
            } else if (rate > 0.5){
                countList[3] = countList[3] + 1;
            }
        }
        for (int i = 0; i < 5; i++){
            List<Object> list = new ArrayList<>();
            if (i == 0){
                list.add("Only Comment");
            } else if (i == 1){
                list.add("<0.5");
            } else if (i == 2){
                list.add("0.5");
            } else if (i == 3){
                list.add(">0.5");
            } else {
                list.add("Only Answer");
            }
            list.add(countList[i]);
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
    public ResponseEntity<List<Map<String, Object>>> getFrequentlyDiscussedClasses() {
        JsonNode frequentlyDiscussedClassesJsonNode = jsonNode.get("frequent_classes");
        Map<String, Integer> frequentlyDiscussedClasses = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode element : frequentlyDiscussedClassesJsonNode) {
            String key = element.get("name").asText();
            int value = element.get("count").asInt();
            frequentlyDiscussedClasses.put(key, value);
            Map<String, Object> adder = new HashMap<>();
            adder.put("value", value);
            adder.put("name", key);
            result.add(adder);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/FrequentlyDiscussedMethods")
    public ResponseEntity<List<Map<String, Object>>> getFrequentlyDiscussedMethods() {
        JsonNode frequentlyDiscussedMethodsJsonNode = jsonNode.get("frequent_methods");
        Map<List<String>, Integer> frequentlyDiscussedMethods = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (JsonNode element : frequentlyDiscussedMethodsJsonNode) {
            List<String> key = new ArrayList<>();
            key.add(element.get("method").asText());
            key.add(element.get("class").asText());
            int value = element.get("count").asInt();
            frequentlyDiscussedMethods.put(key, value);
            Map<String, Object> adder = new HashMap<>();
            adder.put("value", value);
            adder.put("name", key);
            result.add(adder);
        }
        return ResponseEntity.ok(result);
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

    //RESTful API to get user's total score
    @GetMapping("/UserTotalScore/{UserId}")
    public ResponseEntity<Integer> getUserTotalScore(@PathVariable("UserId") int userId) {
        JsonNode userJson = jsonNode.get("reputation_score_relation").get(String.valueOf(userId));
        int score = 0;
        for (JsonNode element : userJson) {
            score = element.asInt();
        }
        return ResponseEntity.ok(score);
    }



}
