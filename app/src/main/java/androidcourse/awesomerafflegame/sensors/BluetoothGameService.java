package androidcourse.awesomerafflegame.sensors;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import androidcourse.awesomerafflegame.adapters.Constants;

/**
 * Created by Jesper on 05/05/16.
 */
public class BluetoothGameService {
    private static final String DEBUG_TAG = BluetoothGameService.class.getSimpleName();

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "AweSomeRaffleGameSecure";
    private static final String NAME_INSECURE = "AweSomeRaffleGameInsecure";

    private static final UUID UUID_SECURE = UUID.fromString("439E6C78-5E74-453B-8F0F-C85700BDE40C");
    private static final UUID UUID_INSECURE = UUID.fromString("64C9DBAB-00BC-477C-A3F8-8431A38307BA");

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;

    public BluetoothGameService(Context context, Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(DEBUG_TAG, "setState() " + state + " -> " + state);
        this.state = state;

        // Give the new state to the Handler so the UI Activity can update
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return state;
    }

    /**
     * Start the game service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        Log.d(DEBUG_TAG, "start");

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }
        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread(false);
            insecureAcceptThread.start();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(DEBUG_TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Start the thread to connect with the given device
        Log.d(DEBUG_TAG, "Connect to : " + device.toString());
        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        Log.d(DEBUG_TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        // Cancel the accept thread because we only want to connect to one device
        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }
        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(DEBUG_TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
            r = connectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(){
        // Send a failure message back to the game
        Message message = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle args = new Bundle();
        args.putString(Constants.TOAST, "Unable to connect to the device");
        message.setData(args);
        handler.sendMessage(message);

        //Start the service over to restart listening mode
        BluetoothGameService.this.start();
    }


    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost(){
        // Send a failure message back to the game
        Message message = handler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle args = new Bundle();
        args.putString(Constants.TOAST, "The connection to the device was lost");
        message.setData(args);
        handler.sendMessage(message);

        //Start the service over to restart listening mode
        BluetoothGameService.this.start();
    }

    private class AcceptThread extends Thread {
        //The local server socket
        private final BluetoothServerSocket serverSocket;
        private String socketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmpSocket = null;
            socketType = secure ? "Secure " : "Insecure";


            //Create a new listening server socket
            try {
                if (secure) {
                    tmpSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_SECURE);
                } else {
                    tmpSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, UUID_INSECURE);
                }
            } catch (IOException ex) {
                Log.d(DEBUG_TAG, "Error listening to socket: " + socketType + " " + "failed");

            }
            serverSocket = tmpSocket;
        }

        public void run() {
            Log.d(DEBUG_TAG, "Socket type: " + socketType + " BEGIN Acceptthread " + this);
            setName("Accepthread: " + socketType);

            BluetoothSocket socket;

            while (state != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = serverSocket.accept();
                } catch (IOException ex) {
                    Log.d(DEBUG_TAG, "Error listening to socket in run: " + socketType + " " + "failed");
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothGameService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), socketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(DEBUG_TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.d(DEBUG_TAG, "Ended Acceptthread type: " + socketType);
        }

        public void cancel() {
            Log.d(DEBUG_TAG, "Socket Type" + socketType + "cancel " + this);
            try {
                serverSocket.close();
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "Socket Type" + socketType + "close() of server failed");
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            this.device = device;
            BluetoothSocket tmpSocket = null;
            socketType = secure ? "Secure " : "Insecure";

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure) {
                    tmpSocket = device.createRfcommSocketToServiceRecord(UUID_SECURE);
                } else {
                    tmpSocket = device.createInsecureRfcommSocketToServiceRecord(UUID_INSECURE);
                }
            } catch (IOException ex) {
                Log.e(DEBUG_TAG, "Socket Type: " + socketType + "create() failed");
            }
            socket = tmpSocket;
        }

        public void run() {
            Log.d(DEBUG_TAG, "BEGIN mConnectThread SocketType:" + socketType);
            setName("ConnectThread" + socketType);
            // Always cancel discovery because it will slow down a connection
            bluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                socket.connect();

            } catch (IOException ex) {
                // Close the socket
                try {
                    socket.close();
                } catch (IOException e2) {
                    Log.e(DEBUG_TAG, "unable to close() " + socketType + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            //Reset the connectionthread because were done
            synchronized (BluetoothGameService.this) {
                connectedThread = null;
            }

            //Start the connected thread
            connected(socket, device, socketType);
        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException ex){
                Log.e(DEBUG_TAG, "close() of connect " + socketType + " socket failed");
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final OutputStream outputStream;
        private final InputStream inputStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(DEBUG_TAG, "create ConnectedThread: " + socketType);

            this.socket = socket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            //Get the bluetoothsocket input and output streams

            try{
                tmpInputStream = socket.getInputStream();
                tmpOutputStream = socket.getOutputStream();
            } catch (IOException ex){
                Log.e(DEBUG_TAG, "temporary sockets not created");
            }

            outputStream = tmpOutputStream;
            inputStream = tmpInputStream;
        }

        public void run(){
            Log.i(DEBUG_TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            //Keep listening to the Inputstream while connected;
            while(state == STATE_CONNECTED){
                try{
                    //Read from the inputstream;
                    bytes = inputStream.read(buffer);

                    //Send the obtained bytes to the game
                    handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();

                } catch (IOException ex) {
                    Log.e(DEBUG_TAG, "disconnected");
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothGameService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer){
            try {
                outputStream.write(buffer);

                // Share the sent message back to the UI Activity
                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException ex){
                Log.e(DEBUG_TAG, "Exception during write");
            }
        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException ex){
                Log.e(DEBUG_TAG, "close() of connect socket failed");
            }
        }
    }
}
