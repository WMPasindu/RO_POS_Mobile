package pasindu.dev.classie.ro_pos2.ui.fooddetail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.CommentsModel;
import pasindu.dev.classie.ro_pos2.Model.FoodModel;

public class FoodDetailViewModel extends ViewModel {

    private MutableLiveData<FoodModel> mutableLiveDataFood;
    private MutableLiveData<CommentsModel> modelMutableLiveDataComment;

    public void setCommentModel (CommentsModel commentModel) {
        if(modelMutableLiveDataComment != null)
            modelMutableLiveDataComment.setValue(commentModel);
    }

    public MutableLiveData<CommentsModel> getModelMutableLiveDataComment() {
        return modelMutableLiveDataComment;
    }

    public FoodDetailViewModel() {
        modelMutableLiveDataComment = new MutableLiveData<>();
    }

    public MutableLiveData<FoodModel> getMutableLiveDataFood() {
        if (mutableLiveDataFood == null)
            mutableLiveDataFood = new MutableLiveData<>();
        mutableLiveDataFood.setValue(Common.selectFood);
        return mutableLiveDataFood;
    }

    public void setFoodModal(FoodModel foodModel) {
        if(mutableLiveDataFood != null)
            mutableLiveDataFood.setValue(foodModel);
    }
}