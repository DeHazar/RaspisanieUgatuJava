package hazarsoftware.raspisanieugatu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hazarsoftware.raspisanieugatu.NotesPart.addnote.AddNoteActivity;


public class DayOfScheduleFragment extends Fragment {
    private static final int REQUEST_ADD_NOTE = 1;
    private EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView mScheduleRecycleView;
    private ScheduleAdapter mAdapter;
    Schedule mSchedule;
    LinearLayoutManager linearLayoutManager;
    private int weekForTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedules_list, container, false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mScheduleRecycleView = view.findViewById(R.id.schedule_recycler_view);
        mScheduleRecycleView.setLayoutManager(linearLayoutManager);
        return view;
    }

    public void loadNextDataFromApi(int offset) {
        mSchedule.setSelectWeek(mSchedule.getSelectWeek() + 1);
        updateUI();
    }

    private List<DayOfSchedule> getOneWeeks() {
        Schedule dayOfSchedule = Schedule.getSchedule(getActivity());
        List<DayOfSchedule> schedules;
        if (dayOfSchedule.isScheduleInDatabase()) {
            schedules = dayOfSchedule.getmDaySchedules();
        } else {
            String s = null;
            try {
                s = new CallAPI().execute(0).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            schedules = dayOfSchedule.getmDaySchedules(s);//Получаю список
        }
        return schedules;
    }

    private void updateUI() {
        List<DayOfSchedule> schedules = getOneWeeks();
        if (mAdapter == null) {
            mAdapter = new ScheduleAdapter(schedules, new OnItemClickListener() {
                @Override
                public void onItemClick(DayOfSchedule item) {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getContext());
                    }
                    builder.setTitle("Добавление заметки")
                            .setMessage("Хотите добавить заметку на  пару " + item.getNameLesson() + " ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(getContext(), AddNoteActivity.class);
                                    intent.putExtra("nameLesson", item.getNameLesson());
                                    intent.putExtra("dataLesson", new SimpleDateFormat("dd.MM.yyyy").format(item.getDay().getTime()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .show();
                }
            });

            mScheduleRecycleView.setAdapter(mAdapter);
            scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    loadNextDataFromApi(page);

                }

                @Override
                public void scrolledUp(boolean up) {
                    if (up) {
                        weekForTitle -= 1;
                        getActivity().setTitle(String.valueOf(weekForTitle) + " учебная неделя");
                    } else {
                        weekForTitle += 1;
                        getActivity().setTitle(String.valueOf(weekForTitle) + " учебная неделя");
                        //mSchedule.setSelectWeek(mSchedule.getSelectWeek()+1);
                        // Objects.requireNonNull(getActivity()).setTitle(mSchedule.getCurrentWeek());
                    }

                }
            };
            mScheduleRecycleView.addOnScrollListener(scrollListener);
        }
        else {
            mAdapter.notifyDataSetChanged();
            }

    }

    @Override
    public void onResume() {
        super.onResume();
        mSchedule = Schedule.getSchedule();
        weekForTitle = mSchedule.getCurrentWeek();
        getActivity().setTitle(String.valueOf(weekForTitle) + " учебная неделя");
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSchedule.setGroupSelectNumber(myPreferences.getString("selectGroupNumber", "0"));
        mSchedule.setKursSelect(myPreferences.getInt("selectKurs", 1));
        mSchedule.setFacultSelect(myPreferences.getString("selectFaculut", "АВИЭТ"));
        updateUI();
    }


    private class ScheduleHolder extends RecyclerView.ViewHolder {

        private TextView para, data, typeLesson, nameLesson, teacher, room, time;

        private DayOfSchedule dayOfSchedule;

        public void bindSchedule(final DayOfSchedule schedule, final OnItemClickListener listener) {
            dayOfSchedule = schedule;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(schedule);
                }
            });
            para.setText(dayOfSchedule.getPara());
            nameLesson.setText(dayOfSchedule.getNameLesson());
            room.setText(dayOfSchedule.getRoom());
            time.setText(dayOfSchedule.getTime());
            teacher.setText(dayOfSchedule.getTeacher());

            if (schedule.getCounter() == 0) {
                data.setHeight(1);
                data.setText("");

            } else {
                data.setHeight(50);
                data.setBackgroundResource(R.color.Razdel);
                String s = schedule.getDenNedeli() + " " + new SimpleDateFormat("dd.MM.yyyy").format(schedule.getDay().getTime());
                data.setText(s);
            }

            int color;

            switch (schedule.getTypeLesson()) {
                case "Практика (семинар)":
                    color = ContextCompat.getColor(getContext(), R.color.Practice);
                    typeLesson.setTextColor(color);

                    break;
                case "Лекция + практика":
                    color = ContextCompat.getColor(getContext(), R.color.LectureAndPractice);
                    typeLesson.setTextColor(color);
                    break;
                case "Лекция":
                    color = ContextCompat.getColor(getContext(), R.color.Lecture);
                    typeLesson.setTextColor(color);
                    break;
                case "Лабораторная работа":
                    color = ContextCompat.getColor(getContext(), R.color.Labaratory);
                    typeLesson.setTextColor(color);
                    break;
            }
            typeLesson.setText(dayOfSchedule.getTypeLesson());
        }

        public ScheduleHolder(View itemView) {
            super(itemView);

            para = itemView.findViewById(R.id.para);
            data = itemView.findViewById(R.id.date);
            typeLesson = itemView.findViewById(R.id.typeLesson);
            nameLesson = itemView.findViewById(R.id.nameLesson);
            teacher = itemView.findViewById(R.id.teacher);
            room = itemView.findViewById(R.id.room);
            time = itemView.findViewById(R.id.time);

        }
    }

    public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleHolder> {
        private final List<DayOfSchedule> mSchedules;
        private final OnItemClickListener listener;

        public ScheduleAdapter(List<DayOfSchedule> schedules, OnItemClickListener listener) {
            this.mSchedules = schedules;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_schedule, parent, false);
            return new ScheduleHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {
            DayOfSchedule schedule = mSchedules.get(position);
            holder.bindSchedule(schedule, listener);
        }

        @Override
        public int getItemCount() {
            return mSchedules.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DayOfSchedule item);
    }

}
