package hazarsoftware.raspisanieugatu;

public class Singleton
    {
        public static final Singleton INSTANCE = new Singleton();
        private static String crsftoken = null;
        public String getCrsftoken(){
            return crsftoken;
        }

        public static void setCrsftoken(String crsftoken)
            {
                Singleton.crsftoken = crsftoken;
            }
    }
