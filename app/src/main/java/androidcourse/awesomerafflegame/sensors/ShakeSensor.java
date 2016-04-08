package androidcourse.awesomerafflegame.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

/**
 * Created by mads on 08-04-16.
 */
public class ShakeSensor implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private float accel;
    private float accelCurrent;
    private float accelLast;

    public ShakeSensor (Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accel = 0.00f;
        this.accelCurrent = SensorManager.GRAVITY_EARTH;
        this.accelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        accelLast = accelCurrent;
        accelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = accelCurrent - accelLast;
        accel = accel * 0.9f + delta; // perform low-pass filter

        if(deviceShaken()) {
            Toast.makeText(context, "Shake it baybayy", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private boolean deviceShaken() {
        return (accel > 12);
    }

    public void register() {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

}
