package com.notes.ui.add_notes;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.notes.data.Notes;
import com.notes.data.NotesRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class AddNoteViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private final ObservableField<Boolean> setReminder;
    private final ObservableField<Calendar> reminderTime;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

    @Inject
    public AddNoteViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
        setReminder = new ObservableField<>(false);
        reminderTime = new ObservableField<>(new GregorianCalendar());
    }

    public ObservableField<Boolean> getSetReminder() {
        return setReminder;
    }

    public void setReminder(Boolean value){
        setReminder.set(value);
    }

    public String getReminderTime() {
        return dateFormat.format(Objects.requireNonNull(reminderTime.get()).getTimeInMillis());
    }

    public long getReminderTimeInMillis() {
        return Objects.requireNonNull(reminderTime.get()).getTimeInMillis();
    }

    public void setReminderDate(long date) {
        Calendar calendar = reminderTime.get();
        Objects.requireNonNull(calendar).setTimeInMillis(date);
        Objects.requireNonNull(reminderTime.get()).setTimeInMillis(date);
    }

    public void setReminderTime(int hour, int minute) {
        Calendar calendar = reminderTime.get();
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        reminderTime.set(calendar);
    }

    public Completable insertNote(Notes note) {
        return notesRepository.insertNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
