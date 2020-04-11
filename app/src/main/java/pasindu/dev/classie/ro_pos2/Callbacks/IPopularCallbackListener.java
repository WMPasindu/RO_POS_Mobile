package pasindu.dev.classie.ro_pos2.Callbacks;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Model.PopularCategoryModel;

public interface IPopularCallbackListener {
    void onPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onPopularLoadFailed(String message);

}
