package pasindu.dev.classie.ro_pos2.ui.fooddetail;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Database.CartDataSource;
import pasindu.dev.classie.ro_pos2.Database.CartDatabase;
import pasindu.dev.classie.ro_pos2.Database.CartItem;
import pasindu.dev.classie.ro_pos2.Database.LocalCartDataSource;
import pasindu.dev.classie.ro_pos2.EventBus.CounterCartEvent;
import pasindu.dev.classie.ro_pos2.Model.AddonModel;
import pasindu.dev.classie.ro_pos2.Model.CommentsModel;
import pasindu.dev.classie.ro_pos2.Model.FoodModel;
import pasindu.dev.classie.ro_pos2.Model.SizeModel;
import pasindu.dev.classie.ro_pos2.R;
import pasindu.dev.classie.ro_pos2.ui.comments.CommentFragment;

public class FoodDetailFragment extends Fragment implements TextWatcher {

    private FoodDetailViewModel foodDetailViewModel;
    private Unbinder unbinder;
    private android.app.AlertDialog writingDialog;
    private BottomSheetDialog addonButtomSheetDialog;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    //    view need inflate
    ChipGroup chip_group_addon;
    EditText edt_search;

    @BindView(R.id.img_food)
    ImageView img_food;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.btn_rating)
    FloatingActionButton btn_rating;
    @BindView(R.id.food_name)
    TextView food_name;
    @BindView(R.id.food_price)
    TextView food_price;
    @BindView(R.id.food_description)
    TextView food_description;
    @BindView(R.id.number_button)
    ElegantNumberButton number_button;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.btnShowComment)
    Button btnShowComment;
    @BindView(R.id.rdi_group_size)
    RadioGroup rdi_group_size;
    @BindView(R.id.img_add_addon)
    ImageView img_add_addon;
    @BindView(R.id.chip_group_user_selected_addon)
    ChipGroup chip_group_user_selected_addon;

    @OnClick(R.id.img_add_addon)
    void onAddonClick() {
        if (Common.selectFood.getAddon() != null) {
            displayAddonList();
            addonButtomSheetDialog.show();
        }
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd() {
        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUserId());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setFoodId(Common.selectFood.getId());
        cartItem.setFoodName(Common.selectFood.getName());
        cartItem.setFoodImage(Common.selectFood.getImage());

        cartItem.setFoodPrice(Double.valueOf(String.valueOf(Common.selectFood.getPrice())));
        cartItem.setFoodQuantity(Integer.valueOf(number_button.getNumber()));
        cartItem.setFoodExtraPrice(Common.calculateExtraPrice(Common.selectFood.getUserSelectedSize(), Common.selectFood.getUserSelectedAddon()));
        if (Common.selectFood.getUserSelectedAddon() != null)
            cartItem.setFoodAddon(new Gson().toJson(Common.selectFood.getUserSelectedAddon()));
        else
            cartItem.setFoodAddon("Default");
        if (Common.selectFood.getUserSelectedSize() != null)
            cartItem.setFoodSize(new Gson().toJson(Common.selectFood.getUserSelectedSize()));
        else
            cartItem.setFoodSize("Default");

        cartDataSource.getItemWithAllOptionsInCart(Common.currentUser.getUserId(),
                cartItem.getFoodId(),
                cartItem.getFoodSize(),
                cartItem.getFoodAddon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<CartItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(CartItem cartItemFromDB) {
                        if (cartItemFromDB.equals(cartItem)) {
                            cartItemFromDB.setFoodExtraPrice(cartItem.getFoodExtraPrice());
                            cartItemFromDB.setFoodAddon(cartItem.getFoodAddon());
                            cartItemFromDB.setFoodSize(cartItem.getFoodSize());
                            cartItemFromDB.setFoodQuantity(cartItemFromDB.getFoodQuantity() + cartItem.getFoodQuantity());

                            cartDataSource.updateCartItems(cartItemFromDB)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            Toast.makeText(getContext(), "Update Cart success", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e.getMessage().contains("empty")) {
                            compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        Toast.makeText(getContext(), "Add to Cart success", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[CART ERROR]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                        } else
                            Toast.makeText(getContext(), "[GET CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayAddonList() {
        if (Common.selectFood.getAddon().size() > 0) {
            chip_group_addon.clearCheck();
            chip_group_addon.removeAllViews();

            edt_search.addTextChangedListener(this);

//            add all view
            for (AddonModel addonModel : Common.selectFood.getAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+Rs.")
                        .append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        if (Common.selectFood.getUserSelectedAddon() == null)
                            Common.selectFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }
        }
    }

    @OnClick(R.id.btn_rating)
    void onRatingButtonClick() {
        showDialogRating();
    }

    @OnClick(R.id.btnShowComment)
    void onShowCommentButtonClick() {
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(), "CommentFragment");
    }

    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rating Food");
        builder.setMessage("Please add your rating for this food");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating, null);
        RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText comments = (EditText) itemView.findViewById(R.id.edit_comment);

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {

            CommentsModel commentsModel = new CommentsModel();
            commentsModel.setUid(Common.currentUser.getUserId());
            commentsModel.setName(Common.currentUser.getUserName());
            commentsModel.setComments(comments.getText().toString());
            commentsModel.setRatingValue(ratingBar.getRating());
            Map<String, Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
            commentsModel.setCommentsTimeStamp(serverTimeStamp);

            foodDetailViewModel.setCommentModel(commentsModel);

        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        foodDetailViewModel =
                ViewModelProviders.of(this).get(FoodDetailViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_detail, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        foodDetailViewModel.getMutableLiveDataFood().observe(this, foodModel -> {
            displayInfo(foodModel);
        });
        foodDetailViewModel.getModelMutableLiveDataComment().observe(this, commnentsModel -> {
            submitRatingToFirebase(commnentsModel);
        });
        return root;
    }

    private void initViews() {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        writingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        addonButtomSheetDialog = new BottomSheetDialog(getContext(), R.style.DialogStyle);
        View layout_addon_display = getLayoutInflater().inflate(R.layout.layout_addon_display, null);
        chip_group_addon = (ChipGroup) layout_addon_display.findViewById(R.id.chip_group_addon);
        edt_search = (EditText) layout_addon_display.findViewById(R.id.edit_search);
        addonButtomSheetDialog.setContentView(layout_addon_display);

        addonButtomSheetDialog.setOnDismissListener(dialogInterface -> {
            displayUserSelectedAddon();
            calculateTotalPrice();
        });

    }

    private void displayUserSelectedAddon() {
        if (Common.selectFood.getUserSelectedAddon() != null &&
                Common.selectFood.getUserSelectedAddon().size() > 0) {
            chip_group_user_selected_addon.removeAllViews();
            for (AddonModel addonModel : Common.selectFood.getUserSelectedAddon()) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_chip_with_delete_icon, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+Rs.").append(addonModel.getPrice()).append(")"));
                chip.setCheckable(false);
                chip.setOnCloseIconClickListener(view -> {
                    chip_group_user_selected_addon.removeView(view);
                    Common.selectFood.getUserSelectedAddon().remove(addonModel);
                    calculateTotalPrice();
                });
                chip_group_user_selected_addon.addView(chip);
            }
        } else if (Common.selectFood.getUserSelectedAddon().size() == 0) {
            chip_group_user_selected_addon.removeAllViews();
        }
    }

    private void submitRatingToFirebase(CommentsModel commentsModel) {
        writingDialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectFood.getId())
                .push()
                .setValue(commentsModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addRatingFood(commentsModel.getRatingValue());
                        }
                        writingDialog.dismiss();
                    }
                });
    }

    private void addRatingFood(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .child("foods")
                .child(Common.selectFood.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FoodModel foodModel = dataSnapshot.getValue(FoodModel.class);
                            foodModel.setKey(Common.selectFood.getKey());

                            if (foodModel.getRatingValue() == null)
                                foodModel.setRatingValue(0d);
                            if (foodModel.getRatingCount() == null)
                                foodModel.setRatingCount(0L);
                            double sumRating = foodModel.getRatingValue() + ratingValue;
                            long ratingCount = foodModel.getRatingCount() + 1;

                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("ratingValue", sumRating);
                            updateData.put("ratingCount", ratingCount);

                            foodModel.setRatingValue(sumRating);
                            foodModel.setRatingCount(ratingCount);

                            dataSnapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task -> {
                                        writingDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), "Thank you ! ", Toast.LENGTH_SHORT).show();
                                            Common.selectFood = foodModel;
                                            foodDetailViewModel.setFoodModal(foodModel);
                                        }
                                    });

                        } else
                            writingDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        writingDialog.dismiss();
                        Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void displayInfo(FoodModel foodModel) {
        Glide.with(getContext()).load(foodModel.getImage()).into(img_food);
        food_name.setText(new StringBuilder(foodModel.getName()));
        food_price.setText(new StringBuilder(foodModel.getPrice().toString()));
        food_description.setText(new StringBuilder(foodModel.getDescription()));

        if (foodModel.getRatingValue() != null)
            ratingBar.setRating(foodModel.getRatingValue().floatValue() / foodModel.getRatingCount());

        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(Common.selectFood.getName());

        for (SizeModel sizeModel : Common.selectFood.getSize()) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b)
                    Common.selectFood.setUserSelectedSize(sizeModel);
                calculateTotalPrice();
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f);
            radioButton.setLayoutParams(params);
            radioButton.setText(sizeModel.getName());
            radioButton.setTag(sizeModel.getPrice());

            Log.d("getName : ", sizeModel.getName());
            Log.d("getPrice : ", String.valueOf(sizeModel.getPrice()));

            rdi_group_size.addView(radioButton);
        }

        if (rdi_group_size.getChildCount() > 0) {
            Log.d("rdi_group_size : ", String.valueOf(rdi_group_size.getChildCount()));
            RadioButton radioButton = (RadioButton) rdi_group_size.getChildAt(0);
            radioButton.setChecked(true);
        }
    }

    private void calculateTotalPrice() {
        double totalPrice = Double.parseDouble(Common.selectFood.getPrice().toString()), displayPrice = 0.0;

//        Addon
        if (Common.selectFood.getUserSelectedAddon() != null && Common.selectFood.getUserSelectedAddon().size() > 0)
            for (AddonModel addonModel : Common.selectFood.getUserSelectedAddon())
                totalPrice += Double.parseDouble(addonModel.getPrice().toString());

//        size
        if (Common.selectFood.getUserSelectedSize() != null)
            totalPrice += Double.parseDouble(Common.selectFood.getUserSelectedSize().getPrice().toString());

        displayPrice = totalPrice * (Integer.parseInt(number_button.getNumber()));
        displayPrice = Math.round(displayPrice * 100.0 / 100.0);

        food_price.setText(new StringBuilder("").append(Common.formatPrice(displayPrice)).toString());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        chip_group_addon.clearCheck();
        chip_group_addon.removeAllViews();

        for (AddonModel addonModel : Common.selectFood.getAddon()) {
            if (addonModel.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.layout_addon_item, null);
                chip.setText(new StringBuilder(addonModel.getName()).append("(+Rs.").append(addonModel.getPrice()).append(")"));
                chip.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        if (Common.selectFood.getUserSelectedAddon() == null)
                            Common.selectFood.setUserSelectedAddon(new ArrayList<>());
                        Common.selectFood.getUserSelectedAddon().add(addonModel);
                    }
                });
                chip_group_addon.addView(chip);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
//      nothing
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}