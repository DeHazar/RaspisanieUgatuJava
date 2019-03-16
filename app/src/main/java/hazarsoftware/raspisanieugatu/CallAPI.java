package hazarsoftware.raspisanieugatu;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CallAPI extends AsyncTask<Integer,Integer,String>
    {
        Schedule mschedule;

        protected void onPostExecute(String s)
            {
                super.onPostExecute(s);
            }


        private String performPostCall(String requestURL, HashMap<String, String> postDataParams)
            {

                URL url;
                StringBuilder stringBuilder = new StringBuilder();

                try
                    {

                        url = new URL(requestURL);

                        if (Singleton.INSTANCE.getCrsftoken() == null)   //Получаем crsftoken вызовом GET запроса
                            {
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setReadTimeout(10000);
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("GET");
                                conn.setDoInput(true);
                                conn.setRequestProperty("Accept-Language", "ru-RU");
                                conn.setRequestProperty("Accept", "text/html, application/xhtml+xml, application/xml; q=0.9, */*; q=0.8");
                                System.setProperty("http.agent", "Chrome");
                                int responseCode = conn.getResponseCode();
                                if (responseCode == HttpsURLConnection.HTTP_OK)
                                    {
                                        String line;
                                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        while ((line = br.readLine()) != null)
                                            {
                                                if (line.contains("csrfmiddlewaretoken: '"))
                                                    {
                                                        Singleton.setCrsftoken(line.substring(line.indexOf("csrfmiddlewaretoken: '") + 22, line.indexOf("csrfmiddlewaretoken: '") + 54));
                                                        break;
                                                    }
                                            }
                                        br.close();
                                        conn.disconnect();
                                    } else
                                    {
                                        conn.disconnect();
                                    }
                            }
                        if (Singleton.INSTANCE.getCrsftoken() != null)
                            {                                              //Здесь отправляем POST запрос и считываем в стрингбилдер данные сайта
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setReadTimeout(10000);
                                conn.setConnectTimeout(10000);
                                conn.setRequestMethod("POST");
                                conn.setDoInput(true);
                                conn.setDoOutput(true);
                                postDataParams.put("csrfmiddlewaretoken", Singleton.INSTANCE.getCrsftoken());
                                if(postDataParams.size()==3){
                                    conn.setRequestProperty("Accept", "application/json");
                                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                                }else
                                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                                conn.setRequestProperty("Cookie", "csrftoken=" + Singleton.INSTANCE.getCrsftoken());
                                conn.setRequestProperty("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
                                conn.setRequestProperty("Referer", "https://lk.ugatu.su/raspisanie/");
                                conn.setRequestProperty("DNT", "1");
                                System.setProperty("http.agent", "Chrome");
                                OutputStream os = conn.getOutputStream();
                                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                                writer.write(getPostDataString(postDataParams));
                                writer.flush();
                                writer.close();
                                os.close();
                                int responseCode = conn.getResponseCode();

                                if (responseCode == HttpsURLConnection.HTTP_OK)
                                    {
                                        String line;
                                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                        while ((line = br.readLine()) != null)
                                            {
                                                stringBuilder.append(line);
                                            }
                                        br.close();
                                        conn.disconnect();
                                    } else
                                    {
                                        conn.disconnect();
                                    }
                            }
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                return stringBuilder.toString();
            }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
            { // формируем пост запрос в правильном порядке
                StringBuilder result = new StringBuilder();
                boolean first = true;
                for (Map.Entry<String, String> entry : params.entrySet())
                    {
                        if (first)
                            first = false;
                        else
                            result.append("&");

                        result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                        result.append("=");
                        result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    }

                return result.toString();
            }

        @Override
        protected String doInBackground(Integer... ints)
            {
                mschedule = Schedule.getSchedule();
                // Выбор между запросом для поиска групп и пполучения самого расписания
                        if (ints[0]==1)
                            {
                                HashMap<String, String> hash = new HashMap<>();
                                hash.put("faculty",mschedule.getFacultSelect());
                                hash.put("klass", String.valueOf(mschedule.getKursSelect()));
                                return performPostCall("https://lk.ugatu.su/raspisanie/#timetable", hash);
                            } else if (ints[0]==0)
                                {
                                HashMap<String, String> hash = new HashMap<>();
                                hash.put("faculty",mschedule.getFacultSelect() );
                                hash.put("klass", String.valueOf(mschedule.getKursSelect()));
                                hash.put("group",mschedule.getGroupSelectNumber());

                                hash.put("ScheduleType", "За неделю");
                                hash.put("week", String.valueOf(mschedule.getSelectWeek()));

                                Date date = new Date();
                                @SuppressLint("SimpleDateFormat") String dat = new SimpleDateFormat("dd.MM.yyyy").format(date);
                                hash.put("date", dat);
                                hash.put("sem", String.valueOf(mschedule.getCurrentNumSemestr()));
                                hash.put("view", "ПОКАЗАТЬ");
                                return performPostCall("https://lk.ugatu.su/raspisanie/#timetable", hash);
                            }


                return "";
            }


    }
