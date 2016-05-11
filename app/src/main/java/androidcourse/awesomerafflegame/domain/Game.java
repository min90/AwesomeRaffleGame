package androidcourse.awesomerafflegame.domain;

/**
 * Created by Mads on 06/05/2016.
 */
public class Game {

    private int id;
    private String opponent;
    private String winner;
    private int playerScore;
    private int opponentScore;

    public Game() {

    }

    public Game(int id, String opponent, String winner, int playerScore, int opponentScore) {
        this.id = id;
        this.opponent = opponent;
        this.winner = winner;
        this.playerScore = playerScore;
        this.opponentScore = opponentScore;
    }

    public int getId() {
        return id;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public int getOpponentScore() {
        return opponentScore;
    }

    public void setOpponentScore(int opponentScore) {
        this.opponentScore = opponentScore;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", opponent='" + opponent + '\'' +
                ", winner='" + winner + '\'' +
                ", playerScore=" + playerScore +
                ", opponentScore=" + opponentScore +
                '}';
    }
}
