package androidcourse.awesomerafflegame;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Jesper on 30/03/16.
 */
public class BluetoothHandler {
    public final static int REQUEST_ENABLE_BT = 1000;

    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothHandler(Context context) {
        this.context = context;
        setUpBluetooth();
    }

    public boolean setUpBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(context, "You're device does not support bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public Intent enableBluetooth(){
        if(bluetoothAdapter != null && !bluetoothAdapter.isEnabled()){
            return new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        } else {
            return null;
        }
    }
}
