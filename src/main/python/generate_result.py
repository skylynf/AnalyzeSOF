import json


def find_answer_info(question_id):
    answer_count = 0
    have_accepted_answer = False
    soluted_time = None
    accepted_answer_score = 0
    best_score = 0

    with open('answers.json') as f:
        answers = json.load(f)

    for answer in answers:
        if answer['question_id'] == question_id:
            answer_count += 1
            best_score = max(best_score, answer['score'])
            if answer['is_accepted']:
                have_accepted_answer = True
                soluted_time = answer['creation_date']
                accepted_answer_score = answer['score']

    is_better_than_accepted = best_score > accepted_answer_score

    return answer_count, have_accepted_answer, soluted_time, is_better_than_accepted


def generate():

    # open the json files
    with open('questions.json') as f:
        questions = json.load(f)


    # calculate the metrics
    total_questions = len(questions)
    questions_without_answers = 0
    total_answers = 0
    max_answers = 0
    answer_counts = []
    better_than_accepted_counts = 0

    total_have_accepted_answer = 0
    resolution_time_distribution = []

    for question in questions:
        question_id = question['question_id']
        answer_count, have_accepted_answer, soluted_time, is_better_than_accepted = find_answer_info(question_id)
        answer_counts.append(answer_count)

        if answer_count == 0:
            questions_without_answers += 1

        total_answers += answer_count
        max_answers = max(max_answers, answer_count)

        if have_accepted_answer:
            total_have_accepted_answer += 1
            resolution_time = soluted_time - question['creation_date']
            resolution_time_distribution.append(resolution_time)
            if is_better_than_accepted:
                better_than_accepted_counts += 1


    percentage_without_answers = (questions_without_answers / total_questions) * 100
    average_answers = total_answers / total_questions

    final_result = {}
    final_result['percentage_without_answers'] = percentage_without_answers
    final_result['average_answers'] = average_answers
    final_result['max_answers'] = max_answers
    final_result['answer_counts'] = answer_counts

    final_result['percentage_have_accepted_answer'] = total_have_accepted_answer / total_questions * 100
    final_result['percentage_better_than_accepted'] = better_than_accepted_counts / total_have_accepted_answer * 100
    final_result['resolution_time_distribution'] = resolution_time_distribution

    with open('result.json', 'w') as f:
        json.dump(final_result, f, indent=4)

generate()