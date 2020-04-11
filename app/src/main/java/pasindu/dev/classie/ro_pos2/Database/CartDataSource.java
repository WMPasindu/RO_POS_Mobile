package pasindu.dev.classie.ro_pos2.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {
    Flowable<List<CartItem>> getAllCart(String uid);
    Single<Integer> countItemInCart(String uid);
    Single<Double> sumPriceInCart(String uid);
    Single<CartItem> getItemInCart(String foodId, String uid);
    Completable insertOrReplaceAll(CartItem... cartItems);
    Single<Integer> updateCartItems(CartItem cartItems);
    Single<Integer> deleteCartItems(CartItem cartItems);
    Single<Integer> cleanCart(String uid);
    Single<CartItem> getItemWithAllOptionsInCart(String uid, String foodId, String foodSize, String foodAddon);
}
