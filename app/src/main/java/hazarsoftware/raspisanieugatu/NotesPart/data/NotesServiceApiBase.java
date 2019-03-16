package hazarsoftware.raspisanieugatu.NotesPart.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.ArrayMap;

import com.google.common.collect.Lists;

import java.util.List;

import hazarsoftware.raspisanieugatu.MainActivity;

public class NotesServiceApiBase implements NotesServiceApi
{

    private static final ArrayMap<String, Note> NOTES_SERVICE_DATA = new ArrayMap();
    private  static SQLiteDatabase db = MainActivity.noteDatabaseHelper;

    @Override
    public void getAllNotes(NotesServiceCallback<List<Note>> callback) {
        NOTES_SERVICE_DATA.clear();
        Cursor query = db.rawQuery("SELECT * FROM NOTES", null);
        if(query.moveToFirst()){
            do{
                String id = query.getString(1);
                String title = query.getString(2);
                String description = query.getString(3);
                Note note = new Note(id,title,description);
                NOTES_SERVICE_DATA.put(note.getId(), note);
            }
            while(query.moveToNext());
        }
        query.close();
        callback.onLoaded(Lists.newArrayList(NOTES_SERVICE_DATA.values()));
    }

    @Override
    public void getNote(String noteId, NotesServiceCallback<Note> callback) {
        Note note = NOTES_SERVICE_DATA.get(noteId);
        callback.onLoaded(note);
    }

    @Override
    public  void saveNote(Note note) {
        db.insert("NOTES",null,getContentValues(note));
        NOTES_SERVICE_DATA.put(note.getId(), note);
    }


    public void deleteNode(String noteId)
    {
        Note note = NOTES_SERVICE_DATA.get(noteId);
        db.execSQL("DELETE FROM NOTES WHERE UUID = ?",new String[]{note.getId()});

    }

    private static ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put("UUID",note.getId());
        values.put("TITLE", note.getTitle());
        values.put("DESCRIPTION", note.getDescription());
        return values;
    }
}
