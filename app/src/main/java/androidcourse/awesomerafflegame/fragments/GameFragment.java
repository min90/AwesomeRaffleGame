package androidcourse.awesomerafflegame.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.models.Game;
import androidcourse.awesomerafflegame.persistence.DatabaseHandler;
import androidcourse.awesomerafflegame.sensors.ShakeSensor;

/**
 * Created by Mads on 01/05/16.
 */
public class GameFragment extends Fragment implements View.OnClickListener, ShakeSensor.OnShakeListener {

    private final int PLAYER_ONE = 1;
    private final int PLAYER_TWO = 2;
    private final int COMPUTER = 3;

    private final int SWAP_TURNS = 10;
    private final int LOST_ALL_POINTS = 11;
    private final int LOST_POINTS_FOR_ROUND = 12;

    public static final int VS_PLAYER = 20;
    public static final int VS_COMPUTER = 21;

    private static final String TAG_VERSUS = "versus";

    private ShakeSensor shakeSensor;

    private AnimationDrawable dieOneAnimation, dieTwoAnimation;

    private TextView tvCurrentPlayer;
    private TextView tvPlayerOneScore;
    private TextView tvOpponentScore;
    private TextView tvRoundScore;
    private TextView tvAnnouncement;

    private Button bHandOverDice;

    private int currentPlayer;
    private String currentPlayerName;

    private int playerOneTotalScore;
    private int opponentTotalScore;
    private int roundScore;

    public static GameFragment newInstance(int versus) {
        Bundle args = new Bundle();
        args.putInt(TAG_VERSUS, versus);

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        this.shakeSensor = new ShakeSensor(getActivity());
        this.shakeSensor.setOnShakeListener(this);

        currentPlayer = PLAYER_ONE;
        // Perhaps get players real name
        currentPlayerName = "Player 1";

        initTextViews(view);

        playerOneTotalScore = 0;
        roundScore = 0;

        bHandOverDice = (Button) view.findViewById(R.id.btn_hand_over_dice);
        bHandOverDice.setOnClickListener(this);

        initDiceIcons(view);

        return view;
    }

    private void initTextViews(View view) {
        tvCurrentPlayer = (TextView) view.findViewById(R.id.tv_current_player);

        tvPlayerOneScore = (TextView) view.findViewById(R.id.player_one_score);
        tvOpponentScore = (TextView) view.findViewById(R.id.opponent_score);

        tvRoundScore = (TextView) view.findViewById(R.id.round_score);

        tvAnnouncement = (TextView) view.findViewById(R.id.announcement);
    }

    private void initDiceIcons(View view) {
        ImageView ivDieOne = (ImageView) view.findViewById(R.id.img_dice);
        ivDieOne.setBackgroundResource(R.drawable.anim_dice_1to6);
        dieOneAnimation = (AnimationDrawable) ivDieOne.getBackground();
        dieOneAnimation.selectDrawable(new Random().nextInt(6));

        ImageView ivDieTwo = (ImageView) view.findViewById(R.id.img_dice_2);
        ivDieTwo.setBackgroundResource(R.drawable.anim_dice_3to2);
        dieTwoAnimation = (AnimationDrawable) ivDieTwo.getBackground();
        dieTwoAnimation.selectDrawable(new Random().nextInt(6));
    }

    private void resetDice() {
        dieOneAnimation.stop();
        dieTwoAnimation.stop();
        dieOneAnimation.selectDrawable(0);
        dieTwoAnimation.selectDrawable(0);
    }

