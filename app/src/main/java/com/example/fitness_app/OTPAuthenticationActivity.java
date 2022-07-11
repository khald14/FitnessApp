package com.example.fitness_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPAuthenticationActivity extends AppCompatActivity {

    TextView changeNumber;
    EditText getOTP;
    android.widget.Button verifyOTP;
    String enteredOTP;

    FirebaseAuth firebaseAuth;
    ProgressBar progressBarOfOTPAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_authentication);

        changeNumber =findViewById(R.id.change_number);
        verifyOTP =findViewById(R.id.verifyotp);
        getOTP=findViewById(R.id.get_otp);
        progressBarOfOTPAuth =findViewById(R.id.progressbarofotpauth);

        firebaseAuth=FirebaseAuth.getInstance();

        changeNumber.setOnClickListener(view -> {
            Intent intent=new Intent(OTPAuthenticationActivity.this, PhoneVerActivity.class);
            startActivity(intent);
        });

        verifyOTP.setOnClickListener(view -> {
            enteredOTP=getOTP.getText().toString();
            if(enteredOTP.isEmpty())
                Toast.makeText(getApplicationContext(),"Enter your OTP First ",Toast.LENGTH_SHORT).show();
            else

            {
                progressBarOfOTPAuth.setVisibility(View.VISIBLE);
                String codeReceived=getIntent().getStringExtra("otp");
                PhoneAuthCredential credential= PhoneAuthProvider.getCredential(codeReceived,enteredOTP);
                signInWithPhoneAuthCredential(credential);

            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                progressBarOfOTPAuth.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),"Login success",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(OTPAuthenticationActivity.this, SetProfileActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                {
                    progressBarOfOTPAuth.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}