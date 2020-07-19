package com.straightgait.straightgaitapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "TAG";
    private Button buttonRegister;
    private EditText editTextEmail, editTextPassword, editTxtName, editTxtLastName, editTxtBirthDate;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonFemale, radioButtonMale;
    private TextView textViewSignIn;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore DB;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();


        if(firebaseAuth.getCurrentUser() != null){
            // the user is already logged in...
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.btnRegister);
        editTextEmail  = (EditText) findViewById(R.id.editTxtEmail);
        editTextPassword  = (EditText) findViewById(R.id.editTxtPassword);
        editTxtName = (EditText) findViewById(R.id.editTxtName);
        editTxtLastName = (EditText) findViewById(R.id.editTxtLastName);
        editTxtBirthDate = (EditText) findViewById(R.id.editTxtBirthDate);
        textViewSignIn = (TextView) findViewById(R.id.textViewSignIn);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioButtonFemale);
        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);


        buttonRegister.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

    }

    private void registerUser() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String firstName = editTxtName.getText().toString().trim();
        final String lastName = editTxtLastName.getText().toString().trim();
        final String birthDate = editTxtBirthDate.getText().toString().trim();
        final String gender;
        // get selected radio button from radioGroup
        int selectedId = radioGroupGender.getCheckedRadioButtonId();


        //validation
        if(selectedId == -1){
            Toast.makeText(this, "Please choose gender", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if(selectedId == radioButtonFemale.getId()){
                gender = "female";
            }else {
                gender = "male";
            }
        }
        if(TextUtils.isEmpty(email)){
            //the email is empty
            editTextEmail.setError("email is required");
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //the password is empty
            editTextPassword.setError("password is required");
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(firstName)){
            //the first name is empty
            editTxtName.setError("first name is required");
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(lastName)){
            //the last name is empty
            editTxtLastName.setError("last name is required");
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(birthDate)){
            //the birth date is empty
            editTxtBirthDate.setError("birth date is required");
            Toast.makeText(this, "Please enter birth date", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //user is successfully registerd ang logged in
                    Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                    userId = firebaseAuth.getCurrentUser().getUid();
                    //store user details
                    DocumentReference documentReference = DB.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("firstName", firstName);
                    user.put("lastName", lastName);
                    user.put("email", email);
                    user.put("birthDate", birthDate);
                    user.put("gender", gender);
                    user.put("firstDate", new Date());
                    user.put("lastDate", new Date());
                    user.put("nextLastDate", new Date());
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user profile is created for "+ userId);
                        }
                    });





                    finish();
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                }else {
                    progressDialog.cancel();
                    Toast.makeText(SignUpActivity.this, "Registerd Failed, pleas try again" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
    @Override
    public void onClick(View v) {
        if(v == buttonRegister){
            registerUser();
        }
        if(v == textViewSignIn ){
            //open login activity
            startActivity(new Intent(this, LoginAcitivty.class));
        }
    }


}
