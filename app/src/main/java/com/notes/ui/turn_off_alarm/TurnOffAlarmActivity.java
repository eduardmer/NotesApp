package com.notes.ui.turn_off_alarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.notes.AlarmService;
import com.notes.R;
import com.notes.databinding.ActivityTurnOffAlarmBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TurnOffAlarmActivity extends AppCompatActivity {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTurnOffAlarmBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_turn_off_alarm);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        ((KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE)).requestDismissKeyguard(this, null);
        long time = getIntent().getLongExtra("time",0);
        binding.timeText.setText(dateFormat.format(new Date(time)));
        binding.stopButton.setOnClickListener(v -> {
            stopService(new Intent(this, AlarmService.class));
            finish();
        });
    }
}