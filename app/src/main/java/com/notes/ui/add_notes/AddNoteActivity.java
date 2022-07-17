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
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.notes.AlarmReceiver;
import com.notes.R;
import com.notes.data.Notes;
import com.notes.databinding.ActivityAddNoteBinding;
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
    private boolean checkTime;
    int hour, minute;
    long date = 0, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AddNoteViewModel viewModel =  new ViewModelProvider(this).get(AddNoteViewModel.class);
        binding.setViewModel(viewModel);

        checkTime = false;
        binding.reminderSwitch.setOnCheckedChangeListener(((compoundButton, b) -> viewModel.setReminder(b)));
        binding.dateText.setOnClickListener(v -> showDatePicker());
        binding.cancelButton.setOnClickListener(v -> goBack());
        binding.saveButton.setOnClickListener(v -> {
            if (binding.titleText.getText().toString().isEmpty() || binding.descriptionText.getText().toString().isEmpty())
                Toast.makeText(this, R.string.field_validation, Toast.LENGTH_SHORT).show();
            else if (viewModel.getSetReminder().get() && !checkTime)
                Toast.makeText(this, R.string.time_validation, Toast.LENGTH_SHORT).show();
            else {
                Date rDate = new Date(date);
                Calendar calendar = new GregorianCalendar();
                calendar.set(1900 + rDate.getYear(), rDate.getMonth(), rDate.getDate(), hour, minute);
                calendar.set(Calendar.YEAR, 1900 + rDate.getYear());
                calendar.set(Calendar.MONTH, rDate.getMonth());
                calendar.set(Calendar.SECOND, 0);
                setAlarm(calendar.getTimeInMillis());
                Log.i("pergjigja", rDate.getYear() +" " + rDate.getMonth() + " " + rDate.getDate() + " " + hour + " " + minute);
                compositeDisposable.add(viewModel.insertNote(new Notes(binding.titleText.getText().toString(), binding.descriptionText.getText().toString(), new Date().getTime(), calendar.getTimeInMillis()))
                        .subscribe(
                                this::goBack,
                                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                        ));
            }
        });
    }

    private void goBack(){
        onBackPressed();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build();
        datePicker.addOnPositiveButtonClickListener(date -> {
            setDate(date);
            showTimePicker();
        });
        datePicker.show(getSupportFragmentManager(), "tag");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).setTitleText("Select time").build();
        timePicker.addOnPositiveButtonClickListener(v -> {
            checkTime = true;
            hour = timePicker.getHour();
            minute = timePicker.getMinute();
        });
        timePicker.show(getSupportFragmentManager(), "tag");
    }

    private void setDate(Long date) {
        this.date = date;
    }

    private void setAlarm(long alarmTime) {
        Log.i("pergjigja", alarmTime+"");
        Log.i("pergjigja", System.currentTimeMillis()+10*1000 + "");
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