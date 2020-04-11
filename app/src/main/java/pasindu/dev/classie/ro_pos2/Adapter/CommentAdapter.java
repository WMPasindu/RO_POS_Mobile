package pasindu.dev.classie.ro_pos2.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pasindu.dev.classie.ro_pos2.Model.CommentsModel;
import pasindu.dev.classie.ro_pos2.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    Context context;
    List<CommentsModel> commentsModels;

    public CommentAdapter(Context context, List<CommentsModel> commentsModels) {
        this.context = context;
        this.commentsModels = commentsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Long timestamp = Long.valueOf(commentsModels.get(position).getCommentsTimeStamp().get("timeStamp").toString());
        holder.txt_comment_date.setText(DateUtils.getRelativeTimeSpanString(timestamp));
        holder.txt_comment.setText(commentsModels.get(position).getComments());
        holder.txt_comment_name.setText(commentsModels.get(position).getName());
        holder.rating_bar.setRating(commentsModels.get(position).getRatingValue());
    }

    @Override
    public int getItemCount() {
        return commentsModels.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        private Unbinder unbinder;

        @BindView(R.id.txt_comment_date)
        TextView txt_comment_date;
        @BindView(R.id.txt_comment_name)
        TextView txt_comment_name;
        @BindView(R.id.txt_comment)
        TextView txt_comment;
        @BindView(R.id.comment_rating_bar)
        RatingBar rating_bar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
