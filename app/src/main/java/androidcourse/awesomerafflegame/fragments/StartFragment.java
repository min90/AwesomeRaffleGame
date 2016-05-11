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
import androidcourse.awesomerafflegame.controllers.FragmentController;
import androidcourse.awesomerafflegame.controllers.SharedPreferencesManager;

/**
 * Created by Jesper on 04/04/16.
 */
public class StartFragment extends Fragment implements View.OnClickListener {
    public static final String DEBUG_TAG = StartFragment.class.getSimpleName();

    private Button btnStartGame;
    private Button btnResults;
    private Button btnAbout;

    private String name;
    private boolean nameChanged = false;

    private TextView txtWelcomeMessage, versionTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        name = SharedPreferencesManager.get().getPlayerName();

        btnStartGame = (Button) view.findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(this);
        btnResults = (Button) view.findViewById(R.id.btnResults);
        btnResults.setOnClickListener(this);
        btnAbout = (Button) view.findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);
        versionTxt = (TextView) view.findViewById(R.id.versionTxt);

        txtWelcomeMessage = (TextView) view.findViewById(R.id.txtPlayername);

        showInfoDialog();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        versionTxt.setText(SharedPreferencesManager.get().getVersionName());
        if (nameChanged) {
            txtWelcomeMessage.setText("Player: " + name);
        } else {
            txtWelcomeMessage.setText("Welcome");
        }

    }

    private void showInfoDialog() {
        if (SharedPreferencesManager.get().getFirstTimeUser()) {
            FragmentController.get().transactDialogFragment(getActivity(), FirstTimeUsersFragment.newInstance(name), "info_fragment");
            SharedPreferencesManager.get().setFirstTimeUser(false);
            nameChanged = true;
        }
    }

    private void startGame() {
        FragmentController.get().transactFragments(getActivity(), new PreGameFragment(), "game_fragment");
    }

    private void settings() {
        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentController.get().transactFragments(getActivity(), settingsFragment, "settings_fragment");
    }

    private void results() {
        Fragment resultFragment = new ScoresFragment();
        FragmentController.get().transactFragments(getActivity(), resultFragment, "result_Fragment");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAbout.getId()) {
            settings();
        }

        if (v.getId() == btnStartGame.getId()) {
            startGame();
        }

        if (v.getId() == btnResults.getId()) {
            results();
        }
    }
}
