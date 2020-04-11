package pasindu.dev.classie.ro_pos2.EventBus;

import pasindu.dev.classie.ro_pos2.Model.FoodModel;

public class FoodItemClick {

    private boolean sucess;
    private FoodModel foodModel;

    public FoodItemClick(boolean sucess, FoodModel foodModel) {
        this.sucess = sucess;
        this.foodModel = foodModel;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public FoodModel getFoodModel() {
        return foodModel;
    }

    public void setFoodModel(FoodModel foodModel) {
        this.foodModel = foodModel;
    }
}
