package hazarsoftware.raspisanieugatu.NotesPart.data;

import android.support.annotation.NonNull;

import java.util.List;

public interface NotesRepository {

    interface LoadNotesCallback {

        void onNotesLoaded(List<Note> notes);
    }

    interface GetNoteCallback {

        void onNoteLoaded(Note note);
    }

    void getNotes(@NonNull LoadNotesCallback callback);

    void getNote(@NonNull String noteId, @NonNull GetNoteCallback callback);

    void saveNote(@NonNull Note note);

    void refreshData();

}