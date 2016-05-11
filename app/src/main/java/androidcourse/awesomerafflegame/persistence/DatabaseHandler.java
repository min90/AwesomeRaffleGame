package androidcourse.awesomerafflegame.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import androidcourse.awesomerafflegame.domain.Game;

/**
 * Created by Mads on 05/04/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "raffle_game_db";

    private final String TABLE_GAMES = "games";

    private final String KEY_ID = "id";
    private final String KEY_OPPONENT = "opponent";
    private final String KEY_WINNER = "winnner";
    private final String KEY_PLAYER_SCORE = "player_score";
    private final String KEY_OPPONENT_SCORE = "opponent_score";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GAMES_TABLE = "CREATE TABLE " + TABLE_GAMES
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_OPPONENT + " TEXT,"
                + KEY_WINNER + " TEXT,"
                + KEY_PLAYER_SCORE + " INTEGER,"
                + KEY_OPPONENT_SCORE + " INTEGER"
                + ")";
        db.execSQL(CREATE_GAMES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle alterations in database here
    }

    public void saveGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OPPONENT, game.getOpponent());
        values.put(KEY_WINNER, game.getWinner());
        values.put(KEY_PLAYER_SCORE, game.getPlayerScore());
        values.put(KEY_OPPONENT_SCORE, game.getOpponentScore());

        db.insert(TABLE_GAMES, null, values);
    }

    public void deleteGame(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GAMES, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Game getGame(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        // SELECT FROM TABLE GAMES id, opponent, winner, player_score, opponent_score WHERE id = :id
        Cursor cursor = db.query(TABLE_GAMES, new String[]{
                        KEY_ID,
                        KEY_OPPONENT,
                        KEY_WINNER,
                        KEY_PLAYER_SCORE,
                        KEY_OPPONENT_SCORE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            throw new Error("Game with id '" + id + "' not found");
        }

        String opponent = cursor.getString(1);
        String winner = cursor.getString(2);
        int playerScore = cursor.getInt(3);
        int opponentScore = cursor.getInt(4);

        Game game = new Game(id, opponent, winner, playerScore, opponentScore);

        cursor.close();
        return game;
    }

    public List<Game> getAllGames() {
        SQLiteDatabase db = this.getWritableDatabase();

        List<Game> games = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_GAMES;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String opponent = cursor.getString(1);
                String winner = cursor.getString(2);
                int playerScore = cursor.getInt(3);
                int opponentScore = cursor.getInt(4);

                Game game = new Game(id, opponent, winner, playerScore, opponentScore);
                games.add(game);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return games;
    }


}
