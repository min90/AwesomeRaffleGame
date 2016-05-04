package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.domain.FragmentController;

/**
 * Created by Jesper on 04/04/16.
 */
public class StartFragment extends Fragment implements View.OnClickListener {
    public static final String DEBUG_TAG = StartFragment.class.getSimpleName();
    public static final String NAME_TAG = "name_tag";

    private Button btnStartGame;
    private Button btnResults;
    private Button btnAbout;

    private String name;

    private TextView txtWelcomeMessage;

    public static StartFragment newInstance(String name) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putString(NAME_TAG, name);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        Bundle args = getArguments();

        name = args.getString(NAME_TAG);

        btnStartGame = (Button) view.findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(this);
        btnResults = (Button) view.findViewById(R.id.btnResults);
        btnResults.setOnClickListener(this);
        btnAbout = (Button) view.findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);

        txtWelcomeMessage = (TextView) view.findViewById(R.id.txtPlayername);
        if (name != null) {
            txtWelcomeMessage.setText("Player: " + name);
        } else {
            txtWelcomeMessage.setText("Player");
        }

        return view;
    }

    private void startGame() {
        FragmentController.get().transactFragments(getActivity(), new PreGameFragment(), "game_fragment");
    }

    private void about() {
        AboutFragment aboutFragment = new AboutFragment();
        FragmentController.get().transactFragments(getActivity(), aboutFragment, "about_fragment");
    }

    private void results() {
        if (name != null) {
            Fragment resultFragment = ResultFragment.newInstance(name);
            FragmentController.get().transactFragments(getActivity(), resultFragment, "result_Fragment");
        } else {
            Fragment resultFragment = ResultFragment.newInstance("Jesper");
            FragmentController.get().transactFragments(getActivity(), resultFragment, "result_Fragment");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAbout.getId()) {
            about();
        }

        if (v.getId() == btnStartGame.getId()) {
            startGame();
        }

        if (v.getId() == btnResults.getId()) {
            results();
        }
    }
}
