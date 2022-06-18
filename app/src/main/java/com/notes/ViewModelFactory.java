package com.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.notes.data.NotesRepository;
import com.notes.ui.add_notes.AddNoteViewModel;
import com.notes.ui.main.MainViewModel;
import javax.inject.Inject;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final NotesRepository notesRepository;

    @Inject
    public ViewModelFactory(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class))
            return (T) new MainViewModel(notesRepository);
        else
            return (T) new AddNoteViewModel();
    }
}
