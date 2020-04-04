package com.developerstudio2212.codingquiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private QuizListViewModel quizListViewModel;
    private NavController navController;
    private Long totalQusitions = 0L;
    private int position;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    private ImageView detailImage;
    private TextView detailTital;
    private TextView detailDesc;
    private TextView detailDiff;
    private TextView detailQustions;
    private TextView detailsScore;
    private Button detailBtn;
    private String quizId;


    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();
        Log.d("APP_LOG", "position" + position);

        detailImage = view.findViewById(R.id.details_image);
        detailTital = view.findViewById(R.id.details_title);
        detailDesc = view.findViewById(R.id.details_desc);
        detailDiff = view.findViewById(R.id.details_difficulty_text);
        detailQustions = view.findViewById(R.id.details_questions_text);
        detailBtn = view.findViewById(R.id.details_start_btn);
        detailsScore = view.findViewById(R.id.details_score_text);
        detailBtn.setOnClickListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quizListViewModel = new ViewModelProvider(getActivity()).get(QuizListViewModel.class);
        quizListViewModel.getQuizlistmodeldata().observe(getViewLifecycleOwner(), new Observer<List<QuizListModel>>() {
            @Override
            public void onChanged(List<QuizListModel> quizListModelList) {

                Glide.with(getContext())
                        .load(quizListModelList.get(position).getImage())
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .into(detailImage);
                detailTital.setText(quizListModelList.get(position).getName());
                detailDesc.setText(quizListModelList.get(position).getDesc());
                detailDiff.setText(quizListModelList.get(position).getLevel());
                String str = Long.toString(quizListModelList.get(position).getQuestions());
                detailQustions.setText(str);
                quizId = quizListModelList.get(position).getQuiz_id();
                totalQusitions = quizListModelList.get(position).getQuestions();

                loadResultsData();
            }
        });
    }

    private void loadResultsData() {
        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results")
                .document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null && document.exists()){
                        //Get Result
                        Long correct = document.getLong("correct");
                        Long wrong = document.getLong("wrong");
                        Long missed = document.getLong("unanswered");

                        //Calculate Progress
                        Long total = correct + wrong + missed;
                        Long percent = (correct*100)/total;

                        detailsScore.setText(percent + "%");
                    } else {
                        //Document Doesn't Exist, and result should stay N/A
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_start_btn:
                DetailFragmentDirections.ActionDetailFragmentToQuizFragment actionDetailFragmentToQuizFragment=DetailFragmentDirections.actionDetailFragmentToQuizFragment();
                actionDetailFragmentToQuizFragment.setTotalQusitions(totalQusitions);
                actionDetailFragmentToQuizFragment.setQuizid(quizId);
                navController.navigate(actionDetailFragmentToQuizFragment);
                break;
        }
    }
}
