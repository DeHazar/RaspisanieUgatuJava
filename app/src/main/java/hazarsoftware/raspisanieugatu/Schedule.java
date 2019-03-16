package hazarsoftware.raspisanieugatu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hazarsoftware.raspisanieugatu.data.ScheduleBaseHelper;
import hazarsoftware.raspisanieugatu.data.ScheduleDbSchema;

public class Schedule {
    private int currentWeek;
    private String FacultSelect;
    private int KursSelect;
    private String GroupSelectNumber;
    private List<DayOfSchedule> mDaySchedules;
    private static Schedule schedule;
    private int selectWeek;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static Schedule getSchedule(Context context) {
        if (schedule == null) {
            schedule = new Schedule(context);
        }
        return schedule;
    }

    public static Schedule getSchedule() {
        if (schedule == null) {
            schedule = new Schedule();
        }
        return schedule;
    }

    private Schedule(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ScheduleBaseHelper(mContext).getWritableDatabase();
        currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - 34;
        if(currentWeek<0)
        {
            currentWeek+=29;
        }
        setSelectWeek(currentWeek);
        mDaySchedules = new ArrayList<>();
    }

    public Schedule() {
        if (mContext != null) {
            mDatabase = new ScheduleBaseHelper(mContext).getWritableDatabase();
        }
        currentWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) - 34;
        if(currentWeek<0)
        {
            currentWeek+=29;
        }
        setSelectWeek(currentWeek);
        mDaySchedules = new ArrayList<>();
    }

    public void cleanDatabase() {
        mDatabase.delete(ScheduleDbSchema.ScheduleTable.NAME, null, null);
    }

    public Boolean isScheduleInDatabase() {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = mDatabase.rawQuery("SELECT * FROM " + ScheduleDbSchema.ScheduleTable.NAME + " WHERE " + ScheduleDbSchema.Cols.WEEK + " = " + String.valueOf(this.getSelectWeek()) +
                    " LIMIT 1;", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            return mCursor.getCount() > 0;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            Log.d(" - existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    //Получаем в Лист обьекты каждого дня расписания
    //String s  - это html страница расписания
    public List<DayOfSchedule> getmDaySchedules(String s) {
        if (s.contains("Расписание отсутствует.")) {

        } else if (s.equals("")) {

        } else {
            if (s.length() < 800) {
                return mDaySchedules;
            }
            String[][] arr = MethodsForHelp.getSchedule(s);
            DayOfSchedule dayOfSchedule;

            for (int j = 1; j < 7; j++) {
                int para = 0;
                boolean first = true;
                for (int i = 1; i < 8; i++) {
                    String typeLesson = "";
                    String nameLesson = "";
                    String room = "";
                    String teacher = "";
                    String denNedeli = null;
                    boolean check = true;

                    if (j == 1) {
                        denNedeli = "Понедельник";
                    } else if (j == 2) {

                        denNedeli = "Вторник";
                    } else if (j == 3) {

                        denNedeli = "Среда";
                    } else if (j == 4) {

                        denNedeli = "Четверг";
                    } else if (j == 5) {

                        denNedeli = "Пятница";
                    } else if (j == 6) {

                        denNedeli = "Суббота";
                    }
                    if (arr[i][j] == null || arr[i][j].equals("")) {
                        para += 1;
                        if (check) {
                            for (int z = 1; z < 8; z++) {
                                if (arr[z][j] == null || arr[z][j].equals("")) {
                                    check = true;

                                } else {
                                    check = false;
                                    break;
                                }
                            }
                            if (check) {
                                dayOfSchedule = new DayOfSchedule("", typeLesson, "Нет занятий", room, teacher, 1, denNedeli, getSelectWeek());
                                mDaySchedules.add(dayOfSchedule);
                                ContentValues values = getContentValues(dayOfSchedule);
                                mDatabase.insert(ScheduleDbSchema.ScheduleTable.NAME, null, values);
                                break;
                            }
                        }
                    } else {

                        String choose = arr[i][j];
                        typeLesson = MethodsForHelp.typeLesson(choose);
                        choose = choose.replace(typeLesson, "");
                        StringBuilder copy = new StringBuilder(choose);
                        Pattern pattern = Pattern.compile("([0-9]{0,2}|[0-9а-я]{0,2})[-][0-9]{3}[а-я]?");
                        Pattern pattern2 = Pattern.compile("([0-9]{0,2}|[0-9а-я]{0,2})[-][0-9]{3}[а-я]?[,]\\s[0-9][-][0-9]{3}[а-я]?");
                        if (pattern2.matcher(copy).find()) {
                            Matcher matcher = pattern2.matcher(copy);
                            while (matcher.find()) {
                                nameLesson = copy.substring(0, matcher.start());
                                room = copy.substring(matcher.start(), matcher.start() + 12);
                                teacher = copy.substring(matcher.start() + 12, copy.length());
                            }
                        } else if (pattern.matcher(copy).find()) {

                            Matcher matcher = pattern.matcher(copy);
                            while (matcher.find()) {
                                nameLesson = copy.substring(0, matcher.start());
                                room = copy.substring(matcher.start(), matcher.start() + 6);
                                teacher = copy.substring(matcher.start() + 6, choose.length());
                            }
                        } else if (copy.indexOf("Элективные курсы по физической культуре Кафедра физвоспитания") != -1) {
                            typeLesson = "Практика (семинар)";
                            nameLesson = "Элективные курсы по физической культуре";
                            room = "Кафедра физвоспитания";
                            teacher = "";
                        } else if (copy.indexOf("Физическая культура Кафедра физвоспитания") != -1) {
                            typeLesson = "Лекция + практика";
                            nameLesson = "Физическая культура";
                            room = "Кафедра физвоспитания";
                            teacher = "";
                        } else if (copy.indexOf("Иностранный язык") != -1) {
                            typeLesson = "Практика (семинар)";
                            nameLesson = "Иностранный язык";
                            room = "Кафедра иностр.языка";
                            teacher = "";
                        } else if (copy.indexOf("6 кинозал") != -1) {
                            room = "6 кинозал";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("6 кинозал") != -1) {
                            room = "Аэро- порт";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("Библиотека") != -1) {
                            room = "Библиотека";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("Военная кафедра") != -1) {
                            room = "Военная кафедра";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room) + 15, copy.length());
                        } else if (copy.indexOf("Завод") != -1) {
                            room = "Завод";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("ИПСМ") != -1) {
                            room = "ИПСМ";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("Кафедра") != -1) {
                            room = "Кафедра";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("общ№8") != -1) {
                            room = "общ№8";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("УВЦ") != -1) {
                            room = "УВЦ";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("УПК-1") != -1) {
                            room = "УПК-1";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("УПК 201") != -1) {
                            room = "УПК 201";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("УПК 202") != -1) {
                            room = "УПК 202";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("Филиал") != -1) {
                            room = "Филиал";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room), copy.length());
                        } else if (copy.indexOf("Чертежный зал1") != -1) {
                            room = "Чертежный зал 1";
                            nameLesson = copy.substring(0, copy.indexOf("Чертежный зал1"));
                            teacher = copy.substring(copy.lastIndexOf("Чертежный зал1") + 14, copy.length());
                        } else if (copy.indexOf("Чертежный зал2") != -1) {
                            room = "Чертежный зал 2";
                            nameLesson = copy.substring(0, copy.indexOf("Чертежный зал2"));
                            teacher = copy.substring(copy.lastIndexOf("Чертежный зал2") + 14, copy.length());
                        } else if (copy.indexOf("Чертежный зал3") != -1) {
                            room = "Чертежный зал 3";
                            nameLesson = copy.substring(0, copy.indexOf("Чертежный зал3"));
                            teacher = copy.substring(copy.lastIndexOf("Чертежный зал3") + 14, copy.length());
                        } else if (copy.indexOf("Чертежный зал4") != -1) {
                            room = "Чертежный зал 4";
                            nameLesson = copy.substring(0, copy.indexOf("Чертежный зал4"));
                            teacher = copy.substring(copy.lastIndexOf("Чертежный зал4") + 14, copy.length());
                        } else if (copy.indexOf("8-1акт") != -1) {
                            room = "8-1акт";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room) + 6, copy.length());
                        } else if (copy.indexOf("8-2Г2") != -1) {
                            room = "8-2Г2";
                            nameLesson = copy.substring(0, copy.indexOf(room));
                            teacher = copy.substring(copy.lastIndexOf(room) + 6, copy.length());
                        } else {
                            room = "none";
                            nameLesson = "Error";
                            teacher = "ERROR";

                        }
                        para += 1;


                        if (first) {
                            dayOfSchedule = new DayOfSchedule(String.valueOf(para), typeLesson, nameLesson, room, teacher, 1, denNedeli, selectWeek);
                            first = false;
                        } else
                            dayOfSchedule = new DayOfSchedule(String.valueOf(para), typeLesson, nameLesson, room, teacher, 0, "", selectWeek);

                        mDaySchedules.add(dayOfSchedule);
                        ContentValues values = getContentValues(dayOfSchedule);
                        mDatabase.insert(ScheduleDbSchema.ScheduleTable.NAME, null, values);
                    }
                }

            }
        }
        return mDaySchedules;
    }

    //Получаем Лист обектов с расписанием, если они были в базе данных.
    public List<DayOfSchedule> getmDaySchedules() {
        Cursor query = mDatabase.rawQuery("SELECT * FROM " + ScheduleDbSchema.ScheduleTable.NAME +
                " WHERE " + ScheduleDbSchema.Cols.WEEK + " = " + String.valueOf(schedule.getSelectWeek()), null);
        String last = "";
        if (query.moveToFirst()) {
            do {
                DayOfSchedule dayOfSchedule;
                String week = query.getString(2);
                String day = query.getString(3);
                String Lesson = query.getString(4);
                String para = query.getString(5);
                String room = query.getString(6);
                String teacher = query.getString(7);
                String type = query.getString(8);
                if (last.equals(day)) {
                    dayOfSchedule = new DayOfSchedule(para, type, Lesson, room, teacher, 0, day, Integer.parseInt(week));
                } else
                    dayOfSchedule = new DayOfSchedule(para, type, Lesson, room, teacher, 1, day, Integer.parseInt(week));
                mDaySchedules.add(dayOfSchedule);
            }
            while (query.moveToNext());
        }
        query.close();
        return mDaySchedules;
    }

    private static ContentValues getContentValues(DayOfSchedule dayOfSchedule) {
        ContentValues values = new ContentValues();
        values.put(ScheduleDbSchema.Cols.DAY, dayOfSchedule.getDenNedeli());
        values.put(ScheduleDbSchema.Cols.GROUP, schedule.getGroupSelectNumber());
        values.put(ScheduleDbSchema.Cols.LESSON, dayOfSchedule.getNameLesson());
        values.put(ScheduleDbSchema.Cols.PARA, dayOfSchedule.getPara());
        values.put(ScheduleDbSchema.Cols.ROOM, dayOfSchedule.getRoom());
        values.put(ScheduleDbSchema.Cols.TEACHER, dayOfSchedule.getTeacher());
        values.put(ScheduleDbSchema.Cols.TYPE, dayOfSchedule.getTypeLesson());
        values.put(ScheduleDbSchema.Cols.WEEK, schedule.getSelectWeek());
        return values;
    }

    public int getSelectWeek() {
        return selectWeek;
    }

    public void setSelectWeek(int selectWeek) {
        this.selectWeek = selectWeek;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public String getFacultSelect() {
        return FacultSelect;
    }

    public String getGroupSelectNumber() {
        return GroupSelectNumber;
    }

    public void setGroupSelectNumber(String groupSelectNumber) {
        GroupSelectNumber = groupSelectNumber;
    }

    public int getCurrentNumSemestr()
    {
        int appCreateSemestr = 8;
        int currentSemestr =0;

        Calendar currentCalendar = GregorianCalendar.getInstance();
        int currentMonth = currentCalendar.MONTH;
        Calendar appCreateDate = new GregorianCalendar(2018,Calendar.OCTOBER,24);

        int difference = currentCalendar.getTime().getYear() - appCreateDate.getTime().getYear();
        if(currentMonth ==Calendar.JANUARY || currentMonth == Calendar.FEBRUARY || currentMonth == Calendar.MARCH || currentMonth == Calendar.APRIL
                || currentMonth == Calendar.MAY || currentMonth == Calendar.JUNE || currentMonth == Calendar.JULY)
        {

            currentSemestr = appCreateSemestr + difference*2-1;

        }else
            {
                currentSemestr = appCreateSemestr + difference*2;
            }


        return currentSemestr;
    }

    public void cleanDayShedules() {

        mDaySchedules.clear();
    }

    public List<DayOfSchedule> getScheduleList(){
        return mDaySchedules;
    }

    public void setFacultSelect(String facultSelect) {
        FacultSelect = facultSelect;
    }

    public int getKursSelect() {
        return KursSelect;
    }

    public void setKursSelect(int kursSelect) {
        KursSelect = kursSelect;
    }
}
