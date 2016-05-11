package androidcourse.awesomerafflegame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.controllers.FragmentController;
import androidcourse.awesomerafflegame.persistence.SharedPreferencesManager;
import androidcourse.awesomerafflegame.bluetooth.BluetoothHandler;
import androidcourse.awesomerafflegame.fragments.SettingsFragment;
import androidcourse.awesomerafflegame.fragments.StartFragment;

public class MainActivity extends AppCompatActivity {
    public static final String DEBUG_TAG = MainActivity.class.getSimpleName();

    public static Toolbar toolbar;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        SharedPreferencesManager.init(this);


        getVersionName();

        setUpToolbar();

        container = (FrameLayout) findViewById(R.id.fragment_container);
        container.setVisibility(View.VISIBLE);

        FragmentController.get().transactFragments(this, new StartFragment(), "start_fragment");
    }

    private void getVersionName() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            SharedPreferencesManager.get().setVersionName(version);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(DEBUG_TAG, "Not able to determine version", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }


    private void setUpToolbar() {
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
            FragmentController.get().transactFragments(this, new SettingsFragment(), "settings_fragment");
            return true;
        }
        if (id == R.id.action_log_out) {
            SharedPreferencesManager.get().setFirstTimeUser(true);
            SharedPreferencesManager.get().setPlayerName("Player 1");
            FragmentController.get().transactFragments(this, new StartFragment(), "start_fragment");
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        BluetoothHandler.init(this);
        BluetoothHandler.get().onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothHandler.get().onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothHandler.get().onResume();
    }
}
