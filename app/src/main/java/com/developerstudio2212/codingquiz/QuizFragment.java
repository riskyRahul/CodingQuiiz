package com.developerstudio2212.codingquiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class QuizFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "QUIZ_FRAGMENT_LOG";
    private NavController navController;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String quizName;

    private String quizId;
    private String currentUserId;
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

    private int correctAnswers = 0;
    private int wrongAnswers = 0;
    private int notAnswered = 0;

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
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();

        //Get User ID
        if(firebaseAuth.getCurrentUser() != null){
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        } else {
            //Go Back to Home Page
        }

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
        quizName = QuizFragmentArgs.fromBundle(getArguments()).getQuizName();

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
        nextBtn.setOnClickListener(this);
    }

    private void loadUI() {
        quizTital.setText(quizName);
        questionText.setText("Load First qusition");

        enableOption();
        loadQuestion(1);
    }

    private void loadQuestion(int quesNum) {
        questionNumber.setText(quesNum + "");
        questionText.setText(questionsToAnswer.get(quesNum - 1).getQuestion());

        optionOneBtn.setText(questionsToAnswer.get(quesNum - 1).getOption_a());
        optionTwoBtn.setText(questionsToAnswer.get(quesNum - 1).getOption_b());
        optionThreeBtn.setText(questionsToAnswer.get(quesNum - 1).getOption_c());

        canAnswer = true;
        currentQuestion = quesNum;


        startTimer(quesNum);

    }

    private void startTimer(int questionNumber) {
        final Long timeToAnswer = questionsToAnswer.get(questionNumber - 1).getTimer();
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

                questionFeedback.setText("Time Up! No answer was submitted.");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
                notAnswered++;
                showNextBtn();
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
                verifyAnswer(optionOneBtn);
                break;
            case R.id.quiz_option_two:
                verifyAnswer(optionTwoBtn);
                break;
            case R.id.quiz_option_three:
                verifyAnswer(optionThreeBtn);
                break;
            case R.id.quiz_next_btn:
                if (currentQuestion == totalQuestionsToAnswer) {
                    //Load Results
                    submitResults();
                } else {
                    currentQuestion++;
                    loadQuestion(currentQuestion);
                    resetOptions();
                }
                break;
        }
    }

    private void resetOptions() {
        optionOneBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionTwoBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));
        optionThreeBtn.setBackground(getResources().getDrawable(R.drawable.outline_light_btn_bg, null));

        optionOneBtn.setTextColor(getResources().getColor(R.color.light_text, null));
        optionTwoBtn.setTextColor(getResources().getColor(R.color.light_text, null));
        optionThreeBtn.setTextColor(getResources().getColor(R.color.light_text, null));

        questionFeedback.setVisibility(View.INVISIBLE);
        nextBtn.setVisibility(View.INVISIBLE);
        nextBtn.setEnabled(false);
    }

    private void submitResults() {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("correct", correctAnswers);
        resultMap.put("wrong", wrongAnswers);
        resultMap.put("unanswered", notAnswered);

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(currentUserId).set(resultMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Go To Results Page
                    QuizFragmentDirections.ActionQuizFragmentToResultFragment action = QuizFragmentDirections.actionQuizFragmentToResultFragment();
                    action.setQuizId(quizId);
                    navController.navigate(action);
                } else {
                    //Show Error
                    quizTitle.setText(task.getException().getMessage());
                }
            }
        });
        
    }

    private void verifyAnswer(Button selectedAnswer) {
        if (canAnswer) {

            selectedAnswer.setTextColor(getResources().getColor(R.color.light_text, null));

            if (questionsToAnswer.get(currentQuestion - 1).getAnswer().equals(selectedAnswer.getText())) {
                correctAnswers++;
                selectedAnswer.setBackground(getResources().getDrawable(R.drawable.correct_answer, null));
                //Set Feedback Text
                questionFeedback.setText("Correct Answer");
                questionFeedback.setTextColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                wrongAnswers++;
                selectedAnswer.setBackground(getResources().getDrawable(R.drawable.wrong_answer, null));
                //Set Feedback Text
                questionFeedback.setText("Wrong Answer \n \n Correct Answer : " + questionsToAnswer.get(currentQuestion - 1).getAnswer());
                questionFeedback.setTextColor(getResources().getColor(R.color.colorAccent, null));

            }
            canAnswer = false;

            countDownTimer.cancel();

            showNextBtn();
        }
    }

    private void showNextBtn() {
        if (currentQuestion == totalQuestionsToAnswer) {
            nextBtn.setText("Submit Results");
        }
        questionFeedback.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        nextBtn.setEnabled(true);
    }
}
