package androidcourse.awesomerafflegame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import androidcourse.awesomerafflegame.fragments.StartFragment;

import androidcourse.awesomerafflegame.domain.FragmentController;
import androidcourse.awesomerafflegame.fragments.StartFragment;
import androidcourse.awesomerafflegame.sensors.ShakeSensor;

public class MainActivity extends AppCompatActivity {
    public static Toolbar toolbar;

    private ShakeSensor shakeSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.shakeSensor = new ShakeSensor(this);

        setUpToolbar();

        createBluetooth();

        createStartView();

    }

    private void createStartView(){
        StartFragment startFragment = new StartFragment();
        FragmentController.get().transactFragments(this, startFragment, "start_fragment");
    }

    private void createBluetooth(){
        BluetoothHandler bluetoothHandler = new BluetoothHandler(this);
        Intent intent = bluetoothHandler.enableBluetooth();
        if (intent != null) {
            startActivityForResult(intent, BluetoothHandler.REQUEST_ENABLE_BT);
        }
    }

    private void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeSensor.register();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shakeSensor.unregister();
    }
}
