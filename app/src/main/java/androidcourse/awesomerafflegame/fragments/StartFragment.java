package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.domain.FragmentController;

/**
 * Created by Jesper on 04/04/16.
 */
public class StartFragment extends Fragment implements View.OnClickListener {

    private Button btnStartGame;
    private Button btnResults;
    private Button btnAbout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        btnStartGame = (Button) view.findViewById(R.id.btnStartGame);
        btnStartGame.setOnClickListener(this);
        btnResults = (Button) view.findViewById(R.id.btnResults);
        btnResults.setOnClickListener(this);
        btnAbout = (Button) view.findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);

        return view;
    }

    private void startGame(){

    }

    private void about(){
        AboutFragment aboutFragment = new AboutFragment();
        FragmentController.get().transactFragments(getActivity(), aboutFragment, "about_fragment");
    }

    private void results(){

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnAbout.getId()){
            about();
        }

        if(v.getId() == btnStartGame.getId()){

        }

        if(v.getId() == btnResults.getId()){

        }
    }
}
