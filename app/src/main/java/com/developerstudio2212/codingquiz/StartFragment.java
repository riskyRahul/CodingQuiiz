package com.developerstudio2212.codingquiz;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {
    private static final String START_TAG = "START_TAG";

    public StartFragment() {}

    private ProgressBar startProgressBar;
    private TextView startFeedback;
    private FirebaseAuth firebaseAuth;

    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        startProgressBar=view.findViewById(R.id.contentLoadingProgressBar);
        startFeedback = view.findViewById(R.id.text_feedback);
        startFeedback.setText("Checking user Account...");
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser==null){
            startFeedback.setText("Creacting Account...");
            firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startFeedback.setText("Account created...");
                       navController.navigate(R.id.action_startFragment_to_listFragment);
                    }else {
                        Log.d(START_TAG,"Start log:-"+task.getException());
                    }
                }
            });
        }else {
            startFeedback.setText("Allredy logdin...");
            navController.navigate(R.id.action_startFragment_to_listFragment);
        }
    }
}
