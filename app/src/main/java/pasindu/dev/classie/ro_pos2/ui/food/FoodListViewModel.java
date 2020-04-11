package pasindu.dev.classie.ro_pos2.ui.food;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.FoodModel;

public class FoodListViewModel extends ViewModel {

    private MutableLiveData<List<FoodModel>> mutableLiveDataFoodList;

    public FoodListViewModel() {
    }

    public MutableLiveData<List<FoodModel>> getMutableLiveDataFoodList() {
        if(mutableLiveDataFoodList == null)
            mutableLiveDataFoodList = new MutableLiveData<>();
        mutableLiveDataFoodList.setValue(Common.categorySelected.getFoods());
        return mutableLiveDataFoodList;
    }
}