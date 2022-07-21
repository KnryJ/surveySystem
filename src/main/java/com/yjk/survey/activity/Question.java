package com.yjk.survey.activity;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    public final Integer quid;
    public final String question;
    public final List<String> options;
}
