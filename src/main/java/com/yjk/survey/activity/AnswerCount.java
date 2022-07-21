package com.yjk.survey.activity;

import lombok.Data;

@Data
public class AnswerCount {
    public final Integer quid;
    public Integer countOfA;
    public Integer countOfB;
    public Integer countOfC;
    public Integer countOfD;

    public AnswerCount(int quid) {
        this.quid = quid;
        this.countOfA = 0;
        this.countOfB = 0;
        this.countOfC = 0;
        this.countOfD = 0;
    }
}
