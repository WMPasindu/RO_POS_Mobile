package pasindu.dev.classie.ro_pos2.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pasindu.dev.classie.ro_pos2.Callbacks.IBestDealsCallbackListener;
import pasindu.dev.classie.ro_pos2.Callbacks.IPopularCallbackListener;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.BestDealsModel;
import pasindu.dev.classie.ro_pos2.Model.PopularCategoryModel;

public class HomeViewModel extends ViewModel implements IPopularCallbackListener, IBestDealsCallbackListener {

    private MutableLiveData<List<PopularCategoryModel>> popularList;
    private MutableLiveData<List<BestDealsModel>> bestDealsList;
    private MutableLiveData<String> messageError;
    private IPopularCallbackListener popularCallbackListener;
    private IBestDealsCallbackListener bestDealsCallbackListener;

    public HomeViewModel() {
        popularCallbackListener = this;
        bestDealsCallbackListener = this;
    }

    public MutableLiveData<List<BestDealsModel>> getBestDealsList() {
        if(bestDealsList == null) {
            bestDealsList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadBeastDealsList();
        }
        return bestDealsList;
    }

    private void loadBeastDealsList() {
        List<BestDealsModel> tempDealsList = new ArrayList<>();
        DatabaseReference bestDealsRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF);
        bestDealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()) {
                    BestDealsModel model = itemSnapshot.getValue(BestDealsModel.class);
                    tempDealsList.add(model);
                }
                bestDealsCallbackListener.onBestDealsLoadSuccess(tempDealsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bestDealsCallbackListener.onBestDealsLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<List<PopularCategoryModel>> getPopularList() {
        if(popularList == null) {
            popularList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadPopularList();
        }
        return popularList;
    }

    private void loadPopularList() {
        List<PopularCategoryModel> tempList = new ArrayList<>();
        DatabaseReference popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_CATEGORY_REF);
        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()) {
                    PopularCategoryModel model = itemSnapshot.getValue(PopularCategoryModel.class);
                    tempList.add(model);
                }
                popularCallbackListener.onPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                popularCallbackListener.onPopularLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels) {
        popularList.setValue(popularCategoryModels);
    }

    @Override
    public void onPopularLoadFailed(String message) {
        messageError.setValue(message);
    }

    @Override
    public void onBestDealsLoadSuccess(List<BestDealsModel> bestDealsModels) {
        bestDealsList.setValue(bestDealsModels);
    }

    @Override
    public void onBestDealsLoadFailed(String message) {
        messageError.setValue(message);
    }
}
