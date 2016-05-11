package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private Button btnSettings;
    private Button btnNoFacebook;
    private Button btnSavePlayerName;
    private LinearLayout playerNameLayout;
    private LinearLayout loggedInLayout;
    private EditText edtPlayerName;

    private String name;

    private TextView txtWelcomeMessage, versionTxt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        name = SharedPreferencesManager.get().getPlayerName();

        btnStartGame = (Button) view.findViewById(R.id.btn_start_game);
        btnStartGame.setOnClickListener(this);
        btnResults = (Button) view.findViewById(R.id.btn_results);
        btnResults.setOnClickListener(this);
        btnSettings = (Button) view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        versionTxt = (TextView) view.findViewById(R.id.tv_version);
        txtWelcomeMessage = (TextView) view.findViewById(R.id.tv_playername);
        loggedInLayout = (LinearLayout) view.findViewById(R.id.logged_in_layout);
        btnNoFacebook = (Button) view.findViewById(R.id.btn_without_facebook);
        btnNoFacebook.setOnClickListener(this);
        btnSavePlayerName = (Button) view.findViewById(R.id.btn_save_player_name);
        btnSavePlayerName.setOnClickListener(this);
        playerNameLayout = (LinearLayout) view.findViewById(R.id.player_name_layout);
        edtPlayerName = (EditText) view.findViewById(R.id.edt_player_name);


        firstTimeUser();

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        versionTxt.setText(SharedPreferencesManager.get().getVersionName());
        txtWelcomeMessage.setText("Player: " + SharedPreferencesManager.get().getPlayerName());


    }

    private void firstTimeUser() {
        if (SharedPreferencesManager.get().getFirstTimeUser()) {
            DialogFragment fragment = new FirstTimeUsersFragment();
            FragmentController.get().transactDialogFragment(getActivity(), fragment, "info_fragment");
            SharedPreferencesManager.get().setFirstTimeUser(false);
        } else {
            btnNoFacebook.setVisibility(View.GONE);
            loggedInLayout.setVisibility(View.VISIBLE);
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
        if (v.getId() == btnSettings.getId()) {
            settings();
        }

        if (v.getId() == btnStartGame.getId()) {
            startGame();
        }

        if (v.getId() == btnResults.getId()) {
            results();
        }
        if (v.getId() == btnNoFacebook.getId()) {
            if (btnNoFacebook.getVisibility() == View.VISIBLE) {
                btnNoFacebook.setVisibility(View.GONE);
                loggedInLayout.setVisibility(View.VISIBLE);
                playerNameLayout.setVisibility(View.VISIBLE);
                txtWelcomeMessage.setVisibility(View.GONE);
            }
        }

        if (v.getId() == btnSavePlayerName.getId()) {
            if (!edtPlayerName.getText().toString().equalsIgnoreCase("")) {
                SharedPreferencesManager.get().setPlayerName(edtPlayerName.getText().toString());
                playerNameLayout.setVisibility(View.GONE);
                txtWelcomeMessage.setText("Player: " + SharedPreferencesManager.get().getPlayerName());
                txtWelcomeMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
