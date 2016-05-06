package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;


import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.adapters.DividerItemDecoration;
import androidcourse.awesomerafflegame.adapters.GameRCVAdapter;
import androidcourse.awesomerafflegame.models.Game;
import androidcourse.awesomerafflegame.persistence.DatabaseHandler;

/**
 * Created by Mads on 30/04/16.
 */
public class ScoresFragment extends Fragment {
    public static final String DEBUG_TAG = ScoresFragment.class.getSimpleName();
    public static final String RESULT_TAG = "results";

    public static ScoresFragment newInstance(String name) {
        ScoresFragment fragment = new ScoresFragment();
        Bundle args = new Bundle();

        args.putString(RESULT_TAG, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);

        List<Game> games = new DatabaseHandler(getActivity()).getAllGames();

        //reverse the list to show newest games first
        Collections.reverse(games);

        setUpRecyclerView(games, view);
        return view;
    }

    private void setUpRecyclerView(List<Game> games, View view) {
        RecyclerView gamesRCV = (RecyclerView) view.findViewById(R.id.games_rcv);
        gamesRCV.setLayoutManager(new LinearLayoutManager(getActivity()));
        gamesRCV.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        gamesRCV.setAdapter(new GameRCVAdapter(getActivity(), games));

    }
}
