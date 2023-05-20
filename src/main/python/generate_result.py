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

    # Create the answer distribution dictionary
    answer_distribution = {
        "0": 0,
        "1": 0,
        "2": 0,
        "3": 0,
        "4": 0,
        "5": 0,
        "6": 0,
        "7+": 0
    }

    # Populate the answer distribution
    for count in answer_counts:
        if count == 7 or count > 7:
            answer_distribution["7+"] += 1
        else:
            answer_distribution[str(count)] += 1

    print(answer_distribution)

    final_result = {}
    final_result['percentage_without_answers'] = percentage_without_answers
    final_result['average_answers'] = average_answers
    final_result['max_answers'] = max_answers
    final_result['answer_distribution'] = answer_distribution

    final_result['percentage_have_accepted_answer'] = total_have_accepted_answer / total_questions * 100
    final_result['percentage_better_than_accepted'] = better_than_accepted_counts / total_have_accepted_answer * 100
    final_result['resolution_time_distribution'] = resolution_time_distribution
    return final_result




from itertools import combinations

def analyze_tags():
    with open('questions.json', 'r') as f:
        questions = json.load(f)

    # Dictionary to store tag co-occurrence counts
    tag_cooccurrence = {}

    # Dictionary to store upvotes for each tag or tag combination
    upvotes = {}

    # Dictionary to store views for each tag or tag combination
    views = {}

    for question in questions:
        tags = question['tags']
        question_upvotes = question['score']
        question_views = question['view_count']

        # Update tag co-occurrence counts
        for i in range(len(tags)):
            for j in range(i + 1, len(tags)):
                tag1 = tags[i]
                tag2 = tags[j]
                tag_pair = (tag1, tag2)

                # Update tag co-occurrence count
                tag_cooccurrence[tag_pair] = tag_cooccurrence.get(tag_pair, 0) + 1

        # Exclude "java" tag from the analysis
        tags = [tag for tag in tags if tag != 'java']

        # Update upvotes for each tag or tag combination
        for tag in tags:
            upvotes[tag] = upvotes.get(tag, 0) + question_upvotes

        # Update views for each tag or tag combination
        for tag in tags:
            views[tag] = views.get(tag, 0) + question_views

        # Update upvotes for tag combinations
        tag_combinations = combinations(tags, 2)
        for combination in tag_combinations:
            upvotes[combination] = upvotes.get(combination, 0) + question_upvotes

    # Sort tag co-occurrence counts by frequency
    sorted_cooccurrence = sorted(tag_cooccurrence.items(), key=lambda x: x[1], reverse=True)

    # Sort upvotes by count
    sorted_upvotes = sorted(upvotes.items(), key=lambda x: x[1], reverse=True)

    # Sort views by count
    sorted_views = sorted(views.items(), key=lambda x: x[1], reverse=True)

    results = {}
    results['sorted_cooccurrence'] = []
    results['sorted_upvotes'] = sorted_upvotes[:20]
    results['sorted_views'] = sorted_views[:20]

    # Print results
    print("Tags frequently appearing together with the 'java' tag:")
    for tag_pair, count in sorted_cooccurrence[:22]:
        if 'java' in tag_pair:
            other_tag = tag_pair[0] if tag_pair[1] == 'java' else tag_pair[1]
            print(f"{other_tag} (co-occurrence count: {count})")
            results['sorted_cooccurrence'].append((other_tag, count))


    print("Tags and tag combinations with the most upvotes:")
    for item, upvote_count in sorted_upvotes[:20]:
        print(f"{item} (upvotes: {upvote_count})")

    print("\nTags and tag combinations with the most views:")
    for item, view_count in sorted_views[:20]:
        print(f"{item} (views: {view_count})")

    return results


 #   return final_result



# Test


import json



# Test END



final_result = {}

# combine final_result and generate() together
final_result.update(generate())

# combine final_result and analyze_tags() together
final_result.update(analyze_tags())


not_exist_user_count = 0
total_user_count = 0

def count_distinct_users(data):
    global not_exist_user_count
    users = set()
    for item in data:
        if 'owner' in item:
            if 'user_id' in item['owner']:
                users.add(item['owner']['user_id'])
            else:
                not_exist_user_count += 1
    return len(users)

