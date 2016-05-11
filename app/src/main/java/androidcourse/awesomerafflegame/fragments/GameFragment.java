package androidcourse.awesomerafflegame.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.bluetooth.BluetoothHandler;
import androidcourse.awesomerafflegame.bluetooth.listeners.OnBluetoothMessageReceivedListener;
import androidcourse.awesomerafflegame.controllers.FragmentController;
import androidcourse.awesomerafflegame.persistence.SharedPreferencesManager;
import androidcourse.awesomerafflegame.domain.Game;
import androidcourse.awesomerafflegame.persistence.DatabaseHandler;
import androidcourse.awesomerafflegame.sensors.OnShakeListener;
import androidcourse.awesomerafflegame.sensors.ShakeSensor;

/**
 * Created by Mads on 01/05/16.
 */
public class GameFragment extends Fragment implements View.OnClickListener, OnShakeListener, OnBluetoothMessageReceivedListener {

    private final int PLAYER_ONE = 1;
    private final int PLAYER_TWO = 2;
    private final int COMPUTER = 3;

    private int SWAP_TURNS = 10;
    private int LOST_ALL_POINTS = 11;
    private int LOST_POINTS_FOR_ROUND = 12;

    public static final int VS_PLAYER = 20;
    public static final int VS_COMPUTER = 21;

    private static final String TAG_VERSUS = "versus";

    private final String TAG_SCORE = "score";
    private final String TAG_SWAP = "swap";
    private final String TAG_RESET = "reset";
    private final String TAG_WHO_STARTS = "who_starts";

    private ShakeSensor shakeSensor;
    private BluetoothHandler bluetoothHandler;

    private AnimationDrawable dieOneAnimation, dieTwoAnimation;

    private TextView tvOpponentName;

    private TextView tvCurrentPlayer;
    private TextView tvPlayerOneScore;
    private TextView tvOpponentScore;
    private TextView tvRoundScore;
    private TextView tvAnnouncement;

    private Button bHandOverDice;

    private AlertDialog endOfGameDialog;

    private int currentPlayer;
    private String playerName;
    private String opponentName;
    private String currentPlayerName;

    private boolean vsPlayer;

    private int initialRoll;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        shakeSensor = new ShakeSensor(getActivity());
        shakeSensor.setOnShakeListener(this);

        bluetoothHandler = BluetoothHandler.get();
        bluetoothHandler.setOnBluetoothMessageReceivedListener(this);

        currentPlayer = PLAYER_ONE;

        playerName = SharedPreferencesManager.get().getPlayerName();
        currentPlayerName = playerName;

        playerOneTotalScore = 0;
        roundScore = 0;

        vsPlayer = getArguments().getInt(TAG_VERSUS) == VS_PLAYER;

        initTextViews(view);

        bHandOverDice = (Button) view.findViewById(R.id.btn_hand_over_dice);
        bHandOverDice.setOnClickListener(this);

        initDiceIcons(view);

        setHasOptionsMenu(true);
        if (vsPlayer) {
            chooseWhoStarts();
        }

