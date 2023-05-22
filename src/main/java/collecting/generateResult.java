package collecting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


class Answer {
    public Owner owner;
    int question_id;
    int score;
    boolean is_accepted;
    long creation_date;
}

class Question {
    int question_id;
    List<String> tags;
    int score;
    int view_count;
    long creation_date;
    Owner owner;
}

class Comment {
    int comment_id;
    int score;
    long creation_date;
    Owner owner;
}

class Owner {
    int user_id;
    String display_name;
    int reputation;
    String link;
    String profile_image;
}

public class generateResult {
    public static void main(String[] args) {
        Map<String, Object> finalResult = new HashMap<>();

        // Combine finalResult and generate() together
        Map<String, Object> generateResult = generate();
        finalResult.putAll(generateResult);

        // Combine finalResult and analyzeTags() together
        Map<String, Object> analyzeTagsResult = analyzeTags();
        finalResult.putAll(analyzeTagsResult);

        int notExistUserCount = 0;
        int totalUserCount = 0;

        // Load data from JSON files
        List<Answer> answersData = loadDataFromJsonFile("answers.json", new TypeToken<List<Answer>>(){}.getType());
        List<Question> questionsData = loadDataFromJsonFile("questions.json", new TypeToken<List<Question>>(){}.getType());
        List<Comment> commentsData = loadDataFromJsonFile("comments.json", new TypeToken<List<Comment>>(){}.getType());

        // Calculate the distribution of user participation in threads
        int questionUsers = countDistinctUsers(questionsData);
        int answerUsers = countDistinctUsers(answersData);
        int commentUsers = countDistinctUsers(commentsData);

        System.out.println("Number of distinct users who posted questions: " + questionUsers);
        finalResult.put("number_of_question_users", questionUsers);

        System.out.println("Number of distinct users who posted answers: " + answerUsers);
        finalResult.put("number_of_answer_users", answerUsers);

        System.out.println("Number of distinct users who posted comments: " + commentUsers);
        finalResult.put("number_of_comment_users", commentUsers);

        // Print not exist user count
        System.out.println("Number of posts without user id: " + notExistUserCount);
        finalResult.put("number_of_posts_without_user_id", notExistUserCount);

        // Get the most active users in thread discussions
        List<Map<String, Object>> mostActiveUsers = getMostActiveUsers(questionsData, answersData, commentsData, 10);

        // Print total user count
        totalUserCount = mostActiveUsers.size();
        System.out.println("Total number of users: " + totalUserCount);
        finalResult.put("total_number_of_users", totalUserCount);

        finalResult.put("most_active_users", mostActiveUsers);

        // Gives result of relation between user reputation and post score
        Map<Integer, List<Integer>> reputationScoreRelation = reputationScoreRelation(questionsData, answersData);
        finalResult.put("reputation_score_relation", reputationScoreRelation);

        // Gives result of relation between user reputation and acceptance
        Map<Integer, Boolean> reputationAcceptanceRelation = reputationAcceptanceRelation(answersData);
        finalResult.put("reputation_acceptance_relation", reputationAcceptanceRelation);

        // Save the final result to a JSON file
        try (FileWriter file = new FileWriter("result.json")) {
            Gson gson = new Gson();
            gson.toJson(finalResult, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, Boolean> reputationAcceptanceRelation(List<Answer> answersData) {
        // Implement the logic for calculating the relation between user reputation and acceptance
        return new HashMap<>();
    }

    private static Map<Integer, List<Integer>> reputationScoreRelation(List<Question> questionsData, List<Answer> answersData) {
        // Implement the logic for calculating the relation between user reputation and post score
        return new HashMap<>();
    }


    private static <T> List<T> loadDataFromJsonFile(String fileName, java.lang.reflect.Type type) {
        List<T> data = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            Gson gson = new Gson();
            data = gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static Map<String, Object> generate() {
        Map<String, Object> finalResult = new HashMap<>();

        // Implement the logic for generating the metrics
        // ...

        return finalResult;
    }

    private static Map<String, Object> analyzeTags() {
        Map<String, Object> results = new HashMap<>();

        // Implement the logic for analyzing tags
        // ...

        return results;
    }

    private static int countDistinctUsers(List<? extends Object> data) {
        Set<Integer> users = new HashSet<>();
        int notExistUserCount = 0;

        for (Object item : data) {
            if (item instanceof Question) {
                Question question = (Question) item;
                if (question.owner != null && question.owner.user_id != 0) {
                    users.add(question.owner.user_id);
                } else {
                    notExistUserCount++;
                }
            } else if (item instanceof Answer) {
                Answer answer = (Answer) item;
                if (answer.owner != null && answer.owner.user_id != 0) {
                    users.add(answer.owner.user_id);
                } else {
                    notExistUserCount++;
                }
            } else if (item instanceof Comment) {
                Comment comment = (Comment) item;
                if (comment.owner != null && comment.owner.user_id != 0) {
                    users.add(comment.owner.user_id);
                } else {
                    notExistUserCount++;
                }
            }
        }

        return users.size();
    }

    private static List<Map<String, Object>> getMostActiveUsers(List<Question> questionsData, List<Answer> answersData, List<Comment> commentsData, int numUsers) {
        Map<Integer, Integer> userCounts = new HashMap<>();
        int notExistUserCount = 0;

        for (Question question : questionsData) {
            if (question.owner != null && question.owner.user_id != 0) {
                int userId = question.owner.user_id;
                userCounts.put(userId, userCounts.getOrDefault(userId, 0) + 1);
            } else {
                notExistUserCount++;
            }
        }

        for (Answer answer : answersData) {
            if (answer.owner != null && answer.owner.user_id != 0) {
                int userId = answer.owner.user_id;
                userCounts.put(userId, userCounts.getOrDefault(userId, 0) + 1);
            } else {
                notExistUserCount++;
            }
        }

        for (Comment comment : commentsData) {
            if (comment.owner != null && comment.owner.user_id != 0) {
                int userId = comment.owner.user_id;
                userCounts.put(userId, userCounts.getOrDefault(userId, 0) + 1);
            } else {
                notExistUserCount++;
            }
        }

        List<Map.Entry<Integer, Integer>> sortedUsers = new ArrayList<>(userCounts.entrySet());
        sortedUsers.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        List<Map<String, Object>> mostActiveUsers = new ArrayList<>();
        for (int i = 0; i < numUsers && i < sortedUsers.size(); i++) {
            int userId = sortedUsers.get(i).getKey();
            int count = sortedUsers.get(i).getValue();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user_id", userId);
            userMap.put("count", count);
            userMap.put("info", getUserInfo(questionsData, answersData, commentsData, userId));

            mostActiveUsers.add(userMap);
        }

        return mostActiveUsers;
    }

    private static Map<String, Object> getUserInfo(List<Question> questionsData, List<Answer> answersData, List<Comment> commentsData, int userId) {
        Map<String, Object> userInfo = new HashMap<>();

        for (Question question : questionsData) {
            if (question.owner != null && question.owner.user_id == userId) {
                userInfo.put("user_id", userId);
                userInfo.put("display_name", question.owner.display_name);
                userInfo.put("reputation", question.owner.reputation);
                userInfo.put("link", question.owner.link);
                userInfo.put("profile_image", question.owner.profile_image);
                break;
            }
        }

        if (userInfo.isEmpty()) {
            for (Answer answer : answersData) {
                if (answer.owner != null && answer.owner.user_id == userId) {
                    userInfo.put("user_id", userId);
                    userInfo.put("display_name", answer.owner.display_name);
                    userInfo.put("reputation", answer.owner.reputation);
                    userInfo.put("link", answer.owner.link);
                    userInfo.put("profile_image", answer.owner.profile_image);
                    break;
                }
            }
        }

        if (userInfo.isEmpty()) {
            for (Comment comment : commentsData) {
                if (comment.owner != null && comment.owner.user_id == userId) {
                    userInfo.put("user_id", userId);
                    userInfo.put("display_name", comment.owner.display_name);
                    userInfo.put("reputation", comment.owner.reputation);
                    userInfo.put("link", comment.owner.link);
                    userInfo.put("profile_image", comment.owner.profile_image);
                    break;
                }
            }
        }

        return userInfo;
    }

    private static Map<Integer, Integer> getReputationScoreRelation(List<Question> questionsData, List<Answer> answersData) {
        Map<Integer, Integer> reputationScoreRelation = new HashMap<>();

        for (Question question : questionsData) {
            if (question.owner != null && question.owner.user_id != 0) {
                int userId = question.owner.user_id;
                reputationScoreRelation.put(userId, question.owner.reputation);
            }
        }

        for (Answer answer : answersData) {
            if (answer.owner != null && answer.owner.user_id != 0) {
                int userId = answer.owner.user_id;
                reputationScoreRelation.put(userId, answer.owner.reputation);
            }
        }

        return reputationScoreRelation;
    }

    private static Map<Integer, Boolean> getReputationAcceptanceRelation(List<Answer> answersData) {
        Map<Integer, Boolean> reputationAcceptanceRelation = new HashMap<>();

        for (Answer answer : answersData) {
            if (answer.owner != null && answer.owner.user_id != 0) {
                int userId = answer.owner.user_id;
                boolean isAccepted = answer.is_accepted;
                reputationAcceptanceRelation.put(userId, isAccepted);
            }
        }

        return reputationAcceptanceRelation;
    }
}
