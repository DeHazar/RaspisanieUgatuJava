package hazarsoftware.raspisanieugatu.NotesPart.notes;

import android.support.annotation.NonNull;

import java.util.List;

import hazarsoftware.raspisanieugatu.NotesPart.data.Note;
import hazarsoftware.raspisanieugatu.NotesPart.data.NotesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class NotesPresenter implements NotesContract.UserActionsListener {

    private final NotesRepository mNotesRepository;
    private final NotesContract.View mNotesView;

    public NotesPresenter(
            @NonNull NotesRepository notesRepository, @NonNull NotesContract.View notesView) {
        mNotesRepository = checkNotNull(notesRepository, "notesRepository cannot be null");
        mNotesView = checkNotNull(notesView, "notesView cannot be null!");
    }

    @Override
    public void loadNotes(boolean forceUpdate) {
        mNotesView.setProgressIndicator(true);
        if (forceUpdate) {
            mNotesRepository.refreshData();
        }


        mNotesRepository.getNotes(new NotesRepository.LoadNotesCallback() {
            @Override
            public void onNotesLoaded(List<Note> notes) {
                mNotesView.setProgressIndicator(false);
                mNotesView.showNotes(notes);
            }
        });
    }

    @Override
    public void addNewNote() {
        mNotesView.showAddNote();
    }

    @Override
    public void openNoteDetails(@NonNull Note requestedNote) {
        checkNotNull(requestedNote, "requestedNote cannot be null!");
        mNotesView.showNoteDetailUi(requestedNote.getId());
    }

}
