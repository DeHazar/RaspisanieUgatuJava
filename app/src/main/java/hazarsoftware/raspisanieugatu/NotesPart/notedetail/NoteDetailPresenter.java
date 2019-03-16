package hazarsoftware.raspisanieugatu.NotesPart.notedetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import hazarsoftware.raspisanieugatu.NotesPart.data.Note;
import hazarsoftware.raspisanieugatu.NotesPart.data.NotesRepository;

import static com.google.common.base.Preconditions.checkNotNull;

public class NoteDetailPresenter implements NoteDetailContract.UserActionsListener {

    private final NotesRepository mNotesRepository;

    private final NoteDetailContract.View mNotesDetailView;

    public NoteDetailPresenter(@NonNull NotesRepository notesRepository,
                               @NonNull NoteDetailContract.View noteDetailView) {
        mNotesRepository = checkNotNull(notesRepository, "notesRepository cannot be null!");
        mNotesDetailView = checkNotNull(noteDetailView, "noteDetailView cannot be null!");
    }

    @Override
    public void openNote(@Nullable String noteId) {
        if (null == noteId || noteId.isEmpty()) {
            mNotesDetailView.showMissingNote();
            return;
        }

        mNotesDetailView.setProgressIndicator(true);
        mNotesRepository.getNote(noteId, new NotesRepository.GetNoteCallback() {
            @Override
            public void onNoteLoaded(Note note) {
                mNotesDetailView.setProgressIndicator(false);
                if (null == note) {
                    mNotesDetailView.showMissingNote();
                } else {
                    showNote(note);
                }
            }
        });
    }

    private void showNote(Note note) {
        String title = note.getTitle();
        String description = note.getDescription();

        if (title != null && title.isEmpty()) {
            mNotesDetailView.hideTitle();
        } else {
            mNotesDetailView.showTitle(title);
        }

        if (description != null && description.isEmpty()) {
            mNotesDetailView.hideDescription();
        } else {
            mNotesDetailView.showDescription(description);
        }


    }
}