package androidcourse.awesomerafflegame.fragments;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.sensors.ShakeSensor;

/**
 * Created by Jesper on 01/05/16.
 */
public class GameFragment extends Fragment implements View.OnClickListener, ShakeSensor.OnShakeListener {

    private ShakeSensor shakeSensor;

    private AnimationDrawable diceAnim, diceAnim2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        this.shakeSensor = new ShakeSensor(getActivity());
        this.shakeSensor.setOnShakeListener(this);

        ImageView imgDice = (ImageView) view.findViewById(R.id.img_dice);
        imgDice.setBackgroundResource(R.drawable.anim_dice_1to6);
        diceAnim = (AnimationDrawable) imgDice.getBackground();
        diceAnim.selectDrawable(new Random().nextInt(6));

        ImageView imgDice2 = (ImageView) view.findViewById(R.id.img_dice_2);
        imgDice2.setBackgroundResource(R.drawable.anim_dice_3to2);
        diceAnim2 = (AnimationDrawable) imgDice2.getBackground();
        diceAnim2.selectDrawable(new Random().nextInt(6));

        return view;
    }

    private void resetDice() {
        diceAnim.stop();
        diceAnim2.stop();
        diceAnim.selectDrawable(0);
        diceAnim2.selectDrawable(0);
    }

    private void rollDice(int duration) {
        diceAnim.start();
        diceAnim2.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDice();
            }
        }, duration);
    }

    private void vibrateDevice(int ms) {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(ms);
    }

    private void stopDice() {
        int face = new Random().nextInt(6);
        int face2 = new Random().nextInt(6);

        diceAnim.stop();
        diceAnim.selectDrawable(face);
        diceAnim2.stop();
        switch (face2 - 2) {
            case -2:
                diceAnim2.selectDrawable(4);
                break;
            case -1:
                diceAnim2.selectDrawable(5);
                break;
            default:
                diceAnim2.selectDrawable(face2 - 2);
                break;
        }

        Toast.makeText(
                getActivity(),
                "Du slog en " + (face + 1) + "'er og en " + (face2 + 1) + "'er",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onShake(int count) {
        resetDice();
        vibrateDevice(1000);
        rollDice(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        shakeSensor.register();
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        shakeSensor.unregister();
        super.onPause();
    }
}
