package collecting;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class runQandA {

    private static final String API_KEY = "35QzBYD2Xrj3e8Xo9Q5f1A((";

    public static void main(String[] args) throws JSONException {
        analyzeQuestions();
    }

    public static void analyzeQuestions() throws JSONException {
        int totalQuestions = 0;
        int questionsWithoutAnswers = 0;
        int totalAnswers = 0;
        int maxAnswers = 0;
        List<Integer> answerCounts = new ArrayList<>();
        List<JSONObject> questionCollections = new ArrayList<>();
        List<JSONObject> answerCollections = new ArrayList<>();

        int page = 1;
        boolean hasMore = true;

        while (hasMore && page <= 10) {
            JSONObject questions = getQuestions(page);
            JSONArray items = questions.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject question = items.getJSONObject(i);
                questionCollections.add(question);
                System.out.println("Processing page " + page + " with " + items.length() + " questions");
                System.out.println("total questions: " + totalQuestions);
                System.out.println("quota remaining: " + questions.getInt("quota_remaining"));

                int questionId = question.getInt("question_id");
                JSONObject answers = getAnswers(questionId);
                JSONArray answerItems = answers.getJSONArray("items");
                answerCollections.addAll(getAnswerList(answerItems));
                System.out.println("Collecting. Question " + questionId + " has " + answerItems.length() + " answers");

                int answerCount = answerItems.length();
                if (answerCount == 0) {
                    questionsWithoutAnswers++;
                }

                totalAnswers += answerCount;
                maxAnswers = Math.max(maxAnswers, answerCount);
                answerCounts.add(answerCount);
            }

            totalQuestions += items.length();
            page++;
            hasMore = questions.getBoolean("has_more");
        }

        // Calculate metrics
        double percentageWithoutAnswers = (double) questionsWithoutAnswers / totalQuestions * 100;
        double averageAnswers = (double) totalAnswers / totalQuestions;

        // Store data in local files
        try {
            writeJSONToFile("questions.json", questionCollections);
            writeJSONToFile("answers.json", answerCollections);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print metrics
        System.out.println("Percentage of questions without answers: " + percentageWithoutAnswers + "%");
        System.out.println("Average number of answers: " + averageAnswers);
        System.out.println("Maximum number of answers: " + maxAnswers);
        System.out.println("Distribution of the number of answers: " + answerCounts);
    }

    private static JSONObject getQuestions(int page) {
        String url = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&site=stackoverflow"
                + "&key=" + API_KEY + "&page=" + page + "&pagesize=100"
                + "&fromdate=1652918400&todate=1653177600&tagged=java";
        return sendRequest(url);
    }

    private static JSONObject getAnswers(int questionId) {
        String url = "https://api.stackexchange.com/2.3/questions/" + questionId + "/answers?order=desc&sort=activity"
                + "&site=stackoverflow&key=" + API_KEY;
        return sendRequest(url);
    }

    private static JSONObject sendRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            return new JSONObject(responseBody);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<JSONObject> getAnswerList(JSONArray answerItems) {
        List<JSONObject> answerList = new ArrayList<>();
        for (int i = 0; i < answerItems.length(); i++) {
            try {
                JSONObject answer = answerItems.getJSONObject(i);
                answerList.add(answer);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answerList;
    }

    private static void writeJSONToFile(String filename, List<JSONObject> data) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(data.toString());
        }
    }
}
