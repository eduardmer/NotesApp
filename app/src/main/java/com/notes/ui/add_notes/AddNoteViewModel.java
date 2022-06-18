package com.notes.ui.add_notes;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddNoteViewModel extends ViewModel {

    private final ObservableField<Boolean> setReminder;

    @Inject
    public AddNoteViewModel(){
        setReminder = new ObservableField<>();
    }

    public ObservableField<Boolean> getSetReminder() {
        return setReminder;
    }

    public void setReminder(Boolean value){
        setReminder.set(value);
    }
}
