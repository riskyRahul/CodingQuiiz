package com.developerstudio2212.codingquiz;

import com.google.firebase.firestore.DocumentId;

public class QustionsModel {
    @DocumentId
    private String question_id;
    private String question;
    private String answer;
    private String option_a;
    private String option_b;
    private String option_c;
    private long timer;

    public QustionsModel() {
    }

    public QustionsModel(String question_id, String question, String answer, String option_a, String option_b, String option_c, long timer) {
        this.question_id = question_id;
        this.question = question;
        this.answer = answer;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.timer = timer;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }
}