        return view;
    }

    private void initTextViews(View view) {
        TextView tvPlayerName = (TextView) view.findViewById(R.id.tv_player_name);
        tvPlayerName.setText(playerName);

        opponentName = vsPlayer ? "Player 2" : "Computer";
        tvOpponentName = (TextView) view.findViewById(R.id.tv_opponent_name);
        tvOpponentName.setText(opponentName);

        tvCurrentPlayer = (TextView) view.findViewById(R.id.tv_current_player);
        tvCurrentPlayer.setText(playerName + " has the dice");

        tvPlayerOneScore = (TextView) view.findViewById(R.id.tv_player_one_total_score);
        tvOpponentScore = (TextView) view.findViewById(R.id.tv_opponent_total_score);

        tvRoundScore = (TextView) view.findViewById(R.id.tv_round_score);

        tvAnnouncement = (TextView) view.findViewById(R.id.tv_announcement);
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

    private void chooseWhoStarts() {
        int face = new Random().nextInt(6) + 1;
        initialRoll = face;
        bluetoothHandler.sendMessage(TAG_WHO_STARTS + "," + playerName + "," + face);
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
            updateOpponentScore(actualFaceOne, actualFaceTwo);
        }
    }

    private void updatePlayerOneScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            playerOneTotalScore = 0;

            if (vsPlayer) {
                // Playing via Bluetooth - send result to opponent
                toggleControls(false);
                currentPlayerName = playerName;
                bluetoothHandler.sendMessage(TAG_SWAP + "," + LOST_ALL_POINTS);
            }

            handOverDice(LOST_ALL_POINTS);
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                playerOneTotalScore -= roundScore;
            }

            if (vsPlayer) {
                // Playing via Bluetooth - send result to opponent
                toggleControls(false);
                currentPlayerName = playerName;
                bluetoothHandler.sendMessage(TAG_SWAP + "," + LOST_POINTS_FOR_ROUND + "," + roundScore);
            }

            handOverDice(LOST_POINTS_FOR_ROUND);
        } else {
            roundScore += (faceOne + faceTwo);
            playerOneTotalScore += (faceOne + faceTwo);

            if (vsPlayer) {
                // Playing via Bluetooth - send result to opponent
                bluetoothHandler.sendMessage(TAG_SCORE + "," + faceOne + "," + faceTwo);
            }

            if (playerOneTotalScore >= 100) {
                playerOneTotalScore = 100;
                tvAnnouncement.setText("You won!");
                endGame(PLAYER_ONE);
            }
        }

        tvRoundScore.setText(Integer.toString(roundScore));
        tvPlayerOneScore.setText(Integer.toString(playerOneTotalScore));
    }

    private void updateOpponentScore(int faceOne, int faceTwo) {
        if (faceOne == 1 && faceTwo == 1) {
            opponentTotalScore = 0;

            if (vsPlayer) currentPlayerName = opponentName;
            handOverDice(LOST_ALL_POINTS);
        } else if (faceOne == 1 ^ faceTwo == 1) {
            if (!(roundScore < 0)) {
                opponentTotalScore -= roundScore;
            }

            if (vsPlayer) currentPlayerName = opponentName;
            handOverDice(LOST_POINTS_FOR_ROUND);
        } else {
            roundScore += (faceOne + faceTwo);
            opponentTotalScore += (faceOne + faceTwo);
            if (opponentTotalScore >= 100) {
                opponentTotalScore = 100;

                if (getArguments().getInt(TAG_VERSUS) == VS_PLAYER) {
                    tvAnnouncement.setText(opponentName + " won!");
                    endGame(PLAYER_TWO);
                } else {
                    tvAnnouncement.setText("Computer won!");
                    endGame(COMPUTER);
                }
            } else {
                if (getArguments().getInt(TAG_VERSUS) == VS_COMPUTER) {
                    doComputerTurn();
                }
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
            togglePlayerNames();
        } else if (announcement == LOST_ALL_POINTS) {
            tvAnnouncement.setText(String.format("%s lost all points. Handing over dice", currentPlayerName));
            togglePlayerNames();
        }

        if (getArguments().getInt(TAG_VERSUS) == VS_COMPUTER) {
            if (currentPlayer == PLAYER_ONE) {
                currentPlayer = COMPUTER;
                currentPlayerName = "Computer";
            } else {
                currentPlayer = PLAYER_ONE;
                currentPlayerName = playerName;
                // Release sensor and buttons to user after computer turn ends
                toggleControls(true);
            }
        }

        tvCurrentPlayer.setText(String.format("%s has the dice", currentPlayerName));

        if (currentPlayer == COMPUTER) {
            doComputerTurn();
        }

    }

    private void togglePlayerNames() {
        if (vsPlayer) {
            if (currentPlayerName.equals(opponentName)) {
                currentPlayerName = playerName;
            } else {
                currentPlayerName = opponentName;
            }
        }
    }


    private void toggleControls(boolean enable) {
        if (enable) {
            vibrateDevice(500);
            shakeSensor.enable();
            bHandOverDice.setEnabled(true);
        } else {
            shakeSensor.disable();
            bHandOverDice.setEnabled(false);
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
                game.setOpponent(opponentName);
                break;
            case VS_COMPUTER:
                game.setOpponent("Computer");
                break;
        }

        switch (winner) {
            case PLAYER_ONE:
                game.setWinner(playerName);
                break;
            case PLAYER_TWO:
                game.setWinner(opponentName);
                break;
            case COMPUTER:
                game.setWinner("Computer");
                break;
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
                if (vsPlayer) {
                    bluetoothHandler.sendMessage(TAG_RESET);
                }
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentController.get().returnToHome(getActivity());
            }
        });

        endOfGameDialog = dialog.create();
        dialog.show();
    }

    private void resetGame() {
        playerOneTotalScore = 0;
        opponentTotalScore = 0;
        roundScore = 0;
        tvPlayerOneScore.setText(playerOneTotalScore);
        tvOpponentScore.setText(opponentTotalScore);
        tvRoundScore.setText(roundScore);

        if (currentPlayer == COMPUTER) {
            handOverDice(SWAP_TURNS);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == bHandOverDice.getId()) {
            toggleControls(false);
            if (vsPlayer) {
                togglePlayerNames();
                bluetoothHandler.sendMessage(TAG_SWAP + "," + "HAND_OVER");
            }
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
    public void onMessageReceived(String message) {
        if (message.split(",")[0].equals(TAG_WHO_STARTS)) {
            Log.d("MESSAGE", message);
            opponentName = message.split(",")[1];

            // Update opponent TextView while we have the name
            tvOpponentName.setText(opponentName);

            if (Integer.parseInt(message.split(",")[2]) > initialRoll) {

                //Their initial roll was bigger, hand over the dice to them
                bHandOverDice.performClick();
            }
        } else if (message.split(",")[0].equals(TAG_SCORE) && message.split(",").length > 1) {

            //Message contains a SCORE - update opponents score
            updateOpponentScore(Integer.parseInt(message.split(",")[1]), Integer.parseInt(message.split(",")[2]));
        } else if (message.split(",")[0].equals(TAG_SWAP)) {

            //Message contains a SWAP request, and additional information - update accordingly
            if (message.split(",")[1].equals(String.valueOf(LOST_ALL_POINTS))) {
                opponentTotalScore = 0;
                currentPlayerName = opponentName;
                handOverDice(LOST_ALL_POINTS);
            } else if (message.split(",")[1].equals(String.valueOf(LOST_POINTS_FOR_ROUND))) {
                opponentTotalScore -= Integer.parseInt(message.split(",")[2]);
                currentPlayerName = opponentName;
                handOverDice(LOST_POINTS_FOR_ROUND);
            } else {
                currentPlayerName = playerName;
                handOverDice(SWAP_TURNS);
            }
            tvOpponentScore.setText(String.valueOf(opponentTotalScore));
            toggleControls(true);
        } else if (message.equals(TAG_RESET)) {
            resetGame();
            chooseWhoStarts();

            endOfGameDialog.dismiss();
        }
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.rules_menu) {
            showRulesDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Rules");
        builder.setMessage(R.string.rules);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
