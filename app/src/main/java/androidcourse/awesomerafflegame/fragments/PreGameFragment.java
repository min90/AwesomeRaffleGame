package androidcourse.awesomerafflegame.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidcourse.awesomerafflegame.bluetooth.BluetoothHandler;
import androidcourse.awesomerafflegame.bluetooth.listeners.OnBluetoothConnectionListener;
import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.activities.DeviceListActivity;
import androidcourse.awesomerafflegame.controllers.FragmentController;

/**
 * Created by Mads on 09/05/2016.
 */
public class PreGameFragment extends Fragment implements View.OnClickListener, OnBluetoothConnectionListener {

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private Button btnVsComputer;
    private Button btnVsPlayer;
    private Button btnSecure;
    private Button btnInsecure;
    private Button btnDisco;
    private Button btnCancel;
    private LinearLayout blueLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        BluetoothHandler.get().setOnBluetoothConnectionListener(this);

        btnVsComputer = (Button) view.findViewById(R.id.btn_vs_computer);
        btnVsComputer.setOnClickListener(this);

        btnVsPlayer = (Button) view.findViewById(R.id.btn_vs_player);
        btnVsPlayer.setOnClickListener(this);

        btnSecure = (Button) view.findViewById(R.id.btn_connect_secure);
        btnSecure.setOnClickListener(this);

        btnInsecure = (Button) view.findViewById(R.id.btn_connect_insecure);
        btnInsecure.setOnClickListener(this);

        btnDisco = (Button) view.findViewById(R.id.btn_make_disco);
        btnDisco.setOnClickListener(this);

        blueLayout = (LinearLayout) view.findViewById(R.id.blue_layout);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnVsComputer.getId()) {
            blueLayout.setVisibility(View.GONE);
            FragmentController.get().transactFragments(getActivity(), GameFragment.newInstance(GameFragment.VS_COMPUTER), "game_fragment");
        }
        if (v.getId() == btnVsPlayer.getId()) {
            btnVsComputer.setEnabled(false);
            blueLayout.setVisibility(View.VISIBLE);
        }
        if (v.getId() == btnSecure.getId()) {
            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        }
        if (v.getId() == btnInsecure.getId()) {
            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
        }
        if (v.getId() == btnDisco.getId()) {
            BluetoothHandler.get().ensureDiscoverable();
        }
        if (v.getId() == btnCancel.getId()) {
            blueLayout.setVisibility(View.GONE);
            btnVsComputer.setEnabled(true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BluetoothHandler.get().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBluetoothConnection() {
        FragmentController.get().transactFragments(getActivity(), GameFragment.newInstance(GameFragment.VS_PLAYER), "game_fragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        BluetoothHandler.get().onResume();
    }
}
