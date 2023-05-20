import json

# count how many answers in answers.json

with open('answers.json') as answers_file:
    answers = json.load(answers_file)

print(len(answers))
