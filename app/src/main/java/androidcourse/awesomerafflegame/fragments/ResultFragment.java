package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.adapters.ResultAdapter;
import androidcourse.awesomerafflegame.models.Results;

/**
 * Created by Jesper on 30/04/16.
 */
public class ResultFragment extends Fragment {
    public static final String DEBUG_TAG = ResultFragment.class.getSimpleName();
    public static final String RESULT_TAG = "results";

    public static ResultFragment newInstance(String name) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();

        args.putString(RESULT_TAG, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        Bundle args = getArguments();
        Log.d(DEBUG_TAG, "Inde i create view");

        String name = args.getString(RESULT_TAG);

        List<Results> resultses = new ArrayList<>();
        Results results = new Results();
        results.setUser(name);
        results.setScore(100);
        resultses.add(results);

        Log.d(DEBUG_TAG, "Results: " + resultses.toString());

        setUpRecyclerView(resultses, view);
        return view;
    }

    private void setUpRecyclerView(List<Results> resultses, View view) {
        RecyclerView resultView = (RecyclerView) view.findViewById(R.id.recyclerview_results);
        resultView.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        resultView.setAdapter(new ResultAdapter(getActivity(), resultses));
    }
}
