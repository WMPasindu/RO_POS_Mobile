package pasindu.dev.classie.ro_pos2.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Model.CommentsModel;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentsModel>> mutableLiveDataFoodList;

    public CommentViewModel() {
        mutableLiveDataFoodList = new MutableLiveData();
    }

    public MutableLiveData<List<CommentsModel>> getMutableLiveDataFoodList() {
        return mutableLiveDataFoodList;
    }

    public void setCommentList(List<CommentsModel> commnentList) {
        mutableLiveDataFoodList.setValue(commnentList);
    }
}
