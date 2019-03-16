package hazarsoftware.raspisanieugatu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ContentFragmetOptions extends Fragment
    {

        private View view;
        private Schedule mSchedule;
        private HashMap<String,String> groupsSave;
        boolean first =false;

        @Override
        public void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                mSchedule = Schedule.getSchedule(getActivity());
                mSchedule.cleanDayShedules();
                mSchedule.cleanDatabase();
                mSchedule.setSelectWeek(mSchedule.getCurrentWeek());
            }

        @Override
        public void onPause()
            {
                super.onPause();
                mSchedule = Schedule.getSchedule();
                if(!check[0])
                {

                }else{
                SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
                myPreferences.edit().putString("selectFaculut",mSchedule.getFacultSelect())
                        .putInt("selectKurs",mSchedule.getKursSelect())
                        .putString("selectGroupNumber",mSchedule.getGroupSelectNumber()).apply();}
            }

        @Override
        public View onCreateView(LayoutInflater inflater,  ViewGroup container,Bundle savedInstanceState)
            {
                view = inflater.inflate(R.layout.content_fragment_options, container, false);
                SpinnerSetFacultets();
                SpinnerKursSet();
                return view;
            }

        private ArrayList<String> parse(String jsonToParse)
            { //Получаем список групп
                ArrayList<String> groups = new ArrayList<>();
                groupsSave = new HashMap<String, String>();
                //jsonToParse= jsonToParse.replace("{","");
              //  jsonToParse= jsonToParse.replace("}","").replace("[","{").replace("]","}");
                Gson g = new Gson();

                TypeDTO[] object= g.fromJson(jsonToParse,TypeDTO[].class);

               for (TypeDTO item : object)
                 {
                      groups.add(item.mane);
                      groupsSave.put(item.mane, item.id);
                }
                return groups;
            }
        class TypeDTO
        {
            String id;
            String mane;
        }
            private  final boolean[] check = {false};
        private void SpinnerSetGroups()
            { //Показываем список групп
                final ArrayList<String> group;
                try
                    {
                        String s = new CallAPI().execute(1).get();
                        if (s.equals(""))
                            {
                                Toast toast = Toast.makeText(this.getActivity(), "Сервер не отвечает", Toast.LENGTH_LONG);
                                toast.show();
                            } else
                            {
                                group = parse(s);

                                final Spinner spinner = view.findViewById(R.id.groups);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, group);
                                spinner.setAdapter(adapter);
                                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spinner.setSelection(0,false);
                                check[0]=false;
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                                    {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                            {

                                                mSchedule.setGroupSelectNumber(groupsSave.get(parent.getItemAtPosition(position)));
                                                spinner.setSelection(position);
                                                if(check[0])
                                                    startActivity(new Intent(getContext(), MainActivity.class));
                                                // getFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new DayOfScheduleFragment()).commit();
                                                else check[0] =true;
                                            }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent)
                                            {
                                            }
                                    });
                            }
                    }catch (Exception ignored){
                    ignored.printStackTrace();
                }


            }

        private void SpinnerSetFacultets()
            { //заполняем выбор факультета

                String[] facult = getResources().getStringArray(R.array.facultetArray);
                final Spinner spinner = view.findViewById(R.id.facultets);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, facult);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
                            {

                                if (String.valueOf(adapterView.getItemAtPosition(i)).equals("ИАТМ"))
                                    {
                                        mSchedule.setFacultSelect("ФАТС");
                                    } else
                                    {
                                        mSchedule.setFacultSelect((String) adapterView.getItemAtPosition(i));
                                    }
                                spinner.setSelection(i);
                                try
                                    {
                                        if(first)
                                            {
                                                if (MethodsForHelp.isConnected())
                                                    {
                                                        SpinnerSetGroups();
                                                    }

                                            }else
                                            first =true;
                                    } catch (InterruptedException e)
                                    {
                                        e.printStackTrace();
                                    } catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }

                            }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView)
                            {
                            }
                    });
            }


        private void SpinnerKursSet()                                       //Выбираем курс
        {

            String[] kurs = getResources().getStringArray(R.array.kursArray);
            final Spinner spinner = view.findViewById(R.id.Kurs);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, kurs);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    // Чтобы Итем слушатель первый раз не вызывал групселектед и тем самым уменьшить кол-во запросов к сайту
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {

                            mSchedule.setKursSelect(position+1);
                            spinner.setSelection(position);

                            try
                                {
                                    if (MethodsForHelp.isConnected())
                                        {

                                            SpinnerSetGroups();

                                        }
                                } catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                } catch (IOException e)
                                {
                                    e.printStackTrace();
                                }

                        }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                        {

                        }

                });
        }
    }
