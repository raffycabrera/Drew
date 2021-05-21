package edu.dlsu.drew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AccountOptions extends AppCompatActivity {
    Button changeEmail;
    FirebaseAuth fAuth;
    FirebaseUser user;
    TextView mAdminVerifText;
    EditText mAdminVerif;
    String userID,currentAccountRole, selectedRole,adminVerifPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_options);
        changeEmail = findViewById(R.id.changeEmailButton);
        mAdminVerif = findViewById(R.id.adminVerif);
        mAdminVerifText = findViewById(R.id.textView6);
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        mAdminVerif.setVisibility(View.INVISIBLE);
        mAdminVerifText.setVisibility(View.INVISIBLE);
        Query queryAuth = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        queryAuth.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 currentAccountRole= (String) snapshot.child("role").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Spinner roleSpinner = findViewById(R.id.accountRole);
        String[] accountRoleList= new String[]{"Basic", "Responder", "Administrator"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, accountRoleList);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedRole = roleSpinner.getSelectedItem().toString();
                if (selectedRole.equals("Administrator")){
                    mAdminVerif.setVisibility(View.VISIBLE);
                    mAdminVerifText.setVisibility(View.VISIBLE);
                }
                else if(selectedRole.equals("Basic") || selectedRole.equals("Responder")){
                    mAdminVerif.setVisibility(View.INVISIBLE);
                    mAdminVerifText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void changeUserEmail(View view){
        final EditText updateEmail = new EditText(view.getContext());
        final AlertDialog.Builder updateEmailDialog = new AlertDialog.Builder(view.getContext());
        updateEmailDialog.setTitle("Change email?");
        updateEmailDialog.setMessage("You will be logged out after updating your email and a new verification email will be sent to your new email.");
        updateEmailDialog.setView(updateEmail);

        updateEmailDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newEmail = updateEmail.getText().toString();
                //stop
                user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        sendEmailVerification();
                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("email").setValue(newEmail);
                        alert("Success!","Email updated and verification sent, signing out and returning to log in screen", "Ok");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alert("Failure","Email not updated","Ok");
                    }
                });
            }
        });
        updateEmailDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialogue
            }
        });
        updateEmailDialog.create().show();
    }

    public void changeUserPassword(View view){
    final EditText resetPassword = new EditText(view.getContext());
    final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
    passwordResetDialog.setTitle("Reset Password? You will be logged out after updating your password.");
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
                        alert("Success!","Password Updated, signing out and returning to log in screen.", "Ok");
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
    public void changeUserRole(View view) {
        String adminVerifPass = mAdminVerif.getText().toString().trim();
        if (currentAccountRole.equals(selectedRole)){
        alert("Alert!","You already have the selected role!","Ok");
        }
        else if (!adminVerifPass.equals("#em!er)ge&nc^2y") && selectedRole.equals("Administrator")) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("role").setValue(selectedRole);
            alert("Success!","Correct Administrator password, Role has been changed","Ok");
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("role").setValue(selectedRole);
            alert("Success!","Role has been changed, signing out.","Ok");
        }
    }
    private void alert(String title, String message, String positiveButton ){
        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(AccountOptions.this).setTitle(title).setMessage(message).setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int x) {
                dialog.dismiss();
                fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }).create();
        alert.show();
    }
    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = fAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }
    }
