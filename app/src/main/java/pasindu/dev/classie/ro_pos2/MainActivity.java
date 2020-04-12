package pasindu.dev.classie.ro_pos2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;
import pasindu.dev.classie.ro_pos2.Common.Common;
import pasindu.dev.classie.ro_pos2.Model.UserModel;
import pasindu.dev.classie.ro_pos2.Remote.ICloudFunctions;
import pasindu.dev.classie.ro_pos2.Remote.RetrofitCloudClient;

public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171; //any number
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ICloudFunctions cloudFunctions;
    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> provides;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener == null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        provides = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCE);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        cloudFunctions = RetrofitCloudClient.getInstance().create(ICloudFunctions.class);
        listener = firebaseAuth -> {

            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
//                    Already Login
                                checkUserFormFirebase(user);
                            } else {
//                    not login
                                phoneLogin();
                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "You must enable this permission to use app", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        }
                    }).check();
        };
    }

    private void checkUserFormFirebase(FirebaseUser user) {
        dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(MainActivity.this, "Already Registered Using this ID", Toast.LENGTH_SHORT).show();
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            goToHomeActivity(userModel);
                        } else {
                            showRegisterDialog(user);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterDialog(FirebaseUser user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill your details here");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText edit_name = (EditText) itemView.findViewById(R.id.edt_name);
        EditText edit_address = (EditText) itemView.findViewById(R.id.edt_address);
        EditText edit_phone = (EditText) itemView.findViewById(R.id.edt_phone);

        edit_phone.setText(user.getPhoneNumber().toString());
        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("Register", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(edit_name.getText().toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(edit_address.getText().toString())) {
                Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(edit_phone.getText().toString())) {
                Toast.makeText(this, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                return;
            } else {
                UserModel userModel = new UserModel();
                if (user.getUid() != null && edit_name.getText().toString() != null && edit_address.getText().toString()
                        != null && edit_phone.getText().toString() != null) {
                    userModel.setUserId(user.getUid());
                    userModel.setUserName(edit_name.getText().toString());
                    userModel.setAddress(edit_address.getText().toString());
                    userModel.setPhone(edit_phone.getText().toString());
//                    need to add IMIE here

                    userRef.child(user.getUid()).setValue(userModel)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dialogInterface.dismiss();
                                    Toast.makeText(MainActivity.this, "You have, successfully registered", Toast.LENGTH_SHORT).show();
                                    goToHomeActivity(userModel);
                                } else {
                                    Toast.makeText(MainActivity.this, "Something happened, Please try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Something Wrong!!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void goToHomeActivity(UserModel userModel) {
        Common.currentUser = userModel;
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }

    private void phoneLogin() {

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provides)
                .build(), APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (requestCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
