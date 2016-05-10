package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidcourse.awesomerafflegame.R;

/**
 * Created by Jesper on 09/05/16.
 */
public class FirstTimeUsersFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first_time, container, false);
    }
}
