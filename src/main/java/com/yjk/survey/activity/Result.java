package com.yjk.survey.activity;

import lombok.Data;

import java.util.LinkedHashMap;

@Data
public class Result {
    public final Integer quid;
    public final String question;
    public final LinkedHashMap<String, Integer> options;

    public Result(Question question, AnswerCount answerCount) {
        this.quid = question.quid;
        this.question = question.question;
        this.options = new LinkedHashMap<>();
        this.options.put(question.options.get(0), answerCount.countOfA);
        this.options.put(question.options.get(1), answerCount.countOfB);
        this.options.put(question.options.get(2), answerCount.countOfC);
        this.options.put(question.options.get(3), answerCount.countOfD);
    }
}
