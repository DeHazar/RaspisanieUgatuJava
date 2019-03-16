package hazarsoftware.raspisanieugatu;

import java.util.Calendar;
import java.util.Date;

public class DayOfSchedule
    {
        private int currentWeek;
        private String DenNedeli;
        private String  para;
        private String nameLesson;
        private String room;
        private String teacher;
        private String typeLesson;
        private int counter ;



        public String getDenNedeli() {
            return DenNedeli;
        }

        public DayOfSchedule( String para, String typeLesson, String nameLesson, String room, String teacher, int counter,String den,int currentWeek){
            this.para = para;
            this.nameLesson = nameLesson;
            this.room =room;
            this.teacher =teacher;
            this.typeLesson=typeLesson;
            this.counter = counter;
            this.DenNedeli = den;
            this.currentWeek = currentWeek;
        }


        public Calendar getDay() {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            // Получение даты для разных семестров
            int xWeek=0;
            if(new Schedule().getCurrentNumSemestr() % 2 ==0){
                 xWeek = -Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 34  + currentWeek;
            }else{
                 xWeek = currentWeek - 4;
            }
            switch (getDenNedeli()) {
                case "Понедельник":
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                case "Вторник":
                    cal.add(Calendar.DATE, 1);
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                case "Среда":
                    cal.add(Calendar.DATE, 2);
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                case "Четверг":
                    cal.add(Calendar.DATE, 3);
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                case "Пятница":
                    cal.add(Calendar.DATE, 4);
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                case "Суббота":
                    cal.add(Calendar.DATE, 5);
                    cal.add(Calendar.DATE, xWeek * 7);
                    return cal;
                default:
                    return cal;
            }
        }

        public int getCounter() {

            return counter;
        }

        public String getTypeLesson() {

            return this.typeLesson;
        }

        public String getNameLesson() {
            return this.nameLesson;
        }

        public String getPara() {
            return this.para;
        }

        public String getRoom() {
            return this.room;
        }

        public String getTeacher() {
            return this.teacher;
        }
        public String getTime(){
            switch (this.para){
                case "1":
                    return "8:00 -  9:35";
                case "2":
                    return  "9:45 - 11:20";
                case "3":
                    return "12:10 - 13:45";
                case "4":
                    return "13:55 - 15:30";
                case "5":
                    return "16:10 - 17:45";
                case  "6":
                    return "17:55 - 19:30";
                case "7":
                    return "20:05 - 21:40";
                default:
                    return "";
            }

        }
    }
