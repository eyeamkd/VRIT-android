package com.example.vrit;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity{

    public String VERIFICATION_STATUS = "NOT_STARTED";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks otpCallBack;
    public String otpEntered = "";
    private FirebaseAuth phoneAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        otpCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneNumber(phoneAuthCredential);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                onOtpSent();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,otpEntered);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
        };
    }
    public void onLoginButtonPressed(View view){
        EditText phonenumberEntered = findViewById(R.id.phoneNumber);
        String phoneNumber = phonenumberEntered.toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                this.otpCallBack);        // OnVerificationStateChangedCallbacks
        VERIFICATION_STATUS = "IN_PROGRESS";
    }
    public void onLoginSuccess(){
        Toast.makeText(this,"Login Success!!", Toast.LENGTH_SHORT).show();
    }
    public void onLoginFailed(){
        Toast.makeText(this,"Login Failed!!", Toast.LENGTH_SHORT).show();
    }
    public void onOtpSent(){
        Toast.makeText(this,"OTP SENT!!", Toast.LENGTH_SHORT).show();
    }
    public void onEnterOtpClicked(){
        EditText otp = findViewById(R.id.editText3);
        otpEntered = otp.toString();
    }

    public void signInWithPhoneNumber(PhoneAuthCredential phoneAuthCredential){
        phoneAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    onLoginSuccess();
                    FirebaseUser user = task.getResult().getUser();
                    // ...
                } else {
                   onLoginFailed();
                }
            }
        });
    }



}
