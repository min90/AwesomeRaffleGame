package androidcourse.awesomerafflegame.models;

/**
 * Created by Jesper on 05/04/16.
 */
public class Results {

    private int score;
    private Users user;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Results{" +
                "score=" + score +
                ", user=" + user +
                '}';
    }
}
