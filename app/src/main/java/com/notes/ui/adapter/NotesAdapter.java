package com.notes.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import com.notes.BR;
import com.notes.R;
import com.notes.data.entities.Notes;
import com.notes.databinding.ItemNoteBinding;
import com.notes.ui.main.AdapterListener;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    final AdapterListener listener;
    final List<Notes> notes;

    public NotesAdapter(AdapterListener listener, List<Notes> notes){
        this.listener = listener;
        this.notes = notes;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        ItemNoteBinding binding;

        public NoteViewHolder(@NonNull ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AdapterListener listener, Notes note) {
            binding.setVariable(BR.data, note);
            binding.dateText.setText("18:24");
            binding.executePendingBindings();
            binding.settingsButton.setOnClickListener(v -> showPopUpMenu(listener, note, v.getContext(), v));
        }

        @SuppressLint("NonConstantResourceId")
        private void showPopUpMenu(AdapterListener listener, Notes note, Context context, View button) {
            PopupMenu popupMenu = new PopupMenu(context, button);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setForceShowIcon(true);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.save_button:
                        listener.createPDF(note.getTitle(), note.getDescription());
                        break;
                    case R.id.share_button:
                        listener.shareNote(note.getTitle(), note.getDescription());
                        break;
                    case R.id.delete_button:
                        listener.deleteNote(note.getId());
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
        holder.bind(listener, item);
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
