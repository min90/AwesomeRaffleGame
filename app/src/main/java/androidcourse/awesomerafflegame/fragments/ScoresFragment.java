package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.adapters.RCVDividerItemDecoration;
import androidcourse.awesomerafflegame.adapters.GameRCVAdapter;
import androidcourse.awesomerafflegame.domain.Game;
import androidcourse.awesomerafflegame.persistence.DatabaseHandler;

/**
 * Created by Mads on 30/04/16.
 */
public class ScoresFragment extends Fragment {
    public static final String DEBUG_TAG = ScoresFragment.class.getSimpleName();

    private RecyclerView gamesRCV;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);

        List<Game> games = new DatabaseHandler(getActivity()).getAllGames();
        gamesRCV = (RecyclerView) view.findViewById(R.id.games_rcv);
        TextView txtNoPrevious = (TextView) view.findViewById(R.id.txt_no_previous);

        //reverse the list to show newest games first
        Collections.reverse(games);

        if(games.isEmpty()){
            if(gamesRCV.getVisibility() == View.VISIBLE){
                gamesRCV.setVisibility(View.GONE);
            }
            txtNoPrevious.setVisibility(View.VISIBLE);
        }

        setUpRecyclerView(games);
        return view;
    }

    private void setUpRecyclerView(List<Game> games) {
        gamesRCV.setLayoutManager(new LinearLayoutManager(getActivity()));
        gamesRCV.addItemDecoration(new RCVDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        gamesRCV.setAdapter(new GameRCVAdapter(getActivity(), games));

    }
}
