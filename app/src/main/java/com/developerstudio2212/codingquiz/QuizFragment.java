package com.developerstudio2212.codingquiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "QUIZ_FRAGMENT_LOG";

    private FirebaseFirestore firebaseFirestore;
    private String quizId;
    private TextView quizTital;

    private List<QustionsModel> allQuestionsList = new ArrayList<>();
    private List<QustionsModel> questionsToAnswer = new ArrayList<>();
    private Long totalQuestionsToAnswer = 0L;
    private CountDownTimer countDownTimer;

    private TextView quizTitle;
    private Button optionOneBtn;
    private Button optionTwoBtn;
    private Button optionThreeBtn;
    private Button nextBtn;
    private ImageButton closeBtn;
    private TextView questionFeedback;
    private TextView questionText;
    private TextView questionTime;
    private ProgressBar questionProgress;
    private TextView questionNumber;

    private boolean canAnswer = false;
    private int currentQuestion = 0;

    public QuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //UI Initialize
        quizTitle = view.findViewById(R.id.quiz_title);
        optionOneBtn = view.findViewById(R.id.quiz_option_one);
        optionTwoBtn = view.findViewById(R.id.quiz_option_two);
        optionThreeBtn = view.findViewById(R.id.quiz_option_three);
        nextBtn = view.findViewById(R.id.quiz_next_btn);
        questionFeedback = view.findViewById(R.id.quiz_question_feedback);
        questionText = view.findViewById(R.id.quiz_question);
        questionTime = view.findViewById(R.id.quiz_question_time);
        questionProgress = view.findViewById(R.id.quiz_question_progress);
        questionNumber = view.findViewById(R.id.quiz_question_number);

        quizTital = view.findViewById(R.id.quiz_title);
        firebaseFirestore = FirebaseFirestore.getInstance();

        quizId = QuizFragmentArgs.fromBundle(getArguments()).getQuizid();
        totalQuestionsToAnswer = QuizFragmentArgs.fromBundle(getArguments()).getTotalQusitions();
        firebaseFirestore.collection("QuizList").document(quizId).collection("questions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    allQuestionsList = task.getResult().toObjects(QustionsModel.class);
                    pickQuestions();
                    loadUI();
                } else {
                    quizTital.setText("Error for lodding Data...");
                }
            }
        });
        optionOneBtn.setOnClickListener(this);
        optionTwoBtn.setOnClickListener(this);
        optionThreeBtn.setOnClickListener(this);
    }

    private void loadUI() {
        quizTital.setText("Qusition Data laded");
        questionText.setText("Load First qusition");

        enableOption();
        loadQuestion(1);
    }

    private void loadQuestion(int quesNum) {
        questionNumber.setText(quesNum + "");
        questionText.setText(questionsToAnswer.get(quesNum).getQuestion());

        optionOneBtn.setText(questionsToAnswer.get(quesNum).getOption_a());
        optionTwoBtn.setText(questionsToAnswer.get(quesNum).getOption_b());
        optionThreeBtn.setText(questionsToAnswer.get(quesNum).getOption_c());

        canAnswer = true;
        currentQuestion = quesNum;


        startTimer(quesNum);

    }

    private void startTimer(int questionNumber) {
        final Long timeToAnswer = questionsToAnswer.get(questionNumber).getTimer();
        String str = Long.toString(timeToAnswer);
        questionTime.setText(str);

        questionProgress.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(timeToAnswer * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                questionTime.setText(millisUntilFinished / 1000 + "");

                Long percent = millisUntilFinished / (timeToAnswer * 10);
                questionProgress.setProgress(percent.intValue());
            }

            @Override
            public void onFinish() {
                canAnswer = false;
            }
        };
        countDownTimer.start();

    }

    private void enableOption() {
        optionOneBtn.setVisibility(View.VISIBLE);
        optionTwoBtn.setVisibility(View.VISIBLE);
        optionThreeBtn.setVisibility(View.VISIBLE);

        optionOneBtn.setEnabled(true);
        optionTwoBtn.setEnabled(true);
        optionThreeBtn.setEnabled(true);

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);


    }

    private void pickQuestions() {
        for (int i = 0; i < totalQuestionsToAnswer; i++) {
            int randomNumber = getRandomInt(0, allQuestionsList.size());
            questionsToAnswer.add(allQuestionsList.get(randomNumber));
            allQuestionsList.remove(randomNumber);
            Log.d("QUIZ_FRAGMENT_LOG", "Question " + i + " : " + questionsToAnswer.get(i).getQuestion());
        }
    }

    private int getRandomInt(int min, int max) {
        return ((int) (Math.random() * (max - min))) + min;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quiz_option_one:
                answerSelected(optionOneBtn.getText());
                break;
            case R.id.quiz_option_two:
                answerSelected(optionTwoBtn.getText());
                break;
            case R.id.quiz_option_three:
                answerSelected(optionThreeBtn.getText());
                break;
        }
    }

    private void answerSelected(CharSequence selectedAnswer) {
        if (canAnswer) {
            if (questionsToAnswer.get(currentQuestion).getAnswer().equals(selectedAnswer)) {
                Log.d(TAG, "Correct Answer");
            } else {
                Log.d(TAG, "Wrong Answer");
            }
            canAnswer = false;
        }
    }
}
