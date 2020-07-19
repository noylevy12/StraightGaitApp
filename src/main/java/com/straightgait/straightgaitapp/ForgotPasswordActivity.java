package com.straightgait.straightgaitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Button buttonResetPassword;
    private EditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            // the user is already logged in...
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        progressDialog = new ProgressDialog(this);
        buttonResetPassword = (Button) findViewById(R.id.btnResetPassword);
        editTextEmail  = (EditText) findViewById(R.id.editTxtEmail);

        buttonResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v== buttonResetPassword){
            sendResetPasswordEmail();
        }

    }

    private void sendResetPasswordEmail() {
        String email = editTextEmail.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            //the email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Send Email...");
        progressDialog.show();
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
//        String noyMail = "noylevy12@gmail.com";
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent. Login again with the new password.", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginAcitivty.class));
                        }
                        else {
                            Toast.makeText(ForgotPasswordActivity.this, "There is a problem.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
