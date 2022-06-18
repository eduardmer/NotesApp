package com.notes.data;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class NotesRepository {

    private final NotesDao notesDao;

    @Inject
    public NotesRepository(NotesDao notesDao){
        this.notesDao = notesDao;
    }

    public Flowable<List<Notes>> getNotes(){
        return notesDao.getNotes();
    }

    public Completable insertNote(Notes note){
       return notesDao.insertNote(note);
    }

    public Completable deleteNote(Notes note){
        return  notesDao.deleteNote(note);
    }

}