def get_most_active_users(data, num_users):
    global not_exist_user_count, total_user_count
    user_counts = {}
    for item in data:
        if 'owner' in item:
            if 'user_id' in item['owner']:
                user_id = item['owner']['user_id']
                user_counts[user_id] = user_counts.get(user_id, 0) + 1
            else:
                not_exist_user_count += 1
    sorted_users = sorted(user_counts.items(), key=lambda x: x[1], reverse=True)
    total_user_count = len(sorted_users)
    return sorted_users[:num_users]

def get_user_info(data, user_id):
    return_dict = {}
    for item in data:
        if 'owner' in item:
            if 'user_id' in item['owner']:
                if item['owner']['user_id'] == user_id:
                    return_dict['user_id'] = user_id
                    return_dict['display_name'] = item['owner']['display_name']
                    return_dict['reputation'] = item['owner']['reputation']
                    return_dict['link'] = item['owner']['link']
                    return_dict['profile_image'] = item['owner']['profile_image']

                    return return_dict
    return None

# Load data from JSON files
with open('answers.json', 'r') as f:
    answers_data = json.load(f)

with open('questions.json', 'r') as f:
    questions_data = json.load(f)

with open('comments.json', 'r') as f:
    comments_data = json.load(f)

# Calculate the distribution of user participation in threads

total_question_users = count_distinct_users(questions_data)
total_answer_users = count_distinct_users(answers_data)
total_comment_users = count_distinct_users(comments_data)

single_question_user_distribution = []

for item in questions_data:
    answer_users = []
    comments_users = []
    for answer in answers_data:
        if answer['question_id'] == item['question_id']:
            answer_users.append(answer['owner']['user_id'])
    for comment in comments_data:
        if comment['post_id'] == item['question_id']:
            if 'user_id' in comment['owner']:
                comments_users.append(comment['owner']['user_id'])
    single_question_user_distribution.append((len(set(answer_users)), len(set(comments_users))))

final_result['single_question_user_distribution'] = single_question_user_distribution


print(f"Number of distinct users who posted questions: {total_question_users}")
final_result['number_of_question_users'] = total_question_users

print(f"Number of distinct users who posted answers: {total_answer_users}")
final_result['number_of_answer_users'] = total_answer_users

print(f"Number of distinct users who posted comments: {total_comment_users}")
final_result['number_of_comment_users'] = total_comment_users

# print not exist user count
print(f"Number of posts without user id: {not_exist_user_count}")
final_result['number_of_posts_without_user_id'] = not_exist_user_count

# Get the most active users in thread discussions
most_active_users = get_most_active_users(questions_data + answers_data + comments_data, 10)

# print total user count
print(f"Total number of users: {total_user_count}")
final_result['total_number_of_users'] = total_user_count
final_result['most_active_users'] = {}
# Print the most active users in thread discussions
print("Most active users in thread discussions:")
for user_id, count in most_active_users:
    print(f"User ID: {user_id}, Count: {count}")
    final_result['most_active_users'][user_id] = {}
    final_result['most_active_users'][user_id]['info'] = get_user_info(questions_data + answers_data + comments_data,user_id)
    final_result['most_active_users'][user_id]['count'] = count



# gives result of relation between user reputation and post score
def reputation_score_relation(data):
    reputation_score_relation = {}
    for item in data:
        if 'owner' in item:
            if 'user_id' in item['owner']:
                user_id = item['owner']['user_id']
                reputation = item['owner']['reputation']
                score = item['score']
                reputation_score_relation[user_id] = (reputation, score)
    return reputation_score_relation

# gives result of relation between user reputation and acceptance
def reputation_acceptance_relation(data):
    reputation_acceptance_relation = {}
    for item in data:
        if 'owner' in item:
            if 'user_id' in item['owner']:
                user_id = item['owner']['user_id']
                reputation = item['owner']['reputation']
                if 'is_accepted' in item:
                    is_accepted = item['is_accepted']
                    reputation_acceptance_relation[user_id] = (reputation, is_accepted)
    return reputation_acceptance_relation

final_result['reputation_score_relation'] = reputation_score_relation(questions_data + answers_data)
final_result['reputation_acceptance_relation'] = reputation_acceptance_relation(answers_data)


with open('result.json', 'w') as f:
    json.dump(final_result, f, indent=4)



