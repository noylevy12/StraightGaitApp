package com.example.straightgaitapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonSignIn, buttonSignUp;
//    private EditText editTextEmail, editTextPassword;
//    private TextView textViewSignIn;
//    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            // the user is already logged in...
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
//        progressDialog = new ProgressDialog(this);

        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        buttonSignUp = (Button) findViewById(R.id.btnSignUp);

        buttonSignIn.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            startActivity(new Intent(this, LoginAcitivty.class));
        }
        if(v == buttonSignUp ){
            //open login activity
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }


}
