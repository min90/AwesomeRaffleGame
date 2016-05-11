package androidcourse.awesomerafflegame.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.controllers.SharedPreferencesManager;

/**
 * Created by Jesper on 09/05/16.
 */
public class FirstTimeUsersFragment extends DialogFragment {
    private static final String DEBUG_TAG = FirstTimeUsersFragment.class.getSimpleName();
    private static final String NAME_TAG = "name";

    public static FirstTimeUsersFragment newInstance(String name) {
        FirstTimeUsersFragment fragment = new FirstTimeUsersFragment();
        Bundle args = new Bundle();
        args.putString(NAME_TAG, name);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText edtName = (EditText) view.findViewById(R.id.edt_player_name);
        Button btnOkName = (Button) view.findViewById(R.id.btn_ok_dialog);

        String title = getArguments().getString(NAME_TAG);
        getDialog().setTitle("Welcome: " + title);

        btnOkName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtName.getText().toString().equalsIgnoreCase("")){
                    SharedPreferencesManager.get().setPlayerName(edtName.getText().toString());
                }
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
