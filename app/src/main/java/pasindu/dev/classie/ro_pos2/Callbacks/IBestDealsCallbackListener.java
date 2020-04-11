package pasindu.dev.classie.ro_pos2.Callbacks;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Model.BestDealsModel;

public interface IBestDealsCallbackListener {
    void onBestDealsLoadSuccess(List<BestDealsModel> bestDealsModels);
    void onBestDealsLoadFailed(String message);
}
