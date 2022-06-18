package com.notes.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM Notes ORDER BY createdDate DESC")
    Flowable<List<Notes>> getNotes();

    @Insert
    Completable insertNote(Notes note);

    @Delete
    Completable deleteNote(Notes note);



}
