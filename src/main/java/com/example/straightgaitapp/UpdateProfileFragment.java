package com.example.straightgaitapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class UpdateProfileFragment extends Fragment {

    private static final String TAG = "UpdateProfileFragment";
    private FirebaseFirestore db;
    private DocumentReference usersDocRef;
    private Button btnUpdate, btnResetPassword ;
    private EditText editTextEmail, editTxtName, editTxtLastName, editTxtBirthDate;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonFemale, radioButtonMale;
    private TextView textViewReturnToProfile;
    private ProgressDialog progressDialog;

    private String userId;
    private String firstName, lastName,email, gender, birthDate;
    private Timestamp birthDateTS;
    private Date birthDateD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile_update, container, false);
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        usersDocRef = db.collection("users").document(userId);


        progressDialog = new ProgressDialog(getContext());

        btnUpdate = (Button) rootView.findViewById(R.id.btnRegister);
        btnResetPassword = (Button) rootView.findViewById(R.id.btnResetPassword);
        editTextEmail  = (EditText) rootView.findViewById(R.id.editTxtEmail);
        editTxtName = (EditText) rootView.findViewById(R.id.editTxtName);
        editTxtLastName = (EditText) rootView.findViewById(R.id.editTxtLastName);
        editTxtBirthDate = (EditText) rootView.findViewById(R.id.editTxtBirthDate);
        textViewReturnToProfile = (TextView) rootView.findViewById(R.id.textViewReturnToProfile);
        radioButtonFemale = (RadioButton) rootView.findViewById(R.id.radioButtonFemale);
        radioButtonMale = (RadioButton) rootView.findViewById(R.id.radioButtonMale);
        radioGroupGender = (RadioGroup) rootView.findViewById(R.id.radioGroupGender);

        setUiUserInfo();


        return rootView;
    }

    private void setUiUserInfo() {
        usersDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    firstName = documentSnapshot.getString("firstName");
                    lastName = documentSnapshot.getString("lastName");
                    gender = documentSnapshot.getString("gender");
                    email = documentSnapshot.getString("email");
                    birthDate = documentSnapshot.getString("birthDate");
//                    birthDateTS = documentSnapshot.getTimestamp("birthDate");

//                    birthDateD = birthDateTS.toDate();

//                    firstDate = monthAndYearFormat.format(firstDateD);
//                    lastDate = dayAndMonthFormat.format(lastDateD);


                    if(gender.equals("female")){
                        radioButtonFemale.setChecked(true);

                    }else if(gender.equals("male")){
                        radioButtonMale.setChecked(true);

                    }
                    editTextEmail.setText(email);
                    editTxtName.setText(firstName);
                    editTxtLastName.setText(lastName);
                    editTxtBirthDate.setText(birthDate);

                }
                else {

                }
            }
        });
    }
}

