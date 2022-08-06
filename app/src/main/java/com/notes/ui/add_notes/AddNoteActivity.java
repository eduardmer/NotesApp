package com.notes.ui.add_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.notes.AlarmReceiver;
import com.notes.R;
import com.notes.data.entities.Notes;
import com.notes.databinding.ActivityAddNoteBinding;
import com.notes.utils.Constants;
import java.util.Date;
import java.util.Objects;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class AddNoteActivity extends AppCompatActivity {

    ActivityAddNoteBinding binding;
    AddNoteViewModel viewModel;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewModel =  new ViewModelProvider(this).get(AddNoteViewModel.class);
        binding.setViewModel(viewModel);

        binding.reminderSwitch.setOnCheckedChangeListener(((compoundButton, b) -> viewModel.setReminder(b)));
        binding.dateText.setOnClickListener(v -> showDatePicker());
        binding.cancelButton.setOnClickListener(v -> onBackPressed());
        binding.saveButton.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.titleText.getText()).toString().isEmpty() || Objects.requireNonNull(binding.descriptionText.getText()).toString().isEmpty())
                Toast.makeText(this, R.string.field_validation, Toast.LENGTH_SHORT).show();
            else if (viewModel.getSetReminder().get() && viewModel.getReminderTimeInMillis() < System.currentTimeMillis() + 60 * 1000)
                Toast.makeText(this, R.string.time_validation, Toast.LENGTH_SHORT).show();
            else {
                insertNote();
            }
        });
    }

    private void insertNote() {
        compositeDisposable.add(viewModel.insertNote(new Notes(binding.titleText.getText().toString(), binding.descriptionText.getText().toString(), new Date().getTime(), viewModel.getSetReminder().get() ? viewModel.getReminderTimeInMillis() : 0)).subscribe(
                id -> {
                    if (viewModel.getSetReminder().get())
                        setAlarm(id.intValue(), viewModel.getReminderTimeInMillis());
                    onBackPressed();
                    },
                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()));
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.select_date).build();
        datePicker.addOnPositiveButtonClickListener(date -> {
            viewModel.setReminderDate(date);
            showTimePicker();
        });
        datePicker.show(getSupportFragmentManager(), Constants.DATE_PICKER_TAG);
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setTitleText(R.string.select_time).build();
        timePicker.addOnPositiveButtonClickListener(v -> viewModel.setReminderTime(timePicker.getHour(), timePicker.getMinute()));
        timePicker.show(getSupportFragmentManager(), Constants.TIME_PICKER_TAG);
    }

    private void setAlarm(int noteId, long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, noteId, intent, 0);
        AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(alarmTime, null);
        alarmManager.setAlarmClock(ac, pendingIntent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}