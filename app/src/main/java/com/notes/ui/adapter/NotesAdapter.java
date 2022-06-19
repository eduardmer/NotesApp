package com.notes.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.notes.BR;
import com.notes.R;
import com.notes.data.Notes;
import com.notes.databinding.ItemNoteBinding;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    final List<Notes> notes;

    public NotesAdapter(List<Notes> notes){
        this.notes = notes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        ItemNoteBinding binding;

        public NoteViewHolder(@NonNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Notes note) {
            binding.setVariable(BR.data, note);
            binding.dateText.setText("18:24");
            binding.executePendingBindings();
            binding.settingsButton.setOnClickListener(v -> showPopUpMenu(v.getContext(), v));
        }

        @SuppressLint("NonConstantResourceId")
        private void showPopUpMenu(Context context, View button) {
            PopupMenu popupMenu = new PopupMenu(context, button);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setForceShowIcon(true);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.save_button:
                        Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.share_button:
                        Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.delete_button:
                        Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            });
            popupMenu.show();
        }

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Notes item = notes.get(position);
        holder.bind(item);
    }

    public void update(List<Notes> notes){
        this.notes.clear();
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}
