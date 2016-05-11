package androidcourse.awesomerafflegame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.controllers.SharedPreferencesManager;

/**
 * Created by Jesper on 11/05/16.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private EditText edtChangePlayerName;
    private Button btnSaveChanges;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSaveChanges = (Button) view.findViewById(R.id.btn_save_changes);
        btnSaveChanges.setOnClickListener(this);
        edtChangePlayerName = (EditText) view.findViewById(R.id.edt_change_player_name);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnSaveChanges.getId()){
            if(!edtChangePlayerName.getText().toString().equalsIgnoreCase("")){
                SharedPreferencesManager.get().setPlayerName(edtChangePlayerName.getText().toString());
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }
}
