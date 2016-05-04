package androidcourse.awesomerafflegame.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.domain.FragmentController;

/**
 * Created by Jesper on 30/03/16.
 */
public class PreGameFragment extends Fragment implements View.OnClickListener {
    private final static String DEBUG_TAG = PreGameFragment.class.getSimpleName();
    public final static int REQUEST_ENABLE_BT = 1000;
    private static final UUID UUID_TAG = UUID.fromString("AF760977-14B2-457A-9D2C-6D8926E5A18F");
    private static final int REQUEST_ENABLE = 1;
    private static final int REQUEST_DISCOVERABLE = 2;
    private static final String SEARCH_NAME = "awesomeRaffleGame";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;

    private Button btnVsComputer;
    private Button btnVsPlayer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);
        btnVsComputer = (Button) view.findViewById(R.id.btnVsComputer);
        btnVsComputer.setOnClickListener(this);
        btnVsPlayer = (Button) view.findViewById(R.id.btnVsPlayer);
        btnVsPlayer.setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PreGameFragment.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            BluetoothAdapter BT = BluetoothAdapter.getDefaultAdapter();
            String address = BT.getAddress();
            String name = BT.getName();
            String toastText = name + " : " + address;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
            //Giv en ting tilbage til bluetooth handler.
        } else if (requestCode == PreGameFragment.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            //BRUGER SAGDE NEJ TAK TIL AT SLÅ BLUETOOTH TIL.
        }
    }


    public void setUpBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            enableBluetooth();
        }
    }

    public void enableBluetooth() {
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent disIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            disIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(disIntent, PreGameFragment.REQUEST_ENABLE_BT);
        }
        startListening();
    }

    private void startListening() {
        AcceptTask task = new AcceptTask();
        task.execute(UUID_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, intentFilter);
        intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException ex) {
            Log.e(DEBUG_TAG, "Error closing bluetooth socket: " + ex.getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnVsComputer.getId()) {
            FragmentController.get().transactFragments(getActivity(), new GameFragment(), "game_fragment");
        }
        if (v.getId() == btnVsPlayer.getId()) {
            setUpBluetooth();
        }
    }

    private class AcceptTask extends AsyncTask<UUID, Void, BluetoothSocket> {
        @Override
        protected BluetoothSocket doInBackground(UUID... params) {
            String name = bluetoothAdapter.getName();
            try {
                //While listening, set the discovery name to
                // a specific value
                bluetoothAdapter.setName(SEARCH_NAME);
                BluetoothServerSocket socket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothRecipe", params[0]);
                BluetoothSocket connected = socket.accept();
                //Reset the BT adapter name
                bluetoothAdapter.setName(name);
                return connected;
            } catch (IOException e) {
                e.printStackTrace();
                bluetoothAdapter.setName(name);
                return null;
            }
        }

        @Override
        protected void onPostExecute(BluetoothSocket socket) {
            if (socket == null) {
                return;
            }
            bluetoothSocket = socket;
            ConnectedTask task = new ConnectedTask();
            task.execute(bluetoothSocket);
        }
    }

    private class ConnectedTask extends
            AsyncTask<BluetoothSocket, Void, String> {
        @Override
        protected String doInBackground(
                BluetoothSocket... params) {
            InputStream in = null;
            OutputStream out = null;
            try {
                //Send your data
                out = params[0].getOutputStream();
                String email = "HEJ";
                out.write(email.getBytes());
                //Receive the other's data
                in = params[0].getInputStream();
                byte[] buffer = new byte[1024];
                in.read(buffer);
                //Create a clean string from results
                String result = new String(buffer);
                //Close the connection
                bluetoothSocket.close();
                return result.trim();
            } catch (Exception exc) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), result,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device =
                        intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE);
                if (TextUtils.equals(device.getName(),
                        SEARCH_NAME)) {
                    //Matching device found, connect
                    bluetoothAdapter.cancelDiscovery();
                    try {
                        bluetoothSocket = device
                                .createRfcommSocketToServiceRecord(
                                        UUID_TAG);
                        bluetoothSocket.connect();
                        ConnectedTask task = new ConnectedTask();
                        task.execute(bluetoothSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //When discovery is complete
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Toast.makeText(getActivity(), "Fundet", Toast.LENGTH_LONG).show();
            }
        }
    };
}



