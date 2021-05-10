package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "edu.dlsu.DREW.MESSAGE";
    private static final String TAG = "EmailPassword";
    private FirebaseAuth fAuth;
    Button mResetPassword;

    EditText mEmail,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail      = findViewById(R.id.Email);
        mPassword   = findViewById(R.id.password);
        mResetPassword = findViewById(R.id.resetPassword);
        fAuth = FirebaseAuth.getInstance();


    }


    public void swapToRegister(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);


    }

    public void loginPressed(View view){

        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        authenticate(email,password);

    }

    public void resetPasswordPressed(View view){
        /*final String email = mEmail.getText().toString().trim();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                        }
                    }
                });*/
        EditText resetPassword = new EditText(view.getContext());
        AlertDialog.Builder resetPasswordDialogue = new AlertDialog.Builder(view.getContext());
        resetPasswordDialogue.setTitle("Reset Password?");
        resetPasswordDialogue.setMessage("Input email to receive password reset link.");
        resetPasswordDialogue.setView(resetPassword);

        resetPasswordDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = resetPassword.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Reset link sent to email.",
                                Toast.LENGTH_SHORT).show();
                        alert("Notice","Reset link sent to email","OK");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Reset link failed to send.",
                                Toast.LENGTH_SHORT).show();
                        alert("Error","Reset link failed to send","OK");
                    }
                });
            }
        });

        resetPasswordDialogue.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialogue
            }
        });
        resetPasswordDialogue.create().show();

    }

    public void updateUI(FirebaseUser user){

        if (user != null) {
            Intent intent = new Intent(this, MainMenu.class);
            startActivity(intent);

        }
        else{
            alert("Alert!","Incorrect Information", "OK");
        }
    }



    private void authenticate(String email, String password) {
        // [START sign_in_with_email]
        fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(fAuth.getCurrentUser().isEmailVerified()){
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = fAuth.getCurrentUser();
                                updateUI(user);
                            }
                            // Sign in success, update UI with the signed-in user's information
                            else{
                                //Toast.makeText(MainActivity.this,"Please verify email address",Toast.LENGTH_LONG).show();
                                alert("Notice","Please verify email address", "OK");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void alert(String title, String message, String positiveButton ){
        AlertDialog alert = new AlertDialog.Builder(MainActivity.this).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int x) {
                dialog.dismiss();
            }
        }).create();
        alert.show();
    }

}