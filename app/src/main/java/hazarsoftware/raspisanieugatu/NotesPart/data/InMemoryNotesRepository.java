package hazarsoftware.raspisanieugatu.NotesPart.data;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class InMemoryNotesRepository implements NotesRepository {

    private final NotesServiceApi mNotesServiceApi;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    List<Note> mCachedNotes;

    public InMemoryNotesRepository(@NonNull NotesServiceApi notesServiceApi) {
        mNotesServiceApi = checkNotNull(notesServiceApi);
    }

    @Override
    public void getNotes(@NonNull final LoadNotesCallback callback) {
        checkNotNull(callback);
        // Load from API only if needed.
        if (mCachedNotes == null) {
            mNotesServiceApi.getAllNotes(new NotesServiceApi.NotesServiceCallback<List<Note>>() {
                @Override
                public void onLoaded(List<Note> notes) {
                    mCachedNotes = ImmutableList.copyOf(notes);
                    callback.onNotesLoaded(mCachedNotes);
                }
            });
        } else {
            callback.onNotesLoaded(mCachedNotes);
        }
    }

    @Override
    public void saveNote(@NonNull Note note) {
        checkNotNull(note);
        mNotesServiceApi.saveNote(note);
        refreshData();
    }

    @Override
    public void getNote(@NonNull final String noteId, @NonNull final GetNoteCallback callback) {
        checkNotNull(noteId);
        checkNotNull(callback);
        // Load notes matching the id always directly from the API.
        mNotesServiceApi.getNote(noteId, note -> callback.onNoteLoaded(note));
    }

    @Override
    public void refreshData() {
        mCachedNotes = null;
    }

}
