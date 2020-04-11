package pasindu.dev.classie.ro_pos2.ui.food;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pasindu.dev.classie.ro_pos2.Adapter.FoodListAdapter;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.FoodModel;
import pasindu.dev.classie.ro_pos2.R;

public class FoodListFragment extends Fragment {

    private FoodListViewModel sendViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_food_list)
    RecyclerView recycler_food_list;

    LayoutAnimationController layoutAnimationController;
    FoodListAdapter foodListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(FoodListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();

        sendViewModel.getMutableLiveDataFoodList().observe(this, new Observer<List<FoodModel>>() {
            @Override
            public void onChanged(List<FoodModel> foodModels) {
                foodListAdapter = new FoodListAdapter(getContext(), foodModels);
                recycler_food_list.setAdapter(foodListAdapter);
                recycler_food_list.setLayoutAnimation(layoutAnimationController);
            }
        });
        return root;
    }

    private void initViews() {
        recycler_food_list.setHasFixedSize(true);
        recycler_food_list.setLayoutManager(new LinearLayoutManager(getContext()));

        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());
    }
}