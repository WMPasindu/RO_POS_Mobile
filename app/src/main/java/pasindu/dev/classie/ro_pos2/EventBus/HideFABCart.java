package pasindu.dev.classie.ro_pos2.EventBus;

public class HideFABCart {
    private boolean hidden;

    public HideFABCart(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
