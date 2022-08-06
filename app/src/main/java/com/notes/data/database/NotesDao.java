package com.notes.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.notes.data.entities.Notes;

import java.util.List;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM Notes ORDER BY createdDate DESC")
    Flowable<List<Notes>> getNotes();

    @Insert
    Single<Long> insertNote(Notes note);

    @Query("DELETE FROM Notes WHERE id=:id")
    Completable deleteNote(int id);

}
