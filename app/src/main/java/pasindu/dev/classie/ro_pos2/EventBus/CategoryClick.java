package pasindu.dev.classie.ro_pos2.EventBus;

import pasindu.dev.classie.ro_pos2.Model.CategoryModel;

public class CategoryClick {
    private boolean sucess;
    private CategoryModel categoryModel;

    public CategoryClick(boolean sucess, CategoryModel categoryModel) {
        this.sucess = sucess;
        this.categoryModel = categoryModel;
    }

    public boolean isSucess() {
        return sucess;
    }

    public void setSucess(boolean sucess) {
        this.sucess = sucess;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }
}
