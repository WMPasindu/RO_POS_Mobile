package pasindu.dev.classie.ro_pos2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.annotations.NonNull;
import pasindu.dev.classie.ro_pos2.Database.CartItem;
import pasindu.dev.classie.ro_pos2.EventBus.UpdateItemInCart;
import pasindu.dev.classie.ro_pos2.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getFoodImage()).into(holder.img_cart);
        holder.txt_food_name.setText(new StringBuilder(cartItemList.get(position).getFoodName()));
        holder.txt_food_price.setText(new StringBuilder("")
                .append(cartItemList.get(position).getFoodPrice() + cartItemList.get(position).getFoodExtraPrice()));

        holder.number_button.setNumber(String.valueOf(cartItemList.get(position).getFoodQuantity()));

//        Event
        holder.number_button.setOnValueChangeListener((view, oldValue, newValue) -> {
//            when user click this button it will update the database
            cartItemList.get(position).setFoodQuantity(newValue);
            EventBus.getDefault().postSticky(new UpdateItemInCart(cartItemList.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        @BindView(R.id.img_cart)
        ImageView img_cart;
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @BindView(R.id.number_button)
        ElegantNumberButton number_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
