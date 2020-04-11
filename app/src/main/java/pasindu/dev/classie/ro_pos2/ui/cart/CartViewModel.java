package pasindu.dev.classie.ro_pos2.ui.cart;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Database.CartDataSource;
import pasindu.dev.classie.ro_pos2.Database.CartDatabase;
import pasindu.dev.classie.ro_pos2.Database.CartItem;
import pasindu.dev.classie.ro_pos2.Database.LocalCartDataSource;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable;
    private CartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataCartItems;

    public CartViewModel() {
        compositeDisposable = new CompositeDisposable();
    }

    public void initCartDataSource(Context context) {
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public MutableLiveData<List<CartItem>> getMutableLiveDataCartItems() {
        if(mutableLiveDataCartItems == null)
            mutableLiveDataCartItems = new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataCartItems;
    }

    private void getAllCartItems() {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUserId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(cartItems -> {
            mutableLiveDataCartItems.setValue(cartItems);
        }, throwable -> {
            mutableLiveDataCartItems.setValue(null);
        }));
    }
}