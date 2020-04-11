package pasindu.dev.classie.ro_pos2.Callbacks;

import java.util.List;

import pasindu.dev.classie.ro_pos2.Model.CommentsModel;

public interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<CommentsModel> commentsModels);
    void onCommentLOadFailed(String message);
}
