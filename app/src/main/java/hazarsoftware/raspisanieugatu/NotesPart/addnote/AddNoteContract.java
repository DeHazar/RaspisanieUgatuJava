package hazarsoftware.raspisanieugatu.NotesPart.addnote;


public interface AddNoteContract {

    interface View {

        void showEmptyNoteError();

        void showNotesList();
    }

    interface UserActionsListener {

        void saveNote(String title, String description);

    }
}
