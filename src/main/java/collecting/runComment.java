//package collecting;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//public class runComment {
//
//    // Enter your API key here
//    private static final String API_KEY = "YOUR_API_KEY";
//
//    // Function to retrieve comments for a list of post IDs
//    private static List<JsonObject> getComments(List<Integer> postIds) throws IOException {
//        List<JsonObject> comments = new ArrayList<>();
//        String site = "stackoverflow";
//        for (int postId : postIds) {
//            String url = "https://api.stackexchange.com/2.3/posts/" + postId + "/comments?site=" + site;
//            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Accept", "application/json");
//
//            if (connection.getResponseCode() == 200) {
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inputStream);
//                JsonElement element = JsonParser.parseReader(reader);
//                JsonObject data = element.getAsJsonObject();
//                JsonArray items = data.getAsJsonArray("items");
//                for (JsonElement item : items) {
//                    comments.add(item.getAsJsonObject());
//                }
//            }
//        }
//        return comments;
//    }
//
//    public static void main(String[] args) throws IOException {
//        // Load question.json and answer.json
//        JsonArray questionData = loadJsonArray("questions.json");
//        JsonArray answerData = loadJsonArray("answers.json");
//
//        // Extract post IDs from question.json and answer.json
//        List<Integer> questionIds = extractPostIds(questionData);
//        List<Integer> answerIds = extractPostIds(answerData);
//
//        // Combine question IDs and answer IDs
//        List<Integer> postIds = new ArrayList<>(questionIds);
//        postIds.addAll(answerIds);
//
//        // Retrieve comments in batches of 100 post IDs
//        int batchSize = 100;
//        int numBatches = (postIds.size() - 1) / batchSize + 1;
//        List<JsonObject> allComments = new ArrayList<>();
//
//        for (int i = 0; i < numBatches; i++) {
//            System.out.printf("Processing batch %d/%d%n", i + 1, numBatches);
//            int start = i * batchSize;
//            int end = Math.min(start + batchSize, postIds.size());
//            List<Integer> batchIds = postIds.subList(start, end);
//            List<JsonObject> comments = getComments(batchIds);
//            allComments.addAll(comments);
//        }
//
//        // Save comments to a file
//        try (FileWriter fileWriter = new FileWriter("comments.json")) {
//            fileWriter.write(allComments.toString());
//        }
//    }
//
//    private static JsonArray loadJsonArray(String filename) throws IOException {
//        // Load the JSON file into a JsonArray
//        // Implement this method according to your preferred JSON library
//        // Example using Gson:
//        // Gson gson = new Gson();
//        // JsonArray jsonArray = gson.fromJson(new FileReader(filename), JsonArray.class);
//        // return jsonArray;
//        return null;
//    }
//
//    private static List<Integer> extractPostIds(JsonArray data) {
//        List<Integer> postIds = new ArrayList<>();
//        for (JsonElement element : data) {
//            JsonObject post = element.getAsJsonObject();
//            int postId = post.get("question_id").getAsInt();
//            postIds.add(postId);
//        }
//        return postIds;
//    }
//}
