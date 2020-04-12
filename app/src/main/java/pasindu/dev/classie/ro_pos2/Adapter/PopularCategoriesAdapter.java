package pasindu.dev.classie.ro_pos2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pasindu.dev.classie.ro_pos2.Callbacks.IRecyclerClickListener;
import pasindu.dev.classie.ro_pos2.EventBus.PopularCategoryClick;
import pasindu.dev.classie.ro_pos2.Model.PopularCategoryModel;
import pasindu.dev.classie.ro_pos2.R;

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.ViewHolder> {
    Context context;
    List<PopularCategoryModel> popularCategoryModelList;

    public PopularCategoriesAdapter(Context context, List<PopularCategoryModel> popularCategoryModelList) {
        this.context = context;
        this.popularCategoryModelList = popularCategoryModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_popular_categories_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(popularCategoryModelList.get(position).getImage())
                .into(holder.category_image);
        holder.txt_category_name.setText(popularCategoryModelList.get(position).getName());

        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                EventBus.getDefault().postSticky(new PopularCategoryClick(popularCategoryModelList.get(pos)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularCategoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.txt_category_name)
        TextView txt_category_name;
        @BindView(R.id.category_image)
        ImageView category_image;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}
