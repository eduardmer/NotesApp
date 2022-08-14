package com.notes.data;

import com.notes.data.database.NotesDao;
import com.notes.data.entities.Notes;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class NotesRepository {

    private final NotesDao notesDao;

    @Inject
    public NotesRepository(NotesDao notesDao){
        this.notesDao = notesDao;
    }

    public Flowable<List<Notes>> getNotes(){
        return notesDao.getNotes();
    }

    public Single<Long> insertNote(Notes note) {
        return notesDao.insertNote(note);
    }

    public Completable deleteNote(int id){
        return  notesDao.deleteNote(id);
    }

}
