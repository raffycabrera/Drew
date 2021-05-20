package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AccountOptions extends AppCompatActivity {
    Button changeEmail;
    TextView newEmail;
    FirebaseAuth fAuth;
    FirebaseUser user;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_options);
        changeEmail = findViewById(R.id.changeEmailButton);
        newEmail = findViewById(R.id.emailChange);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();




    }
    public void changeUserEmail(View view){

    }

    public void changeUserPassword(View view){
    final EditText resetPassword = new EditText(view.getContext());
    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
    passwordResetDialog.setTitle("Reset Password?");
    passwordResetDialog.setMessage("New Password should be 6 characters long");
    resetPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    passwordResetDialog.setView(resetPassword);

           passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newPassword = resetPassword.getText().toString();
                    //stop
                   user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                        alert("Success!","Password Updated", "Ok");
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           alert("Failure","Password not updated","Ok");
                       }
                   });
                }
            });
            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //close dialogue
                }
            });
            passwordResetDialog.create().show();

        }
    private void alert(String title, String message, String positiveButton ){
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(AccountOptions.this).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int x) {
                dialog.dismiss();
            }
        }).create();
        alert.show();
    }
    }
