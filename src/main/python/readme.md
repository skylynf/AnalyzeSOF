### Number of Answers

What percentage of questions don't have any answer? 

```python
final_result['percentage_without_answers']
```

What is the average and maximum number of answers? 

```python
final_result['average_answers']
final_result['max_answers']
```

What is the distribution of the number of answers? 

The distribution contains answer count of:

0, 1, 2, 3, 4, 5, 6, 7+

```python
answer_distribution = {
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7+"
    }
final_result['answer_distribution']
```



### Accepted Answers

What percentage of questions have accepted answers (one question could only have one accepted answer)? 

```python
final_result['percentage_have_accepted_answer']
```

What is the distribution of question resolution time (i.e., the duration between the question posting time and the posting time of the accepted answer)? 

```python
final_result['percentage_better_than_accepted']
```

What percentage of questions have non-accepted answers (i.e., answers that are not marked as accepted) that have received more upvotes than the accepted answers?

```python
final_result['resolution_time_distribution']
```



### Tags

Which tags frequently appear together with the java tag? 

```python
results['sorted_cooccurrence']:[other_tag, count]
```

Which tags or tag combinations receive the most upvotes? 

```python
results['sorted_upvotes']:[[tags or combinations], upvotes]
```

Which tags or tag combinations receive the most views?

```python
results['sorted_views']:[[tags or combinations], views]
```



### Users

Many users could participate in a thread discussion. What is the distribution of such participation (i.e., the number of distinct users who post the question, answers, or comments in a thread)? 

```python
final_result['number_of_question_users']
final_result['number_of_answer_users']
final_result['number_of_comment_users']
```

**Addition:** Please mention, number of posts without user id, A pie chart can be made to indicate that some people have no ID or have cancelled their account.

```python
final_result['number_of_posts_without_user_id']

final_result['total_number_of_users']
```

Which are the most active users who frequently participate in thread discussions?

```python
final_result['most_active_users']:[user_id:['info', 'count']]
```



### Frequently discussed Java APIs (8 points) 

Which Java APIs (e.g., classes, methods) are frequently discussed on Stack Overflow? 

```python
result["frequent_classes"] = class_result[{"name", "count"}]
result["frequent_methods"] = method_result[{"method", "class", "count"}]
```



### Addition

```python
final_result['reputation_score_relation'] = [user_id: [reputation, score]]
final_result['reputation_acceptance_relation'] = [user_id: [reputation, is_accepted]]
```

Can use this data to create a scatter plot and see if there is a relationship between reputation and score/acceptance in the results