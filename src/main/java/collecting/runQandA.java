package collecting;

import java.io.FileWriter;
import java.io.IOException;
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

    public static void main(String[] args) {
        analyzeQuestions();
    }

    public static JSONObject getQuestions(int page, int pageSize) throws IOException, JSONException {
        String url = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&site=stackoverflow"
                + "&key=" + API_KEY + "&page=" + page + "&pagesize=" + pageSize
                + "&fromdate=1652918400&todate=1653177600&tagged=java";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        return new JSONObject(responseBody);
    }

    public static JSONObject getAnswers(int questionId) throws IOException, JSONException {
        String url = "https://api.stackexchange.com/2.3/questions/" + questionId + "/answers?order=desc&sort=activity"
                + "&site=stackoverflow&key=" + API_KEY;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        return new JSONObject(responseBody);
    }

    public static void analyzeQuestions() {
        int totalQuestions = 0;
        int questionsWithoutAnswers = 0;
        int totalAnswers = 0;
        int maxAnswers = 0;
        List<Integer> answerCounts = new ArrayList<>();
        List<JSONObject> questionCollections = new ArrayList<>();
        List<JSONObject> answerCollections = new ArrayList<>();

        int page = 1;
        boolean hasMore = true;

        try {
            while (hasMore && page <= 10) {
                JSONObject questions = getQuestions(page, 100);
                JSONArray items = questions.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject question = items.getJSONObject(i);
                    questionCollections.add(question);
                    int questionId = question.getInt("question_id");
                    JSONObject answers = getAnswers(questionId);
                    JSONArray answerItems = answers.getJSONArray("items");
                    answerCollections.addAll(toList(answerItems));
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
            try (FileWriter questionWriter = new FileWriter("questions.json")) {
                JSONArray questionJsonArray = new JSONArray(questionCollections);
                questionWriter.write(questionJsonArray.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileWriter answerWriter = new FileWriter("answers.json")) {
                JSONArray answerJsonArray = new JSONArray(answerCollections);
                answerWriter.write(answerJsonArray.toString(4));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Print metrics
            System.out.println("Percentage of questions without answers: " + percentageWithoutAnswers + "%");
            System.out.println("Average number of answers: " + averageAnswers);
            System.out.println("Maximum number of answers: " + maxAnswers);
            System.out.println("Distribution of the number of answers: " + answerCounts);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private static List<JSONObject> toList(JSONArray jsonArray) throws JSONException {
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getJSONObject(i));
        }
        return list;
    }
}
