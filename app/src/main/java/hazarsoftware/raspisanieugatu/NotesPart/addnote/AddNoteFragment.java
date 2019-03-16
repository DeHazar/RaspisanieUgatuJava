package hazarsoftware.raspisanieugatu.NotesPart.addnote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hazarsoftware.raspisanieugatu.R;


import hazarsoftware.raspisanieugatu.NotesPart.UserSettings;



public class AddNoteFragment extends Fragment implements AddNoteContract.View {

    private AddNoteContract.UserActionsListener mActionListener;

    private TextView mTitle;

    private TextView mDescription;


    public static AddNoteFragment newInstance() {
        return new AddNoteFragment();
    }

    public AddNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionListener = new AddNotePresenter(UserSettings.provideNotesRepository(), this);

        FloatingActionButton fab =
                getActivity().findViewById(R.id.fab_add_notes);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionListener.saveNote(mTitle.getText().toString(),
                        mDescription.getText().toString());
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addnote, container, false);
        mTitle = root.findViewById(R.id.add_note_title);
        mDescription = root.findViewById(R.id.add_note_description);

        Intent intent = getActivity().getIntent();
        String lesson = intent.getStringExtra("nameLesson");
        String data =  intent.getStringExtra("dataLesson");
        mTitle.setText(data+ " Предмет: "+lesson);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }


    @Override
    public void showEmptyNoteError() {
        Snackbar.make(mTitle, getString(R.string.empty_note_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNotesList() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }
}
