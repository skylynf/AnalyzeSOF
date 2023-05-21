# RESTful APIs Documentation



Obtain ` result.json` file.

```java
	@GetMapping("/result")
    public ResponseEntity<byte[]> getFile() throws IOException  
```



Single number APIs including $ int $, $ double$ . Check corresponding function name.

```java
    @GetMapping("/PercentageWithoutAnswers")
    public ResponseEntity<Double> getPercentageWithoutAnswers() throws IOException 
```

  

```java
    @GetMapping("/AverageAnswers")
    public ResponseEntity<Double> getAverageAnswers() throws IOException 
```




```java
    @GetMapping("/MaxAnswers")
    public ResponseEntity<Integer> getMaxAnswers() throws IOException 
```



Return JsonNode of answer distribution.

```java
    @GetMapping("/AnswerDistribution")
    public ResponseEntity<JsonNode> getAnswerDistribution() throws IOException 
```




```java
    @GetMapping("/AcceptedAnswersPercentage")
    public ResponseEntity<Double> getAcceptedAnswersPercentage() 
```



Return a list of integer including resolution time distribution.

```java
    @GetMapping("/QuestionResolutionTimeDistribution")
    public ResponseEntity<List<Integer>> getQuestionResolutionTimeDistribution() 
```




```java
    @GetMapping("/NonAcceptedAnswersUpvotesPercentage")
    public ResponseEntity<Double> getNonAcceptedAnswersUpvotesPercentage() 
```



Return List of Map, which mapping Tag names to occur count.

```java
    @GetMapping("/FrequentlyCooccurringTags")
    public ResponseEntity<List<Map<String, Object>>> getFrequentlyCooccurringTags() 
```



Return List of Map, which mapping Tag names (and tag combination name) to upvote count.

```java
    @GetMapping("/MostUpvotedTags")
    public ResponseEntity<List<Map<String, Object>>> getMostUpvotedTags()
```



Return List of Map, which mapping Tag names (and tag combination name) to view count.

```java
    @GetMapping("/MostViewedTags")
    public ResponseEntity<List<Map<String, Object>>> getMostViewedTags()
```




```java
    @GetMapping("/QuestionUserNumber") //参与问题的
    public ResponseEntity<Integer> getQuestionUserDistribution() 
```




```java
    @GetMapping("/AnswerUserNumber")
    public ResponseEntity<Integer> getAnswerUserNumber() 
```




```java
    @GetMapping("/CommentUserNumber")
    public ResponseEntity<Integer> getCommentUserNumber() 
```



Return List of Tuple (List of 2), One of number is unique user participating in answer, the other is in comment.
Param available, `num=x` means return first x data.

Usage: `http://localhost:9090/SingleQuestionUserDistribution?num=5`

```java
    @GetMapping("/SingleQuestionUserDistribution")
    public ResponseEntity<List<List<Integer>>> getSingleQuestionUserDistribution(
            @RequestParam(value = "num", required = false, defaultValue = "1000") int num
    ) 
```



```java
    @GetMapping("/NumberOfPostsWithoutUserID")
    public ResponseEntity<Integer> getNumberOfPostsWithoutUserID() 
```



```java
    @GetMapping("/TotalNumberOfUsers")
    public ResponseEntity<Integer> getTotalNumberOfUsers() 
```



```java
    @GetMapping("/MostActiveUsers")
    public ResponseEntity<List<JsonNode>> getMostActiveUsers() throws IOException 
```



```java
    @GetMapping("/FrequentlyDiscussedClasses")
    public ResponseEntity<Map<String, Integer>> getFrequentlyDiscussedClasses() 
```



```java
    @GetMapping("/FrequentlyDiscussedMethods")
    public ResponseEntity<Map<List<String>, Integer>> getFrequentlyDiscussedMethods()

```




```java
    @GetMapping("/ReputationScoreRelation")
    public ResponseEntity<JsonNode> getReputationScoreRelation()
```



```java
    @GetMapping("/ReputationAcceptanceRelation")
    public ResponseEntity<JsonNode> getReputationAcceptanceRelation()
```



RESTful API to get user's total score in posts in database.

Usage: `http://localhost:9090/UserTotalScore/5316516`

```java
    @GetMapping("/UserTotalScore/{UserId}")
    public ResponseEntity<Integer> getUserTotalScore(@PathVariable("UserId") int userId)
```
