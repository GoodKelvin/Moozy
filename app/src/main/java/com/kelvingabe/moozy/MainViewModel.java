package com.kelvingabe.moozy;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.kelvingabe.moozy.database.MovieDatabase;
import com.kelvingabe.moozy.database.MovieEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<MovieEntry>> movies;
    public MainViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase movieDatabase = MovieDatabase.getInstance(getApplication());
        movies = movieDatabase.movieDao().loadAllMovies();
    }

    public LiveData<List<MovieEntry>> getMovies(){
        return  movies;
    }
}
