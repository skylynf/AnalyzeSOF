package collecting;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.*;

public class contextAnalyze {

    private static final String API_URL = "https://api.stackexchange.com/2.3/";

    public static void main(String[] args) {
        JSONArray questions;
        JSONArray answers;
        JSONArray comments;

        try {
            // Load JSON data from files
            File questionsFile = new File("D:\\CS209\\Project\\AnalyzeSOF\\src\\main\\java\\collecting\\questions.json");
            File answersFile = new File("D:\\CS209\\Project\\AnalyzeSOF\\src\\main\\java\\collecting\\answers.json");
            File commentsFile = new File("D:\\CS209\\Project\\AnalyzeSOF\\src\\main\\java\\collecting\\comments.json");

            questions = loadJSONArrayFromFile(questionsFile);
            answers = loadJSONArrayFromFile(answersFile);
            comments = loadJSONArrayFromFile(commentsFile);

            List<Integer> questionIds = extractIds(questions, "question_id");
            List<Integer> answerIds = extractIds(answers, "answer_id");
            List<Integer> commentIds = extractIds(comments, "comment_id");

            List<Integer> allIds = new ArrayList<>();
            allIds.addAll(questionIds);
            allIds.addAll(answerIds);

            List<JSONObject> allPosts = getPostContent(allIds);
            List<JSONObject> allComments = getCommentContent(commentIds);

            List<String> allBodies = new ArrayList<>();
            for (JSONObject post : allPosts) {
                allBodies.add(post.getString("body"));
            }
            for (JSONObject comment : allComments) {
                allBodies.add(comment.getString("body"));
            }

            saveJSONArrayToFile(new JSONArray(allBodies), "all_bodies.json");

            List<String> apiMentionsClasses = new ArrayList<>();
            List<String> apiMentionsMethods = new ArrayList<>();

            for (String body : allBodies) {
                Set<String> classMethodCombinations = extractJavaAPIs(body);
                for (String combination : classMethodCombinations) {
                    String[] parts = combination.split("\\.");
                    if (parts.length == 2) {
                        apiMentionsClasses.add(parts[0]);
                        apiMentionsMethods.add(combination);
                    }
                }
            }

            Map<String, Integer> frequentClasses = getFrequentItems(apiMentionsClasses, 10);
            Map<String, Integer> frequentMethods = getFrequentItems(apiMentionsMethods, 10);

            System.out.println("Frequently discussed classes:");
            for (Map.Entry<String, Integer> entry : frequentClasses.entrySet()) {
                System.out.println(entry.getKey() + " (" + entry.getValue() + ")");
            }
            System.out.println();

            System.out.println("Frequently discussed methods:");
            for (Map.Entry<String, Integer> entry : frequentMethods.entrySet()) {
                String[] parts = entry.getKey().split("\\.");
                if (parts.length == 2) {
                    System.out.println(parts[1] + " in " + parts[0] + " (" + entry.getValue() + ")");
                }
            }

            JSONObject result = new JSONObject();
            result.put("frequent_classes", frequentClasses);
            result.put("frequent_methods", frequentMethods);

            saveJsonObjectToFile(result, "result.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static JSONArray loadJSONArrayFromFile(File file) throws IOException, JSONException {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line);
            }
        }
        return new JSONArray(fileContents.toString());
    }

    private static void saveJSONArrayToFile(JSONArray jsonArray, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(jsonArray.toString());
        }
    }

    private static void saveJsonObjectToFile(JSONObject jsonObject, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(jsonObject.toString());
        }
    }

    private static List<Integer> extractIds(JSONArray jsonArray, String key) throws JSONException {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            ids.add(item.getInt(key));
        }
        return ids;
    }

    private static List<JSONObject> getPostContent(List<Integer> ids) throws IOException, JSONException {
        List<JSONObject> posts = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 30) {
            System.out.println("Processing batch " + (i / 30 + 1) + "/" + (ids.size() / 30 + 1));

            List<Integer> batchIds = ids.subList(i, Math.min(i + 30, ids.size()));
            String postIdsStr = batchIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));
            String endpoint = "posts/" + postIdsStr;
            String params = "filter=withbody";
            JSONObject response = makeAPIRequest(endpoint, params);

            int quotaRemaining = response.getInt("quota_remaining");
            System.out.println("Remaining quota: " + quotaRemaining);

            saveJsonObjectToFile(response, "response.json");
            JSONArray items = response.getJSONArray("items");
            for (int j = 0; j < items.length(); j++) {
                posts.add(items.getJSONObject(j));
            }
        }
        return posts;
    }

    private static List<JSONObject> getCommentContent(List<Integer> ids) throws IOException, JSONException {
        List<JSONObject> comments = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += 30) {
            System.out.println("Processing batch " + (i / 30 + 1) + "/" + (ids.size() / 30 + 1));

            List<Integer> batchIds = ids.subList(i, Math.min(i + 30, ids.size()));
            String commentIdsStr = batchIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(";"));
            String endpoint = "comments/" + commentIdsStr;
            String params = "filter=withbody";
            JSONObject response = makeAPIRequest(endpoint, params);

            int quotaRemaining = response.getInt("quota_remaining");
            System.out.println("Remaining quota: " + quotaRemaining);

            JSONArray items = response.getJSONArray("items");
            for (int j = 0; j < items.length(); j++) {
                comments.add(items.getJSONObject(j));
            }
        }
        return comments;
    }

    private static JSONObject makeAPIRequest(String endpoint, String params) throws IOException, JSONException {
        URL url = new URL(API_URL + endpoint + "?" + params + "&site=stackoverflow");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Accept", "application/json");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        System.out.println("Response: " + Arrays.toString(response.toString().getBytes(StandardCharsets.UTF_8)));
        return new JSONObject(response.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static Set<String> extractJavaAPIs(String text) {
        Set<String> combinations = new HashSet<>();
        String pattern = "\\b([A-Z]\\w+)\\.([A-Z]\\w+)\\b";
        Matcher matcher = Pattern.compile(pattern).matcher(text);
        while (matcher.find()) {
            String combination = matcher.group(1) + "." + matcher.group(2);
            combinations.add(combination);
        }
        return combinations;
    }

    private static Map<String, Integer> getFrequentItems(List<String> items, int limit) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String item : items) {
            frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
        }
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        frequencyMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), entry.getValue()));
        return sortedMap;
    }
}


