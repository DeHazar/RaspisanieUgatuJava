package hazarsoftware.raspisanieugatu.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScheduleBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION =1;
    private static final String DATABASE_NAME = "schedule.db";

    public ScheduleBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ScheduleDbSchema.ScheduleTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ScheduleDbSchema.Cols.GROUP + ", "+
                ScheduleDbSchema.Cols.WEEK + ", "+
                ScheduleDbSchema.Cols.DAY + ", "+
                ScheduleDbSchema.Cols.LESSON + ", "+
                ScheduleDbSchema.Cols.PARA + ", "+
                ScheduleDbSchema.Cols.ROOM + ", "+
                ScheduleDbSchema.Cols.TEACHER + ", "+
                ScheduleDbSchema.Cols.TYPE +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

