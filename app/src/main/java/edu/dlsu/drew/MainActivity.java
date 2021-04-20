package edu.dlsu.drew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;




public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "edu.dlsu.DREW.MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }


    public void swapToRegister(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);


    }


    public void swapToMainMenu(View view){
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);

    }


}