package androidcourse.awesomerafflegame.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import androidcourse.awesomerafflegame.bluetooth.listeners.OnBluetoothMessageReceivedListener;
import androidcourse.awesomerafflegame.bluetooth.listeners.OnBluetoothConnectionListener;
import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.activities.DeviceListActivity;

/**
 * This fragment controls Bluetooth to communicate with other devices.
 *
 * Inspired from Androids Bluetooth Chat example
 * https://developer.android.com/samples/BluetoothChat/index.html
 */
public class BluetoothHandler {

    private static final String TAG = BluetoothHandler.class.getSimpleName();

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private OnBluetoothMessageReceivedListener onBluetoothMessageReceivedListener;
    private OnBluetoothConnectionListener onBluetoothConnectionListener;

    private Context context;

    private String mConnectedDeviceName = null;

    private BluetoothAdapter mBluetoothAdapter = null;


    private BluetoothGameService mGameService = null;

    private static BluetoothHandler instance = null;

    public static BluetoothHandler get() {
        if (instance == null) {
            throw new Error("BluetoothHandler not yet initialized");
        }
        return instance;
    }

    public static void init(Context context) {
        instance = new BluetoothHandler(context);
    }

    private BluetoothHandler(Context context) {
        this.context = context;

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
    }

    public void onStart() {
        // If BT is not on, request that it be enabled.
        // setupGame() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled() && mBluetoothAdapter != null) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) context).startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the game session
        } else if (mGameService == null) {
            setupGame();
        }
    }

    public void stopGameService() {
        if (mGameService != null) {
            mGameService.stop();
        }
    }

    public void onDestroy() {
        stopGameService();
    }

    public void onResume() {
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mGameService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mGameService.getState() == BluetoothGameService.STATE_NONE) {
                // Start the Bluetooth game services
                mGameService.start();
            }
        }
    }

    public void setOnBluetoothMessageReceivedListener(OnBluetoothMessageReceivedListener listener) {
        this.onBluetoothMessageReceivedListener = listener;
    }

    public void setOnBluetoothConnectionListener(OnBluetoothConnectionListener listener) {
        this.onBluetoothConnectionListener = listener;
    }

    /**
     * Set up the UI and background operations for game.
     */
    private void setupGame() {
        Log.d(TAG, "setupGame()");

        // Initialize the BluetoothGameService to perform bluetooth connections
        mGameService = new BluetoothGameService(context, mHandler);
    }


    public void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);
        }
    }

    public void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mGameService.getState() != BluetoothGameService.STATE_CONNECTED) {
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothGameService to write
            byte[] send = message.getBytes();
            mGameService.write(send);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = (FragmentActivity) context;
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    Log.d("MESSAGE_WRITE", writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    onBluetoothMessageReceivedListener.onMessageReceived(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        onBluetoothConnectionListener.onBluetoothConnection();
                        Toast.makeText(activity, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a game session
                    setupGame();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(context, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mGameService.connect(device, secure);
    }

}
