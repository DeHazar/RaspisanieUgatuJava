package hazarsoftware.raspisanieugatu.NotesPart.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "notesStorage"; // Имя базы данных
    private static final int DB_VERSION = 1;

    public NotesDataBaseHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + "NOTES" + "(" +
                " _id integer primary key autoincrement, " +
                "UUID TEXT" + ", " +
                "TITLE TEXT" + ", " +
                "DESCRIPTION TEXT"  +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}