package com.notes.ui.main;

public interface AdapterListener {

    void shareNote(String title, String description);

    void createPDF(String title, String description);

    void deleteNote(int id);

}
