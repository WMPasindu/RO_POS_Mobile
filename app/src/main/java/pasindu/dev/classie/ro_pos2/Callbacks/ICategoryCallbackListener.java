package pasindu.dev.classie.ro_pos2.Callbacks;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Model.CategoryModel;

public interface ICategoryCallbackListener {
    void onCategoryLoadSuccess(List<CategoryModel> categoryModelsList);
    void onCategoryLoadFailed(String message);
}
