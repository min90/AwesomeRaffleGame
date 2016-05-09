package androidcourse.awesomerafflegame.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.activities.DeviceListActivity;
import androidcourse.awesomerafflegame.adapters.Constants;
import androidcourse.awesomerafflegame.controllers.FragmentController;
import androidcourse.awesomerafflegame.models.Game;
import androidcourse.awesomerafflegame.sensors.BluetoothGameService;

/**
 * Created by Jesper on 05/05/16.
 */
public class GameSetUpFragment extends Fragment implements View.OnClickListener {
    private static final String DEBUG_TAG = GameSetUpFragment.class.getSimpleName();

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 100;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 200;
    private static final int REQUEST_ENABLE_BT = 300;

    /**
     * Name of the connected device
     */
    private String connectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> gameArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mResultOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothGameService bluetoothGameService = null;

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
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Your device is not supported", Toast.LENGTH_LONG).show();
            //TODO Hvis den er null så bed brugeren om at spille mod computeren.
        }
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
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (bluetoothAdapter != null && bluetoothGameService == null) {
            //Setup game with bluetooth
            setupGame();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothGameService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (bluetoothGameService.getState() == BluetoothGameService.STATE_NONE) {
                // Start the Bluetooth game
                bluetoothGameService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothGameService != null) {
            bluetoothGameService.stop();
        }
    }

    private void setupGame() {
        Log.d(DEBUG_TAG, "SetupGame");

        bluetoothGameService = new BluetoothGameService(getActivity(), handler);

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (bluetoothGameService.getState() != BluetoothGameService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            Log.d(DEBUG_TAG, "Message: " + message);
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            bluetoothGameService.write(send);
        }
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(DEBUG_TAG, "On activity result called");
        Log.d(DEBUG_TAG, "Request: " + requestCode);
        Log.d(DEBUG_TAG, "Result: " + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(DEBUG_TAG, data.toString());
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(DEBUG_TAG, data.toString());
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a game session
                    //TODO Set up game with bluetooth
                    Log.d(DEBUG_TAG, "Resultat: " + resultCode);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(DEBUG_TAG, "BT not enabled");
                    Toast.makeText(getActivity(), "Bluetooth was not enabled. Leaving Game!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        Log.d(DEBUG_TAG, "Address: " + address);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        Log.d(DEBUG_TAG, "Device: " + device.toString());
        bluetoothGameService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_game, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Log.d(DEBUG_TAG, "Connecting secure");
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                Log.d(DEBUG_TAG, "Connecting insecure");
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                Log.d(DEBUG_TAG, "Making discoverable");
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }


    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothGameService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, connectedDeviceName));
                            break;
                        case BluetoothGameService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothGameService.STATE_LISTEN:
                        case BluetoothGameService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    txtTest.setText("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    txtTest.setText(connectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == btnVsPlayer.getId()) {
            setupGame();
        }
        if (v.getId() == btnVsComputer.getId()) {
            FragmentController.get().transactFragments(getActivity(), GameFragment.newInstance(GameFragment.VS_COMPUTER), "game_fragment");
            //sendMessage("Hejsa Mads");
        }
    }
}
