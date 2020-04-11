package pasindu.dev.classie.ro_pos2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pasindu.dev.classie.ro_pos2.Model.BestDealsModel;
import pasindu.dev.classie.ro_pos2.R;

public class BestDealsAdapter extends LoopingPagerAdapter<BestDealsModel> {

    @BindView(R.id.img_best_deals)
    ImageView img_best_deals;
    @BindView(R.id.txt_best_deals)
    TextView txt_best_deals;

    Unbinder unbinder;

    public BestDealsAdapter(Context context, List<BestDealsModel> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.layout_best_deals_items, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        unbinder = ButterKnife.bind(this, convertView);
//        set data
        Glide.with(convertView).load(itemList.get(listPosition).getImage()).into(img_best_deals);
        txt_best_deals.setText(itemList.get(listPosition).getName());
    }
}
