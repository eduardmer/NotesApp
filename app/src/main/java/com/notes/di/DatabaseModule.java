package com.notes.di;

import android.content.Context;

import androidx.room.Room;

import com.notes.data.database.AppDatabase;
import com.notes.data.database.NotesDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Singleton
    @Provides
    public AppDatabase getDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "NotesDatabase").build();
    }

    @Provides
    public NotesDao getNotesDao(AppDatabase database){
        return database.getNotesDao();
    }

}
