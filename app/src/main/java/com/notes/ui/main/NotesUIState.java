package com.notes.ui.main;

import com.notes.data.entities.Notes;

import java.util.List;

public class NotesUIState {

    private boolean isLoading;
    private int message;
    private List<Notes> notes;

    public NotesUIState(boolean isLoading, int message, List<Notes> notes){
        this.isLoading = isLoading;
        this.message = message;
        this.notes = notes;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public int getMessage() {
        return message;
    }

    public List<Notes> getNotes() {
        return notes;
    }
}
