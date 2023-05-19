import requests
import json

# Enter your API key here
api_key = "YOUR_API_KEY"

# Function to retrieve comments for a list of post IDs
def get_comments(post_ids):
    comments = []
    params = {
        "site": "stackoverflow"
    }
    url = "https://api.stackexchange.com/2.3/posts/{}/comments".format(";".join(map(str, post_ids)))
    response = requests.get(url, params=params)
    if response.ok:
        data = response.json()
        for item in data["items"]:
            comments.append(item)
    return comments

# Load question.json and answer.json
with open("questions.json", "r") as question_file:
    question_data = json.load(question_file)
with open("answers.json", "r") as answer_file:
    answer_data = json.load(answer_file)

# Extract post IDs from question.json and answer.json
question_ids = [question["question_id"] for question in question_data]
answer_ids = [answer["answer_id"] for answer in answer_data]

# Combine question IDs and answer IDs
post_ids = question_ids + answer_ids

# Retrieve comments in batches of 100 post IDs
batch_size = 100
num_batches = (len(post_ids) - 1) // batch_size + 1
all_comments = []

for i in range(num_batches):
    print(f"Processing batch {i + 1}/{num_batches}")
    start = i * batch_size
    end = start + batch_size
    comments = get_comments(post_ids[start:end])
    all_comments.extend(comments)

# Save comments to a file
with open("comments.json", "w") as comments_file:
    json.dump(all_comments, comments_file)
