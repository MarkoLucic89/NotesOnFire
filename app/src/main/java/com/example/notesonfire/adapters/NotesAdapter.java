package com.example.notesonfire.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notesonfire.R;
import com.example.notesonfire.models.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NotesViewHolder> {

    private OnNoteListener onNoteListener;


    public NotesAdapter(OnNoteListener onNoteListener, @NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull Note model) {
        holder.textViewItemTitle.setText(model.getNoteTitle());
        holder.textViewItemDateTime.setText(model.getNoteDate());
        holder.textViewItemText.setText(model.getNoteText());
        holder.cardViewItem.setCardBackgroundColor(model.getNoteColor());
        if (model.getNoteImagePath() != null) {
            holder.imageViewItemNoteImage.setVisibility(View.VISIBLE);
            Picasso.get().load(model.getNoteImagePath()).into(holder.imageViewItemNoteImage);
        }
    }


    public class NotesViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        ImageView imageViewItemNoteImage;
        TextView textViewItemTitle;
        TextView textViewItemDateTime;
        TextView textViewItemText;
        CardView cardViewItem;
        ImageView imageViewPopupMenu;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            initUi(itemView);
            itemView.setOnClickListener(v -> onNoteClick(getAdapterPosition()));
            imageViewPopupMenu.setOnClickListener(this::showPopupMenu);
        }

        private void initUi(View itemView) {
            textViewItemTitle = itemView.findViewById(R.id.textViewItemTitle);
            textViewItemDateTime = itemView.findViewById(R.id.textViewItemDateTime);
            textViewItemText = itemView.findViewById(R.id.textViewItemNoteText);
            cardViewItem = itemView.findViewById(R.id.cardViewNoteItem);
            imageViewPopupMenu = itemView.findViewById(R.id.imageViewPopupMenu);
            imageViewItemNoteImage = itemView.findViewById(R.id.imageViewItemNoteImage);
        }

        private void onNoteClick(int position) {
            onNoteListener.setOnNoteClickListener(
                    getSnapshots().getSnapshot(position),
                    position);
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.setGravity(Gravity.END);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.itemDeleteNote:
                    getSnapshots().getSnapshot(getAdapterPosition()).getReference().delete();
                    onNoteListener.deleteImageListener(
                            getSnapshots().getSnapshot(getAdapterPosition()).get("noteImagePath").toString()
                    );
                    return true;
                default:
                    return false;
            }
        }
    }

    public interface OnImageDeleteListener {
        void deleteImageListener(String noteImagePath);
    }

    public interface OnNoteListener {
        void setOnNoteClickListener(DocumentSnapshot documentSnapshot, int position);

        void deleteImageListener(String noteImagePath);
    }
}
