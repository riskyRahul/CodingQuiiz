package com.developerstudio2212.codingquiz;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class QuizListViewModel extends ViewModel implements FirebaseRepositery.OnFirestoreTaskComplete {

    private MutableLiveData<List<QuizListModel>> quizlistmodeldata = new MutableLiveData<>();

    public LiveData<List<QuizListModel>> getQuizlistmodeldata() {
        return quizlistmodeldata;
    }

//    public void setQuizlistmodeldata(MutableLiveData<List<QuizListModel>> quizlistmodeldata) {
//        this.quizlistmodeldata = quizlistmodeldata;
//    }

    private FirebaseRepositery firebaseRepositery = new FirebaseRepositery(this);

    public QuizListViewModel() {
        firebaseRepositery.getQuizData();
    }

    @Override
    public void quizListDataAdded(List<QuizListModel> quizListModelList) {
        quizlistmodeldata.setValue(quizListModelList);
    }

    @Override
    public void onError(Exception e) {

    }
}
