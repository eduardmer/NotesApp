package com.notes.ui.main;

public interface AdapterListener {

    void shareNote();

    void createPDF(String title, String description);

    void deleteNote(int id);

}
