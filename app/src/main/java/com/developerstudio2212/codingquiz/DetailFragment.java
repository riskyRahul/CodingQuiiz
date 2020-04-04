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

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private QuizListViewModel quizListViewModel;
    private NavController navController;
    private int position;

    private ImageView detailImage;
    private TextView detailTital;
    private TextView detailDesc;
    private TextView detailDiff;
    private TextView detailQustions;
    private Button detailBtn;

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
        detailBtn.setOnClickListener(this);
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
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_start_btn:
                DetailFragmentDirections.ActionDetailFragmentToQuizFragment actionDetailFragmentToQuizFragment=DetailFragmentDirections.actionDetailFragmentToQuizFragment();
                actionDetailFragmentToQuizFragment.setPosition(position);
                navController.navigate(actionDetailFragmentToQuizFragment);
                break;
        }
    }
}
