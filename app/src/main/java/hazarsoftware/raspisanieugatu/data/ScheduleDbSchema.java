package hazarsoftware.raspisanieugatu.data;

public class ScheduleDbSchema {

    public final static class ScheduleTable{
        public static final String NAME = "schedules";
    }

    public static final class Cols{
        public static final String GROUP = "groupId";
        public static final String WEEK = "week";
        public static final String ROOM = "room";
        public static final String TEACHER = "teacher";
        public static final String PARA = "para";
        public static final String LESSON = "nameLesson";
        public static final String TYPE = "typeLesson";
        public static final String DAY = "dayOfWeek";
    }
}
