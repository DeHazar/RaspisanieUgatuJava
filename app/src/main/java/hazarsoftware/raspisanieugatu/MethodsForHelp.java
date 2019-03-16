package hazarsoftware.raspisanieugatu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MethodsForHelp
    {
        public static String typeLesson(String choose)
            {
                if (choose.contains("Практика (семинар)"))
                    {
                        return "Практика (семинар)";

                    } else if (choose.contains("Лекция + практика"))
                    {
                        return  "Лекция + практика";

                    } else if (choose.contains("Лекция"))
                    {
                        return  "Лекция";

                    } else if (choose.contains("Лабораторная работа"))
                    {
                        return  "Лабораторная работа";

                    } else
                    {
                        return  "";
                    }
            }

        public static boolean isConnected() throws InterruptedException, IOException   //Проверка есть ли интернет
        {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }

        public static String[][] getSchedule(String html)
            { // Парсим html и получаем расписание в виде массива
                String[][] result = new String[10][10];
                Document doc = Jsoup.parse(html);
                Elements tables = doc.select("table");
                for (Element table : tables)
                    {
                        Elements trs = table.select("tr");
                        String[][] trtd = new String[trs.size()][];
                        for (int i = 0; i < trs.size(); i++)
                            {
                                Elements tds = trs.get(i).select("td");
                                trtd[i] = new String[tds.size()];
                                for (int j = 0; j < tds.size(); j++)
                                    {
                                        trtd[i][j] = tds.get(j).text();
                                    }
                            }
                        result = trtd;
                    }
                return result;
            }

    }
