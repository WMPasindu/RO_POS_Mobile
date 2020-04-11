package pasindu.dev.classie.ro_pos2.ui.menu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import pasindu.dev.classie.ro_pos2.Adapter.CategoriesAdapter;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Common.SpaceItemDecoaration;
import pasindu.dev.classie.ro_pos2.R;

public class MenuFragment extends Fragment {

    private MenuViewModel menuViewModel;
    Unbinder unbinder;

    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    CategoriesAdapter categoriesAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        menuViewModel =
                ViewModelProviders.of(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        unbinder = ButterKnife.bind(this, root);
        initViews();

        menuViewModel.getMessageError().observe(this, s -> {
            Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        menuViewModel.getCategotyListMutable().observe(this, categoryModels -> {
            dialog.dismiss();
            categoriesAdapter = new CategoriesAdapter(getContext(), categoryModels);
            recycler_menu.setAdapter(categoriesAdapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initViews() {
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoriesAdapter != null) {
                    switch (categoriesAdapter.getItemViewType(position)) {
                        case Common
                                .DEFAULT_COLUMN_COUNT:
                            return 1;
                        case Common
                                .FULL_WIDTH_COLUMN:
                            return 2;
                        default:
                            return -1;
                    }
                }
                return -1;
            }
        });
        recycler_menu.setLayoutManager(gridLayoutManager);
        recycler_menu.addItemDecoration(new SpaceItemDecoaration(8));
    }
}