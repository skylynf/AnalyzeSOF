
import requests
import json

API_KEY = '35QzBYD2Xrj3e8Xo9Q5f1A(('

def get_questions(page=1, pagesize=100):
    url = f"https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&site=stackoverflow&key={API_KEY}&page={page}&pagesize={pagesize}&fromdate=1652918400&todate=1653177600&tagged=java"
    # url = f"https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&site=stackoverflow&key={API_KEY}&page={page}&pagesize={pagesize}&fromdate=1652918400&todate=1652928400&tagged=java"
    response = requests.get(url)
    return response.json()

def get_answers(question_id):
    url = f"https://api.stackexchange.com/2.3/questions/{question_id}/answers?order=desc&sort=activity&site=stackoverflow&key={API_KEY}"
    response = requests.get(url)
    return response.json()

def analyze_questions():
    total_questions = 0
    questions_without_answers = 0
    total_answers = 0
    max_answers = 0
    answer_counts = []
    question_collections = []
    answer_collections = []

    page = 1
    has_more = True

    while has_more and page <= 10:
        questions = get_questions(page)
        items = questions['items']
        question_collections.extend(items)
        print(f"Processing page {page} with {len(items)} questions")
        print(f"total questions: {total_questions}")
        print(f"quote remaining: {questions['quota_remaining']}")

        for question in items:
            question_id = question['question_id']
            answers = get_answers(question_id)
            answer_count = len(answers['items'])
            answer_collections.extend(answers['items'])
            print(f"Question {question_id} has {answer_count} answers")

            if answer_count == 0:
                questions_without_answers += 1

            total_answers += answer_count
            max_answers = max(max_answers, answer_count)
            answer_counts.append(answer_count)

        total_questions += len(items)
        page += 1
        has_more = questions['has_more']

    # Calculate metrics
    percentage_without_answers = (questions_without_answers / total_questions) * 100
    average_answers = total_answers / total_questions

    # Store data in local files
    with open('questions.json', 'w') as f:
        json.dump(question_collections, f, indent=4)

    with open('answers.json', 'w') as f:
        json.dump(answer_collections, f, indent=4)

    # Print metrics
    print(f"Percentage of questions without answers: {percentage_without_answers}%")
    print(f"Average number of answers: {average_answers}")
    print(f"Maximum number of answers: {max_answers}")
    print(f"Distribution of the number of answers: {answer_counts}")


analyze_questions()
