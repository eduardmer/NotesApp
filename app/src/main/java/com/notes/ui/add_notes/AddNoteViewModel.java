package com.notes.ui.add_notes;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;
import com.notes.data.entities.Notes;
import com.notes.data.NotesRepository;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class AddNoteViewModel extends ViewModel {

    private final NotesRepository notesRepository;
    private final ObservableField<Boolean> setReminder;
    private final ObservableField<Calendar> reminderTime;

    @Inject
    public AddNoteViewModel(NotesRepository notesRepository){
        this.notesRepository = notesRepository;
        setReminder = new ObservableField<>(false);
        reminderTime = new ObservableField<>(new GregorianCalendar());
    }

    public Single<Long> insertNote(Notes note) {
        return notesRepository.insertNote(note).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public ObservableField<Boolean> getSetReminder() {
        return setReminder;
    }

    public void setReminder(Boolean value){
        setReminder.set(value);
    }

    public ObservableField<Calendar> getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long date, int hour, int minute) {
        Calendar time = new GregorianCalendar();
        time.setTimeInMillis(date);
        time.set(Calendar.HOUR, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);
        reminderTime.set(time);
    }

    public long getReminderTimeInMillis() {
        return Objects.requireNonNull(reminderTime.get()).getTimeInMillis();
    }

}
