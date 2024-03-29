package pasindu.dev.classie.ro_pos2.ui.comments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import pasindu.dev.classie.ro_pos2.Adapter.CommentAdapter;
import pasindu.dev.classie.ro_pos2.Callbacks.ICommentCallbackListener;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.CommentsModel;
import pasindu.dev.classie.ro_pos2.R;

public class CommentFragment extends BottomSheetDialogFragment implements ICommentCallbackListener {

    private CommentViewModel commentViewModel;
    private Unbinder unbinder;

    @BindView(R.id.recycler_comment)
    RecyclerView recycler_comment;

    AlertDialog dialog;
    ICommentCallbackListener listener;

    public CommentFragment() {
        listener = this;
    }

    private static CommentFragment instance;

    public static CommentFragment getInstance() {
        if(instance == null)
            instance = new CommentFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_comment_fragment,container,false);
        unbinder = ButterKnife.bind(this,itemView);
        initViews();
        loadCommentFromFirebase();
        commentViewModel.getMutableLiveDataFoodList().observe(this, commenntsModels -> {
            CommentAdapter adapter = new CommentAdapter(getContext(), commenntsModels);
            recycler_comment.setAdapter(adapter);

        });
        return itemView;
    }

    private void loadCommentFromFirebase() {
        dialog.show();
        List<CommentsModel> commentsModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.COMMENT_REF)
                .child(Common.selectFood.getId())
                .orderByChild("serverTimeStamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                            CommentsModel commentsModel = commentSnapshot.getValue(CommentsModel.class);
                            commentsModels.add(commentsModel);
                        }
                        listener.onCommentLoadSuccess(commentsModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onCommentLOadFailed(databaseError.getMessage());
                    }
                });
    }

    private void initViews() {

        commentViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();

        recycler_comment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, true);
        recycler_comment.setLayoutManager(layoutManager);
        recycler_comment.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }

    @Override
    public void onCommentLoadSuccess(List<CommentsModel> commentsModels) {
        dialog.dismiss();
        commentViewModel.setCommentList(commentsModels);
    }

    @Override
    public void onCommentLOadFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
