package com.notes.ui.add_notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.notes.R;
import com.notes.data.Notes;
import com.notes.databinding.ActivityAddNoteBinding;
import java.util.Date;
import java.util.Objects;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class AddNoteActivity extends AppCompatActivity {

    ActivityAddNoteBinding binding;
    final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        AddNoteViewModel viewModel =  new ViewModelProvider(this).get(AddNoteViewModel.class);
        binding.setViewModel(viewModel);

        binding.reminderSwitch.setOnCheckedChangeListener(((compoundButton, b) -> viewModel.setReminder(b)));
        binding.dateContainer.setOnClickListener(v -> MaterialDatePicker.Builder.datePicker().setTitleText("Select date").build().show(getSupportFragmentManager(), "tag"));
        binding.cancelButton.setOnClickListener(v -> goBack());
        binding.saveButton.setOnClickListener(v -> {
            if (binding.titleText.getText().toString().isEmpty() || binding.descriptionText.getText().toString().isEmpty())
                Toast.makeText(this, getString(R.string.field_validation), Toast.LENGTH_SHORT).show();
            else
                compositeDisposable.add(viewModel.insertNote(
                        new Notes(binding.titleText.getText().toString(), binding.descriptionText.getText().toString(), new Date().getTime(), 0))
                        .subscribe(
                                this::goBack,
                                error -> Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                        ));
        });
    }

    private void goBack(){
        onBackPressed();
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