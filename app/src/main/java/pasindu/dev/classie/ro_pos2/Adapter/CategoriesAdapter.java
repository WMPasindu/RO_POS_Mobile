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
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.EventBus.CategoryClick;
import pasindu.dev.classie.ro_pos2.Model.CategoryModel;
import pasindu.dev.classie.ro_pos2.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    Context context;
    List<CategoryModel> categoryModelList;

    public CategoriesAdapter(Context context, List<CategoryModel> categoryModelList) {
        this.context = context;
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.ViewHolder holder, int position) {
        Glide.with(context).load(categoryModelList.get(position).getImage()).into(holder.img_category);
        holder.txt_category_name.setText(new StringBuilder(categoryModelList.get(position).getName()));

//        event (click event on item)
        holder.setListener((view, pos) -> {
            Common.categorySelected = categoryModelList.get(pos);
            EventBus.getDefault().postSticky(new CategoryClick(true, categoryModelList.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @BindView(R.id.img_category)
        ImageView img_category;
        @BindView(R.id.txt_category)
        TextView txt_category_name;

        IRecyclerClickListener listener;

        public IRecyclerClickListener getListener() {
            return listener;
        }

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

    @Override
    public int getItemViewType(int position) {
        if (categoryModelList.size() == 1)
            return Common.DEFAULT_COLUMN_COUNT;
        else {
            if (categoryModelList.size() % 2 == 0)
                return Common.DEFAULT_COLUMN_COUNT;
            else
                return (position > 1 && position == categoryModelList.size()-1) ? Common.FULL_WIDTH_COLUMN:Common.DEFAULT_COLUMN_COUNT;
        }
    }
}
