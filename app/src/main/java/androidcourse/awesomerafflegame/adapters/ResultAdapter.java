package androidcourse.awesomerafflegame.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidcourse.awesomerafflegame.R;
import androidcourse.awesomerafflegame.models.Results;

/**
 * Created by Jesper on 05/04/16.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private Context context;
    private List<Results> resultsList;

    public ResultAdapter(Context context, List<Results> results) {
        this.context = context;
        this.resultsList = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(context).inflate(R.layout.row_result, parent, false);
        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Results result = resultsList.get(position);
        holder.txtResult.setText(String.valueOf(result.getScore()));
        holder.txtUser.setText(result.getUser());
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtResult;
        TextView txtUser;

        public ViewHolder(View itemView) {
            super(itemView);
            txtResult = (TextView) itemView.findViewById(R.id.txtViewResults);
            txtUser = (TextView) itemView.findViewById(R.id.txtUserResults);
        }
    }
}
