package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.controllers.FragmentController;

/**
 * Created by Jesper on 05/05/16.
 */
public class GameSetUpFragment extends Fragment implements View.OnClickListener {
    private static final String DEBUG_TAG = GameSetUpFragment.class.getSimpleName();

    private Button btnVsComputer;
    private Button btnVsPlayer;
    private Button btnListen;
    private Button btnScan;
    private LinearLayout blueLayout;
    private TextView txtTest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        btnVsComputer = (Button) view.findViewById(R.id.btnVsComputer);
        btnVsComputer.setOnClickListener(this);
        btnVsPlayer = (Button) view.findViewById(R.id.btnVsPlayer);
        btnVsPlayer.setOnClickListener(this);
        btnListen = (Button) view.findViewById(R.id.btnListen);
        btnListen.setOnClickListener(this);
        btnScan = (Button) view.findViewById(R.id.btnScan);
        btnScan.setOnClickListener(this);
        blueLayout = (LinearLayout) view.findViewById(R.id.blueLayout);
        txtTest = (TextView) view.findViewById(R.id.testTxtView);

        return view;
    }



    @Override
    public void onClick(View v) {
        if (v.getId() == btnVsPlayer.getId()) {
        }
        if (v.getId() == btnVsComputer.getId()) {
            FragmentController.get().transactFragments(getActivity(), GameFragment.newInstance(GameFragment.VS_COMPUTER), "game_fragment");
            //sendMessage("Hejsa Mads");
        }
    }
}
