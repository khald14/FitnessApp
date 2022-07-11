package com.example.fitness_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneVerActivity extends AppCompatActivity {



    EditText phoneNumberEditText;
    android.widget.Button sendOTP;
    CountryCodePicker countryCodePicker;
    String countryCode;
    String phoneNumber;

    FirebaseAuth firebaseAuth;
    ProgressBar progressBarOfMain;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String codeSent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_ver);
        countryCodePicker =findViewById(R.id.country_code_picker);
        sendOTP =findViewById(R.id.send_otp_button);
        phoneNumberEditText =findViewById(R.id.phone_number_edit_text);
        progressBarOfMain=findViewById(R.id.progress_bar_for_main);

        firebaseAuth=FirebaseAuth.getInstance();

        countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

        countryCodePicker.setOnCountryChangeListener(() -> countryCode = countryCodePicker.getSelectedCountryCodeWithPlus());

        //send the otp to the phone number.
        sendOTP.setOnClickListener(view -> {
            String number;
            number= phoneNumberEditText.getText().toString();
            if(number.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Please Enter YOur number",Toast.LENGTH_SHORT).show();
            }
            else if(number.length()<10)
            {
                Toast.makeText(getApplicationContext(),"Please Enter correct number",Toast.LENGTH_SHORT).show();
            }
            else
            {

                progressBarOfMain.setVisibility(View.VISIBLE);
                phoneNumber = countryCode +number;

                PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(PhoneVerActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            }

            //go to the otp auth activity.
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"OTP is Sent",Toast.LENGTH_SHORT).show();
                progressBarOfMain.setVisibility(View.INVISIBLE);
                codeSent =s;
                Intent intent=new Intent(PhoneVerActivity.this, OTPAuthenticationActivity.class);
                intent.putExtra("otp", codeSent);
                startActivity(intent);
            }
        };
    }

    //check if the user already logged in to the app.
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(PhoneVerActivity.this,HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}