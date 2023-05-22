# Datapart

## Data collecting

### `RunQandA.java`  and `RunComment.java`

`RunQandA.java` is a program that gets questions and answers from the Stack Overflow API. It retrieves a list of questions related to Java programming language from the Stack Overflow website, along with their corresponding answers, and performs various analyses on the data.

`RunComment.java`  is similar to runQandA, it gets comments from Stack Overflow API.

In order to reduce the number of visits, we grouped 100 pieces of data, packaged the application and queried it with the server. This can significantly reduce the number of times the StackOverflow API is used.



#### Libraries

1. `okhttp3`: It is an open-source library for making HTTP requests in Java. In this code, it is used to send HTTP requests to the Stack Overflow API and receive responses. It provides a convenient and efficient way to handle network communication.
2. `org.json`: It is a library for processing JSON data in Java. It provides classes and methods to parse JSON strings, create JSON objects and arrays, and manipulate JSON data. In this code, it is used to parse the responses received from the Stack Overflow API, extract relevant information, and store the data in JSON format.

#### functionality:

**Importing Libraries:**

The required libraries are imported, including org.json for JSON processing and okhttp3 for making HTTP requests.

**API_KEY Constant:**

The API key for accessing the Stack Overflow API is defined as a constant variable.

**main Method:**

The main method serves as the entry point of the program. It calls the analyzeQuestions method to start the analysis.

**analyzeQuestions Method:**

This method performs the main analysis on the Stack Overflow questions and answers.
It initializes variables to store various metrics and collections for questions and answers.
A loop is used to iterate over multiple pages of questions (up to a maximum of 10 pages) from the API.
For each page of questions, the getQuestions method is called to retrieve the questions.
The method then processes each question, retrieves its answers using the getAnswers method, and collects the necessary data.
Metrics such as the total number of questions, questions without answers, total number of answers, and maximum number of answers are calculated.
The collected question and answer data is stored in separate collections.
Finally, the metrics are printed, and the question and answer data are written to local JSON files.

**getQuestions Method:**

This method constructs the API URL and sends an HTTP request to retrieve a page of questions from the Stack Overflow API.
The URL is constructed with parameters specifying the sorting order, site (Stack Overflow), API key, page number, page size, date range, and tags (only Java-related questions).
The method uses the sendRequest method to execute the HTTP request and parse the response into a JSON object, which is returned.

**getAnswers Method:**

Similar to the getQuestions method, this method constructs the API URL and sends an HTTP request to retrieve answers for a specific question ID from the Stack Overflow API.
The URL is constructed with parameters specifying the question ID, sorting order, site (Stack Overflow), and API key.
The method uses the sendRequest method to execute the HTTP request and parse the response into a JSON object, which is returned.

**sendRequest Method:**

This method uses the okhttp3 library to send an HTTP GET request to the specified URL and retrieve the response.
It returns the response as a JSON object after parsing the response body.

**getAnswerList Method:**

This utility method converts a JSON array of answer items into a list of JSONObject objects for easier processing.

**writeJSONToFile Method:**

This utility method writes a list of JSONObject data to a JSON file with the specified filename.
It uses a PrintWriter to write the JSON data as a string to the file.



## Data Analyze

### `contextAnalyze.java` and `generateResult.java`

In data analysis, we analyze three sets of data packets in various ways, namely the ones mentioned earlier: `questions.json` `answers.json` and `comments.json`.

In `generateResult.java`, we analyze the data from stackoverflow API and save the result to another json file called `result.json` . So that back end RESTful API can get data from it and prevent analyzing step in backend program.

The code performs various analyses on data stored in JSON files and generates a final result in the form of a dictionary, which is then saved in a file named "result.json". 

The code is divided into several functions, each responsible for a specific task:

1. `find_answer_info(question_id)`: This function takes a question ID as input and searches for answers related to that question in the "answers.json" file. It counts the number of answers, checks if there is an accepted answer, records the solution time, and compares the scores of the answers to determine if any answer has a higher score than the accepted answer.
2. `generate()`: This function calculates various metrics based on the data in the "questions.json" file. It iterates through the questions, calls `find_answer_info()` for each question, and collects information such as the total number of questions, questions without answers, average number of answers per question, maximum number of answers to a question, distribution of answer counts, percentage of questions with accepted answers, percentage of answers with better scores than the accepted answer, and resolution time distribution.
3. `analyze_tags()`: This function analyzes the tags associated with the questions in the "questions.json" file. It calculates tag co-occurrence counts, upvotes for each tag or tag combination, and views for each tag or tag combination. It then sorts and selects the most frequent tag co-occurrences, tags with the highest upvotes, and tags with the highest views.
4. `count_distinct_users(data)`: This function counts the number of distinct users based on the "user_id" field in the provided data.
5. `get_most_active_users(data, num_users)`: This function identifies the most active users based on their contributions in the provided data. It counts the occurrences of each user and returns a list of the top "num_users" users sorted by their activity count.
6. `get_user_info(data, user_id)`: This function retrieves additional information about a specific user based on their "user_id" in the provided data.

The main part of the code starts by calling the `generate()` and `analyze_tags()` functions to collect the metrics and tag analysis results, respectively. It then proceeds to perform user-related analyses:

1. It initializes variables to track the count of non-existent user IDs and the total user count.

2. It loads the data from the "answers.json", "questions.json", and "comments.json" files into separate variables.

3. It calls the `count_distinct_users()` function for each type of data to calculate the number of distinct users who posted questions, answers, and comments.

4. It prints the counts of distinct users and posts without user IDs.

5. It calls the `get_most_active_users()` function to retrieve the most active users in the discussions and prints their user IDs and activity counts.

6. It calls the `reputation_score_relation()` and `reputation_acceptance_relation()` functions to analyze the relationship between user reputation and post score, and between user reputation and answer acceptance, respectively.

7. It saves the final result dictionary into a JSON file named "result.json".

   

In `contextAnalyze.java`, we analyze the data of body part of posts in order to know what API is discussed most in stackOverflow.

It performs context analysis on data extracted from the Stack Overflow website. It collects data from JSON files containing information about questions, answers, and comments from Stack Overflow. The code then processes the data to identify frequently discussed Java classes and methods within the collected content.

The contextAnalyze class: contains the main method that serves as the entry point of the program.

Various JSON arrays (questions, answers, comments) are declared to store the extracted data from corresponding JSON files.

The main method: loading the JSON data from the files using the loadJSONArrayFromFile method. Then extracts the IDs from the JSON arrays using the extractIds method.

Two separate lists (allIds, commentIds) are created to store all the question and answer IDs together and all the comment IDs, respectively.

The getPostContent method: retrieve the content (body) of posts (questions and answers) using the obtained IDs. The method makes API requests to the Stack Exchange API to retrieve the data in batches. The retrieved post content is stored in a list of JSON objects (allPosts).

The getCommentContent method: retrieve the content (body) of comments using the obtained comment IDs. Similar to getPostContent, this method also makes API requests in batches to retrieve the data. The retrieved comment content is stored in a list of JSON objects (allComments).

The extracted bodies from posts and comments are combined into a single list (allBodies), and the list is saved as a JSON array in a file named "all_bodies.json". Then proceeds to extract Java API mentions from the collected bodies using the extractJavaAPIs method. 

It searches for combinations of capitalized words followed by a dot, indicating a potential class and method reference. The identified combinations are stored in two separate lists (apiMentionsClasses, apiMentionsMethods).

A JSON object (result) is created to store the frequent classes and methods.

The libraries/packages:
java.nio.charset.StandardCharsets for specifying character encoding.
org.json for working with JSON data.
