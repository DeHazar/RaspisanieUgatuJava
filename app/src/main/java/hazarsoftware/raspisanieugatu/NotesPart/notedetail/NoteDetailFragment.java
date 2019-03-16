package hazarsoftware.raspisanieugatu.NotesPart.notedetail;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import hazarsoftware.raspisanieugatu.NotesPart.data.NotesServiceApiBase;
import hazarsoftware.raspisanieugatu.R;


import hazarsoftware.raspisanieugatu.NotesPart.UserSettings;

public class NoteDetailFragment extends Fragment implements NoteDetailContract.View {

    public static final String ARGUMENT_NOTE_ID = "NOTE_ID";

    private NoteDetailContract.UserActionsListener mActionsListener;

    private TextView mDetailTitle;

    private TextView mDetailDescription;


    public static NoteDetailFragment newInstance(String noteId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_NOTE_ID, noteId);
        NoteDetailFragment fragment = new NoteDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionsListener = new NoteDetailPresenter(UserSettings.provideNotesRepository(),
                this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_detail, container, false);
        mDetailTitle = root.findViewById(R.id.note_detail_title);
        mDetailDescription = root.findViewById(R.id.note_detail_description);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        String noteId = getArguments().getString(ARGUMENT_NOTE_ID);
        mActionsListener.openNote(noteId);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mDetailTitle.setText("");
            mDetailDescription.setText(getString(R.string.loading));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteNoteBd:
                String noteId = getArguments().getString(ARGUMENT_NOTE_ID);
                NotesServiceApiBase fakeNotesServiceApi = new NotesServiceApiBase();
                fakeNotesServiceApi.deleteNode(noteId);
                getActivity().onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showTitle(String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void showMissingNote() {
        mDetailTitle.setText("");
        mDetailDescription.setText(getString(R.string.no_data));
    }
}
