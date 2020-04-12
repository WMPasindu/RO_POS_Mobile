package pasindu.dev.classie.ro_pos2.ui.cart;

import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pasindu.dev.classie.ro_pos2.Adapter.CartAdapter;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Common.MySwipeHelper;
import pasindu.dev.classie.ro_pos2.Database.CartDataSource;
import pasindu.dev.classie.ro_pos2.Database.CartDatabase;
import pasindu.dev.classie.ro_pos2.Database.CartItem;
import pasindu.dev.classie.ro_pos2.Database.LocalCartDataSource;
import pasindu.dev.classie.ro_pos2.EventBus.CounterCartEvent;
import pasindu.dev.classie.ro_pos2.EventBus.HideFABCart;
import pasindu.dev.classie.ro_pos2.EventBus.UpdateItemInCart;
import pasindu.dev.classie.ro_pos2.R;

public class CartFragment extends Fragment {

    private Parcelable recyclerViewState;
    private CartViewModel cartViewModel;
    private Unbinder unbinder;
    private CartDataSource cartDataSource;
    private CartAdapter adapter;

    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;

    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("One more step!");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);

        EditText edt_address = (EditText) view.findViewById(R.id.edt_address);
        EditText edt_comments = (EditText) view.findViewById(R.id.edt_comment);
        TextView txt_address = (TextView) view.findViewById(R.id.txt_address_detail);

        RadioButton rdi_home_address = (RadioButton) view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_deliver_this_address = (RadioButton) view.findViewById(R.id.rdi_deliver_this_address);

        RadioButton rdi_cash_on_delivery = (RadioButton) view.findViewById(R.id.rdi_cod);
        RadioButton rdi_braintree = (RadioButton) view.findViewById(R.id.rdi_braintree);

//        data
        edt_address.setText(Common.currentUser.getAddress()); // default set value as user given address when he/she sign_in

        rdi_home_address.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                edt_address.setText(Common.currentUser.getAddress());
                txt_address.setVisibility(View.GONE);
            }
        });
        rdi_other_address.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                edt_address.setText(""); // clear
                edt_address.setHint("Change your address");
                txt_address.setVisibility(View.GONE);
            }
        });
        rdi_deliver_this_address.setOnCheckedChangeListener((compoundButton, b) -> {

            if (b) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "[Location Failed] " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            txt_address.setVisibility(View.GONE);
                        }).addOnCompleteListener(task -> {
                    String coordinates = new StringBuilder()
                            .append(task.getResult().getLatitude())
                            .append("/")
                            .append(task.getResult().getLongitude()).toString();

                    Single<String> singleAddress = Single.just(getAddressFromLatlan(task.getResult().getLatitude(),
                            task.getResult().getLongitude()));

                    Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                        @Override
                        public void onSuccess(String s) {
                            edt_address.setText(coordinates);
                            txt_address.setText(s);
                            txt_address.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            edt_address.setText(coordinates);
                            txt_address.setText("[Address error] " +e.getMessage());
                            txt_address.setVisibility(View.VISIBLE);
                        }
                    });
                });
            }
        });

        builder.setView(view);
        builder.setNegativeButton("NO", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }).setPositiveButton("YES", (dialogInterface, i) -> {
            Toast.makeText(getContext(), "[implement this later !!! ]", Toast.LENGTH_SHORT).show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getAddressFromLatlan(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
            if(addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0); // always getting first item
                StringBuilder stringBuilder = new StringBuilder(address.getAddressLine(0));
                result = stringBuilder.toString();
            } else {
                result = "Sorry! , Address not found";
            }
        } catch (Exception ex) {
            result = ex.getMessage();
        }
        return result;
    }


    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataCartItems().observe(this, cartItems -> {
            if (cartItems == null || cartItems.isEmpty()) {
                recycler_cart.setVisibility(View.GONE);
                group_place_holder.setVisibility(View.GONE);
                txt_empty_cart.setVisibility(View.VISIBLE);

            } else {
                recycler_cart.setVisibility(View.VISIBLE);
                group_place_holder.setVisibility(View.VISIBLE);
                txt_empty_cart.setVisibility(View.GONE);

                adapter = new CartAdapter(getContext(), cartItems);
                recycler_cart.setAdapter(adapter);
            }
        });
        unbinder = ButterKnife.bind(this, root);
        initViews();
        initLocation();
        return root;
    }

    private void initLocation() {
        buildLocationRequest();
        buildLocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);
    }

    private void initViews() {

        setHasOptionsMenu(true);

        EventBus.getDefault().postSticky(new HideFABCart(true));

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_cart.setLayoutManager(layoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_cart, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF3C30"),
                        pos -> {
                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItems(cartItem)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new SingleObserver<Integer>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {

                                        }

                                        @Override
                                        public void onSuccess(Integer integer) {
                                            adapter.notifyItemRemoved(pos);
                                            sumAllItemInCart(); // update total price
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true)); // update FAB (show)
                                            Toast.makeText(getContext(), "Item Deleted Successfully From Cart", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Toast.makeText(getContext(), "[]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }));
            }
        };

        sumAllItemInCart();
    }

    private void sumAllItemInCart() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double aDouble) {
                        txt_total_price.setText(new StringBuilder("Total: Rs.").append(aDouble));
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                            Toast.makeText(getContext(), "[SUM CART ITEMS]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false); // Hide Home menu action item already inflated
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        cartDataSource.cleanCart(Common.currentUser.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Toast.makeText(getContext(), "Successfully Clean Cart", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "[CLEAR CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().postSticky(new HideFABCart(false));
        cartViewModel.onStop();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUploadItemInCartEvent(UpdateItemInCart event) {
        if (event.getCartItem() != null) {
//            First, save state of Recycler View
            recyclerViewState = recycler_cart.getLayoutManager().onSaveInstanceState();
            cartDataSource.updateCartItems(event.getCartItem())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(Integer integer) {
                            calculateTotalPrice();
                            recycler_cart.getLayoutManager().onRestoreInstanceState(recyclerViewState); // Fix error refresh recycler view after update
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void calculateTotalPrice() {
        cartDataSource.sumPriceInCart(Common.currentUser.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Double>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Double price) {
                        txt_total_price.setText(new StringBuilder("Total : Rs. ")
                                .append(Common.formatPrice(price)));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getContext(), "[SUM CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}