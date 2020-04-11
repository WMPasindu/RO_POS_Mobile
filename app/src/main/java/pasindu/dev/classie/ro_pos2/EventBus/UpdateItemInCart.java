package pasindu.dev.classie.ro_pos2.EventBus;

import pasindu.dev.classie.ro_pos2.Database.CartItem;

public class UpdateItemInCart {

    private CartItem cartItem;

    public UpdateItemInCart(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}
