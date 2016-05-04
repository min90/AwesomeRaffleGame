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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.sensors.ShakeSensor;

/**
 * Created by Jesper on 01/05/16.
 */
public class GameFragment extends Fragment implements View.OnClickListener, ShakeSensor.OnShakeListener {

    private final int PLAYER_ONE = 1;
    private final int PLAYER_TWO = 2;

    private ShakeSensor shakeSensor;

    private AnimationDrawable dieOneAnimation, dieTwoAnimation;

    private TextView tvCurrentPlayer;
    private TextView tvPlayerOneScore;
    private TextView tvPlayerTwoScore;
    private TextView tvRoundScore;
    private TextView tvAnnouncement;

    private Button bHandOverDice;

    private int currentPlayer;

    private int playerOneTotalScore;
    private int playerTwoTotalScore;
    private int roundScore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        this.shakeSensor = new ShakeSensor(getActivity());
        this.shakeSensor.setOnShakeListener(this);

        currentPlayer = PLAYER_ONE;

        tvCurrentPlayer = (TextView) view.findViewById(R.id.tv_current_player);

        tvPlayerOneScore = (TextView) view.findViewById(R.id.player_one_score);
        tvPlayerTwoScore = (TextView) view.findViewById(R.id.player_two_score);

        tvRoundScore = (TextView) view.findViewById(R.id.round_score);

        tvAnnouncement = (TextView) view.findViewById(R.id.announcement);

        playerOneTotalScore = 0;
        roundScore = 0;

        bHandOverDice = (Button) view.findViewById(R.id.btn_hand_over_dice);
        bHandOverDice.setOnClickListener(this);

        ImageView ivDieOne = (ImageView) view.findViewById(R.id.img_dice);
        ivDieOne.setBackgroundResource(R.drawable.anim_dice_1to6);
        dieOneAnimation = (AnimationDrawable) ivDieOne.getBackground();
        dieOneAnimation.selectDrawable(new Random().nextInt(6));

        ImageView ivDieTwo = (ImageView) view.findViewById(R.id.img_dice_2);
        ivDieTwo.setBackgroundResource(R.drawable.anim_dice_3to2);
        dieTwoAnimation = (AnimationDrawable) ivDieTwo.getBackground();
        dieTwoAnimation.selectDrawable(new Random().nextInt(6));

        return view;
    }

    private void resetDice() {
        tvAnnouncement.setText("");
        dieOneAnimation.stop();
        dieTwoAnimation.stop();
        dieOneAnimation.selectDrawable(0);
        dieTwoAnimation.selectDrawable(0);
    }

    private void rollDice(int duration) {
        dieOneAnimation.start();
        dieTwoAnimation.start();

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
        int faceOne = new Random().nextInt(6);
        int faceTwo = new Random().nextInt(6);

        dieOneAnimation.stop();
        dieOneAnimation.selectDrawable(faceOne);

        dieTwoAnimation.stop();
        switch (faceTwo - 2) {
            case -2:
                dieTwoAnimation.selectDrawable(4);
                break;
            case -1:
                dieTwoAnimation.selectDrawable(5);
                break;
            default:
                dieTwoAnimation.selectDrawable(faceTwo - 2);
                break;
        }

        // Correct zero-indexed faces
        int actualFaceOne = faceOne + 1;
        int actualFaceTwo = faceTwo + 1;

        if (currentPlayer == PLAYER_ONE) {
            updatePlayerOneScore(actualFaceOne, actualFaceTwo);
        } else {
            updatePlayerTwoScore(actualFaceOne, actualFaceTwo);
        }
    }

    private void updatePlayerOneScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            playerOneTotalScore = 0;
            handOverDice("Player 1 lost all points. Handing over dice");
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                playerOneTotalScore -= roundScore;
            }
            handOverDice("Player 1 lost points for this round. Handing over dice");
        } else {
            roundScore += (faceOne + faceTwo);
            playerOneTotalScore += (faceOne + faceTwo);
            if (playerOneTotalScore >= 100) {
                playerOneTotalScore = 100;
                tvAnnouncement.setText("Player 1 won!");
            }
        }

        tvRoundScore.setText(Integer.toString(roundScore));
        tvPlayerOneScore.setText(Integer.toString(playerOneTotalScore));
    }

    private void updatePlayerTwoScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            playerTwoTotalScore = 0;
            handOverDice("Player 2 lost all points. Handing over dice");
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                playerTwoTotalScore -= roundScore;
            }
            handOverDice("Player 2 lost points for this round. Handing over dice");
        } else {
            roundScore += (faceOne + faceTwo);
            playerTwoTotalScore += (faceOne + faceTwo);
            if (playerTwoTotalScore >= 100) {
                playerTwoTotalScore = 100;
                tvAnnouncement.setText("Player 2 won!");
            }
        }

        tvRoundScore.setText(Integer.toString(roundScore));
        tvPlayerTwoScore.setText(Integer.toString(playerTwoTotalScore));
    }

    private void handOverDice(String announcement) {
        roundScore = 0;
        tvRoundScore.setText("0");

        tvAnnouncement.setText(announcement);

        if (currentPlayer == PLAYER_ONE) {
            currentPlayer = PLAYER_TWO;
        } else {
            currentPlayer = PLAYER_ONE;
        }

        tvCurrentPlayer.setText("Player " + currentPlayer + " has the dice");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == bHandOverDice.getId()) {
            handOverDice("Handing over dice");
        }
    }

    @Override
    public void onShake(int count) {
        resetDice();
        //vibrateDevice(1000);
        rollDice(1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        shakeSensor.register();
    }

    @Override
    public void onPause() {
        shakeSensor.unregister();
        super.onPause();
    }
}
