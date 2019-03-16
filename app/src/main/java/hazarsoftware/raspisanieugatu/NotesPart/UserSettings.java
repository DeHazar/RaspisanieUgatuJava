package hazarsoftware.raspisanieugatu.NotesPart;

import hazarsoftware.raspisanieugatu.NotesPart.data.NoteRepositories;
import hazarsoftware.raspisanieugatu.NotesPart.data.NotesRepository;
import hazarsoftware.raspisanieugatu.NotesPart.data.NotesServiceApiBase;

public class UserSettings {
    public static NotesRepository provideNotesRepository() {

        return NoteRepositories.getInMemoryRepoInstance(new NotesServiceApiBase());
    }

}
