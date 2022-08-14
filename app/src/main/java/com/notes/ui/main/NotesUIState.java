package com.notes.ui.main;

import com.notes.data.entities.Notes;
import java.util.List;

public class NotesUIState {

    private final boolean isLoading;
    private final int message;
    private final List<Notes> notes;

    public NotesUIState(boolean isLoading, int message, List<Notes> notes){
        this.isLoading = isLoading;
        this.message = message;
        this.notes = notes;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public int getMessage() {
        return message;
    }

    public List<Notes> getNotes() {
        return notes;
    }
}
