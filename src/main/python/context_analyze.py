import requests
import json
import pandas as pd

api_url = "https://api.stackexchange.com/2.3/"
api_key = "YOUR_API_KEY"

# Load JSON data from files
with open('questions.json') as questions_file:
    questions = json.load(questions_file)

with open('answers.json') as answers_file:
    answers = json.load(answers_file)

with open('comments.json') as comments_file:
    comments = json.load(comments_file)

def make_api_request(endpoint, params):
    params["site"] = "stackoverflow"
    url = api_url + endpoint
    response = requests.get(api_url + endpoint, params=params)
    return response.json()

question_ids = [question["question_id"] for question in questions]
answer_ids = [answer["answer_id"] for answer in answers]
comment_ids = [comment["comment_id"] for comment in comments]

def get_post_content(ids):
    posts = []
    for i in range(0, len(ids), 30):
        print(f"Processing batch {i // 30 + 1}/{len(ids) // 30 + 1}")

        batch_ids = ids[i:i + 30]
        post_ids_str = ";".join(str(id) for id in batch_ids)
        endpoint = f"posts/{post_ids_str}"
        params = {"filter": "withbody"}
        response = make_api_request(endpoint, params)
        # print remaining quota
        print(f"Remaining quota: {response['quota_remaining']}")
        with open("response.json", "w") as response_file:
            json.dump(response, response_file)
        posts.extend(response["items"])
    return posts

def get_comment_content(ids):
    comments = []
    for i in range(0, len(ids), 30):
        print(f"Processing batch {i // 30 + 1}/{len(ids) // 30 + 1}")

        batch_ids = ids[i:i + 30]
        comment_ids_str = ";".join(str(id) for id in batch_ids)
        endpoint = f"comments/{comment_ids_str}"
        params = {"filter": "withbody"}
        response = make_api_request(endpoint, params)
        # print remaining quota
        print(f"Remaining quota: {response['quota_remaining']}")
        comments.extend(response["items"])
    return comments

all_posts = get_post_content(question_ids + answer_ids)
all_comments = get_comment_content(comment_ids)

all_bodies = []
for post in all_posts:
    all_bodies.append(post["body"])
for comment in all_comments:
    all_bodies.append(comment["body"])

# save to json first
with open("all_bodies.json", "w") as body_file:
    json.dump(all_bodies, body_file)

with open("all_bodies.json", "r") as body_file:
    all_bodies = json.load(body_file)

def extract_java_apis(text):
    import re
    pattern = r'\b([A-Z]\w+)\.([A-Z]\w+)\b'
    # print(text)
    matches = re.findall(pattern, text)
    unique_combinations = set(matches)
    unique_classes = set([combo[0] for combo in unique_combinations])
    return unique_combinations, unique_classes


api_mentions_classes = []
api_mentions_methods = []
for body in all_bodies:
    class_method_combinations, unique_class = extract_java_apis(body)
    api_mentions_classes.extend(unique_class)
    api_mentions_methods.extend(class_method_combinations)

api_mentions_classes_df = pd.DataFrame(api_mentions_classes, columns=["Class"])
frequent_classes = api_mentions_classes_df["Class"].value_counts().head(10)

api_mentions_df = pd.DataFrame(api_mentions_methods, columns=["Class", "Method"])
frequent_methods = api_mentions_df.value_counts().head(10)

# Display the top 8 frequently discussed classes
print("Frequently discussed classes:")
print(frequent_classes)

print("\n")

# Display the top 8 frequently discussed methods
print("Frequently discussed methods:")
print(frequent_methods)

# add result to result json, first open it, then add the result, then save it

class_result = []
method_result = []

for i in range(len(frequent_classes)):
    class_result.append({"name": frequent_classes.index[i], "count":frequent_classes[i].item()})
for i in range(len(frequent_methods)):
    method_result.append({"method": frequent_methods.index[i][1], "class": frequent_methods.index[i][0], "count": frequent_methods[i].item()})

with open("result.json", "r") as result_file:
    result = json.load(result_file)

result["frequent_classes"] = class_result
result["frequent_methods"] = method_result

with open("result.json", "w") as result_file:
    json.dump(result, result_file)
