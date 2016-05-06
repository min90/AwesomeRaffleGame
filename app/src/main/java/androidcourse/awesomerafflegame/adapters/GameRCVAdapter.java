package androidcourse.awesomerafflegame.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.models.Game;

/**
 * Created by Jesper on 05/04/16.
 */
public class GameRCVAdapter extends RecyclerView.Adapter<GameRCVAdapter.ViewHolder> {

    private Context context;
    private List<Game> games;

    public GameRCVAdapter(Context context, List<Game> results) {
        this.context = context;
        this.games = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.row_game, parent, false);
        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Game game = games.get(position);

        holder.tvIndex.setText(Integer.toString(position + 1));
        holder.tvVersus.setText(String.format("Player 1 vs %s", game.getOpponent()));
        if (game.getWinner().equals("Player 1")) {
            holder.tvOutcome.setText("WIN");
        } else {
            holder.tvOutcome.setText("LOSS");
        }
        holder.tvPlayerScore.setText(Integer.toString(game.getPlayerScore()));
        holder.tvOpponentScore.setText(Integer.toString(game.getOpponentScore()));
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex;
        TextView tvVersus;
        TextView tvOutcome;
        TextView tvPlayerScore;
        TextView tvOpponentScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            tvVersus = (TextView) itemView.findViewById(R.id.tv_versus);
            tvOutcome = (TextView) itemView.findViewById(R.id.tv_outcome);
            tvPlayerScore = (TextView) itemView.findViewById(R.id.tv_player_score);
            tvOpponentScore = (TextView) itemView.findViewById(R.id.tv_opponent_score);
        }
    }
}
