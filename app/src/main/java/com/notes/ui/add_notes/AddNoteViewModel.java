package com.notes.ui.add_notes;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.notes.data.Notes;
import com.notes.data.NotesRepository;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class AddNoteViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private final ObservableField<Boolean> setReminder;

    @Inject
    public AddNoteViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
        setReminder = new ObservableField<>(false);
    }

    public ObservableField<Boolean> getSetReminder() {
        return setReminder;
    }

    public void setReminder(Boolean value){
        setReminder.set(value);
    }

    public Completable insertNote(Notes note) {
        return notesRepository.insertNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
