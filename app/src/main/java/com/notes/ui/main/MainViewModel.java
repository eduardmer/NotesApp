package com.notes.ui.main;

import androidx.lifecycle.ViewModel;
import com.notes.data.Notes;
import com.notes.data.NotesRepository;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MainViewModel extends ViewModel {

    private final NotesRepository notesRepository;

    @Inject
    public MainViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
    }

    public Flowable<List<Notes>> getNotes() {
        return notesRepository.getNotes().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteNote(int id) {
        return notesRepository.deleteNote(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