    private void rollDice(int duration) {
        dieOneAnimation.start();
        dieTwoAnimation.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopDice();
            }
        }, duration);
    }

    private void vibrateDevice(int ms) {
        if (getActivity() != null) {
            Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(ms);
        }
    }

    private void stopDice() {
        //Reset announcer
        tvAnnouncement.setText("");

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
            updateComputerScore(actualFaceOne, actualFaceTwo);
        }
    }

    private void updatePlayerOneScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            playerOneTotalScore = 0;
            handOverDice(LOST_ALL_POINTS);
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                playerOneTotalScore -= roundScore;
            }
            handOverDice(LOST_POINTS_FOR_ROUND);
        } else {
            roundScore += (faceOne + faceTwo);
            playerOneTotalScore += (faceOne + faceTwo);
            if (playerOneTotalScore >= 100) {
                playerOneTotalScore = 100;
                tvAnnouncement.setText("Player 1 won!");
                endGame(PLAYER_ONE);
            }
        }

        tvRoundScore.setText(Integer.toString(roundScore));
        tvPlayerOneScore.setText(Integer.toString(playerOneTotalScore));
    }

    private void updateComputerScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            opponentTotalScore = 0;
            handOverDice(LOST_ALL_POINTS);
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                opponentTotalScore -= roundScore;
            }
            handOverDice(LOST_POINTS_FOR_ROUND);
        } else {
            roundScore += (faceOne + faceTwo);
            opponentTotalScore += (faceOne + faceTwo);
            if (opponentTotalScore >= 100) {
                opponentTotalScore = 100;
                tvAnnouncement.setText("Computer won!");
                endGame(COMPUTER);
            } else {
                doComputerTurn();
            }
        }

        tvRoundScore.setText(Integer.toString(roundScore));
        tvOpponentScore.setText(Integer.toString(opponentTotalScore));
    }

    private void handOverDice(int announcement) {
        roundScore = 0;
        tvRoundScore.setText("0");

        if (announcement == SWAP_TURNS) {
            tvAnnouncement.setText("Handing over dice");
        } else if (announcement == LOST_POINTS_FOR_ROUND) {
            tvAnnouncement.setText(String.format("%s lost points for this round. Handing over dice", currentPlayerName));
        } else if (announcement == LOST_ALL_POINTS) {
            tvAnnouncement.setText(String.format("%s lost all points. Handing over dice", currentPlayerName));
        }

        if (currentPlayer == PLAYER_ONE) {
            if (getArguments().getInt(TAG_VERSUS) == VS_COMPUTER) {
                currentPlayer = COMPUTER;
                currentPlayerName = "Computer";
            } else {
                currentPlayer = PLAYER_TWO;
                currentPlayerName = "Player 2";
            }
        } else {
            currentPlayer = PLAYER_ONE;
            currentPlayerName = "Player 1";
            // Release sensor and buttons to user after computer turn ends
            shakeSensor.enable();
            bHandOverDice.setEnabled(true);
        }


        tvCurrentPlayer.setText(String.format("%s has the dice", currentPlayerName));

        if (currentPlayer == COMPUTER) {
            doComputerTurn();
        }
    }

    private void doComputerTurn() {
        // Prevent user from interrupting while computer is rolling
        shakeSensor.disable();
        bHandOverDice.setEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (roundScore > 20) {
                    handOverDice(SWAP_TURNS);
                } else {
                    shakeSensor.doShake();
                }
            }
        }, 1000);
    }

    private void endGame(int winner) {
        Game game = new Game();

        switch (getArguments().getInt(TAG_VERSUS)) {
            case VS_PLAYER:
                game.setOpponent("Player 2");
                break;
            case VS_COMPUTER:
                game.setOpponent("Computer");
                break;
        }

        switch (winner) {
            case PLAYER_ONE:
                game.setWinner("Player 1");
                break;
            case PLAYER_TWO:
                game.setWinner("Player 2");
                break;
            case COMPUTER:
                game.setWinner("Computer");
        }

        game.setPlayerScore(playerOneTotalScore);
        game.setOpponentScore(opponentTotalScore);

        new DatabaseHandler(getActivity()).saveGame(game);
        showEndOfGameDialog(game.getWinner());
    }

    private void showEndOfGameDialog(String winner) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle("Game finished!");
        dialog.setMessage(winner + " won! Play again?");

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetGame();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //return to home
            }
        });

        dialog.create();
        dialog.show();
    }

    private void resetGame() {
        playerOneTotalScore = 0;
        opponentTotalScore = 0;
        roundScore = 0;
        tvPlayerOneScore.setText("0");
        tvOpponentScore.setText("0");

        if (currentPlayer == COMPUTER) {
            handOverDice(SWAP_TURNS);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == bHandOverDice.getId()) {
            handOverDice(SWAP_TURNS);
        }
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
        shakeSensor.unregister();
        super.onPause();
    }
}
