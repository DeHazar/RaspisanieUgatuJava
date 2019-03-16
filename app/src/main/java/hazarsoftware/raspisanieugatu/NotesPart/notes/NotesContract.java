package hazarsoftware.raspisanieugatu.NotesPart.notes;

import android.support.annotation.NonNull;

import java.util.List;

import hazarsoftware.raspisanieugatu.NotesPart.data.Note;

public interface NotesContract {

    interface View {

        void setProgressIndicator(boolean active);

        void showNotes(List<Note> notes);

        void showAddNote();

        void showNoteDetailUi(String noteId);
    }

    interface UserActionsListener {

        void loadNotes(boolean forceUpdate);

        void addNewNote();

        void openNoteDetails(@NonNull Note requestedNote);
    }
}
