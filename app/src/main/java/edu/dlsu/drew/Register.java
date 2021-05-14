package edu.dlsu.drew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import android.widget.ArrayAdapter;
import android.widget.Spinner;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.SQLOutput;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    FirebaseFirestore fStore;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference();
    EditText mFullName,mEmail,mPassword,mPasswordConfirm, mAdminVerif;
    Button mRegisterBtn;
    TextView mBackBtn, backToSignIn;
    String userID, accountRole;
    TextView mAdminVerifText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // [START initialize_auth]
        // Initialize Firebase Auth

        mFullName   = findViewById(R.id.fullName);
        mEmail      = findViewById(R.id.Email);
        mPassword   = findViewById(R.id.password);
        mRegisterBtn= findViewById(R.id.registerBtn);
        mPasswordConfirm = findViewById(R.id.password2);
        mBackBtn = findViewById(R.id.backToSignIn);
        mAdminVerif = findViewById(R.id.adminVerif);
        mAdminVerifText = findViewById(R.id.textView6);
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        fStore = FirebaseFirestore.getInstance();
        mAdminVerif.setVisibility(View.INVISIBLE);
        mAdminVerifText.setVisibility(View.INVISIBLE);
        Spinner roleSpinner = findViewById(R.id.accountRole);
        String[] accountRoleList= new String[]{"Basic", "Responder", "Administrator"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountRoleList);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                accountRole = roleSpinner.getSelectedItem().toString();
                if (accountRole.equals("Administrator")){
                    mAdminVerif.setVisibility(View.VISIBLE);
                    mAdminVerifText.setVisibility(View.VISIBLE);
                }
                else if(accountRole.equals("Basic") || accountRole.equals("Responder")){
                    mAdminVerif.setVisibility(View.INVISIBLE);
                    mAdminVerifText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });




    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }
    // [END on_start_check_user]


    public void backToSignInPressed(View view){

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }





    private void createAccount(String email, String password, String fullName, String accountRole) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = task.getResult().getUser().getUid();
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", fullName);
                            userMap.put("email", email);
                            userMap.put("role", accountRole);
                            root.child("Users").child(uid).setValue(userMap);
                            sendEmailVerification();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Email verification sent.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        alert("Congratulations!"," Account created. Please check your email for verification to finalize creation","OK");
        // [END create_user_with_email]
    }
//}
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() { }

    private void updateUI(FirebaseUser user) {

    }

    public void registeredPressed(View view){

        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String fullName = mFullName.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString().trim();
        String adminVerifPass = mAdminVerif.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty() || fullName.isEmpty())
        {
            alert("Alert!","Please fill in all required fields.", "OK");
        }
        else if(!password.equals(passwordConfirm)){
            alert("Alert!","Please make sure passwords match.","OK");
        }
        else if(!adminVerifPass.equals("#em!er)ge&nc^2y") && accountRole.equals("Administrator")){
            alert("Alert!","Incorrect Administrator Verification Password!","OK");
        }
        else {
            createAccount(email, password, fullName, accountRole);
        }

    }
    private void alert(String title, String message, String positiveButton ){
        AlertDialog alert = new AlertDialog.Builder(Register.this).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int x) {
                dialog.dismiss();
            }
        }).create();
        alert.show();
    }

    public void loginPressed(View view){

    }



}
