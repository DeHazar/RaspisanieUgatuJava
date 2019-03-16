package hazarsoftware.raspisanieugatu.NotesPart.addnote;

import android.support.annotation.NonNull;


import hazarsoftware.raspisanieugatu.NotesPart.data.Note;
import hazarsoftware.raspisanieugatu.NotesPart.data.NotesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddNotePresenter implements AddNoteContract.UserActionsListener {

    @NonNull
    private final NotesRepository mNotesRepository;
    @NonNull
    private final AddNoteContract.View mAddNoteView;
    public AddNotePresenter(@NonNull NotesRepository notesRepository,
                            @NonNull AddNoteContract.View addNoteView) {
        mNotesRepository = checkNotNull(notesRepository);
        mAddNoteView = checkNotNull(addNoteView);
    }

    @Override
    public void saveNote(String title, String description) {

        Note newNote = new Note(title, description);
        if (newNote.isEmpty()) {
            mAddNoteView.showEmptyNoteError();
        } else {
            mNotesRepository.saveNote(newNote);
            mAddNoteView.showNotesList();
        }
    }

}
