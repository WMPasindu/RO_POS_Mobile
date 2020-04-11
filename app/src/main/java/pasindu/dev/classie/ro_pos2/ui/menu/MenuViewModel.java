package pasindu.dev.classie.ro_pos2.ui.menu;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pasindu.dev.classie.ro_pos2.Callbacks.ICategoryCallbackListener;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.CategoryModel;

public class MenuViewModel extends ViewModel implements ICategoryCallbackListener {

    private MutableLiveData<List<CategoryModel>> categotyListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private ICategoryCallbackListener categoryCallbackListener;

    public MenuViewModel() {
        categoryCallbackListener = this;
    }

    public MutableLiveData<List<CategoryModel>> getCategotyListMutable() {
        if (categotyListMutable == null) {
            categotyListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadCategories();
        }
        return categotyListMutable;
    }

    private void loadCategories() {
        List<CategoryModel> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                    CategoryModel categoryModel = itemSnapShot.getValue(CategoryModel.class);
                    categoryModel.setMenu_id(itemSnapShot.getKey());
                    tempList.add(categoryModel);
                }
                categoryCallbackListener.onCategoryLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                categoryCallbackListener.onCategoryLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onCategoryLoadSuccess(List<CategoryModel> categoryModelsList) {
        categotyListMutable.setValue(categoryModelsList);
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        messageError.setValue(message);
    }
}