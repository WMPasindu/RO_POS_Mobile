package pasindu.dev.classie.ro_pos2.EventBus;

import pasindu.dev.classie.ro_pos2.Model.BestDealsModel;

public class BestDealsItemClick {
    private BestDealsModel bestDealsModel;

    public BestDealsItemClick(BestDealsModel bestDealsModel) {
        this.bestDealsModel = bestDealsModel;
    }

    public BestDealsModel getBestDealsModel() {
        return bestDealsModel;
    }

    public void setBestDealsModel(BestDealsModel bestDealsModel) {
        this.bestDealsModel = bestDealsModel;
    }
}
