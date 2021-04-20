package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "edu.dlsu.DREW.MESSAGE";
    private static final String TAG = "EmailPassword";
    private FirebaseAuth fAuth;


    EditText mEmail,mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail      = findViewById(R.id.Email);
        mPassword   = findViewById(R.id.password);

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
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = fAuth.getCurrentUser();
                                updateUI(user);
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