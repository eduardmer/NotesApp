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
import com.notes.data.Notes;
import com.notes.databinding.ActivityAddNoteBinding;
import com.notes.utils.Constants;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class AddNoteActivity extends AppCompatActivity {

    ActivityAddNoteBinding binding;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();
    final Calendar reminderTime = new GregorianCalendar();
    private boolean canSetReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AddNoteViewModel viewModel =  new ViewModelProvider(this).get(AddNoteViewModel.class);
        binding.setViewModel(viewModel);

        binding.reminderSwitch.setOnCheckedChangeListener(((compoundButton, b) -> viewModel.setReminder(b)));
        binding.dateText.setOnClickListener(v -> showDatePicker());
        binding.cancelButton.setOnClickListener(v -> goBack());
        binding.saveButton.setOnClickListener(v -> {
            if (binding.titleText.getText().toString().isEmpty() || binding.descriptionText.getText().toString().isEmpty())
                Toast.makeText(this, R.string.field_validation, Toast.LENGTH_SHORT).show();
            else if (viewModel.getSetReminder().get() && !canSetReminder)
                Toast.makeText(this, R.string.time_validation, Toast.LENGTH_SHORT).show();
            else {
                compositeDisposable.add(viewModel.insertNote(new Notes(binding.titleText.getText().toString(), binding.descriptionText.getText().toString(), new Date().getTime(), viewModel.getSetReminder().get() ? reminderTime.getTimeInMillis() : 0))
                        .subscribe(
                                () -> {
                                    if (viewModel.getSetReminder().get())
                                        setAlarm(reminderTime.getTimeInMillis());
                                    goBack();
                                },
                                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                        ));
            }
        });
    }

    private void goBack(){
        onBackPressed();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(R.string.select_date).build();
        datePicker.addOnPositiveButtonClickListener(date -> {
            setReminderTime(date, -1, -1);
            showTimePicker();
        });
        datePicker.show(getSupportFragmentManager(), Constants.DATE_PICKER_TAG);
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setTitleText(R.string.select_time).build();
        timePicker.addOnPositiveButtonClickListener(v -> setReminderTime(-1, timePicker.getHour(), timePicker.getMinute()));
        timePicker.show(getSupportFragmentManager(), Constants.TIME_PICKER_TAG);
    }

    private void setReminderTime(long date, int hour, int minute) {
        if (date != -1) {
            reminderTime.setTime(new Date(date));
            canSetReminder = false;
        }
        else {
            reminderTime.set(Calendar.HOUR, hour);
            reminderTime.set(Calendar.MINUTE, minute);
            reminderTime.set(Calendar.SECOND, 0);
            canSetReminder = true;
        }
    }

    private void setAlarm(long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
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