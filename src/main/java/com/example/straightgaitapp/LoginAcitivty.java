package com.example.straightgaitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginAcitivty extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Button buttonSignIn;
    private EditText editTextEmail, editTextPassword;
    private TextView textViewSignUp , textViewForgotPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            // the user is already logged in...
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }


            progressDialog = new ProgressDialog(this);

        buttonSignIn = (Button) findViewById(R.id.btnSignIn);
        editTextEmail  = (EditText) findViewById(R.id.editTxtEmail);
        editTextPassword  = (EditText) findViewById(R.id.editTxtPassword);
        textViewSignUp = (TextView) findViewById(R.id.textViewSignUp);
        textViewForgotPassword = (TextView) findViewById(R.id.textViewForgotPassword);



        buttonSignIn.setOnClickListener(this);
        textViewSignUp.setOnClickListener(this);
        textViewForgotPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == buttonSignIn){
            userLogin();
        }
        if(v == textViewSignUp){
            finish();
            startActivity(new Intent(this, SignUpActivity.class));
        }
        if(v == textViewForgotPassword){
            finish();
            startActivity(new Intent(this, ForgotPasswordActivity.class));

//            String email = editTextEmail.getText().toString().trim();
//            firebaseAuth.sendPasswordResetEmail(email)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
////                                Log.d(TAG, "Email sent...");
//                                Toast.makeText(LoginAcitivty.this, "Email sent.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //validation
        if(TextUtils.isEmpty(email)){
            //the email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //the password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logged in User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    //start the profile activity
                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                } else {
                    Toast.makeText(LoginAcitivty.this, "There is a problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
