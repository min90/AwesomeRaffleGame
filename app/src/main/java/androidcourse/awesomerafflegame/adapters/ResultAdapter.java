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
    private List<Results> results;
    private LayoutInflater inflater;

    public ResultAdapter(Context context, List<Results> results) {
        this.context = context;
        this.results = results;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = inflater.inflate(R.layout.row_results, parent);
        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.txtViewResults);
        }
    }
}
