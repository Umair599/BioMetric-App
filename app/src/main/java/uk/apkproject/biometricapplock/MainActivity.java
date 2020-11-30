package uk.apkproject.biometricapplock;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  {
    private CancellationSignal cancellationSignal=new CancellationSignal();
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private Button loginButton;
    private KeyguardManager keyguardManager;
    private BiometricPrompt.PromptInfo promptInfo;
    private BiometricPrompt biometricPrompt;
    BiometricPrompt.CryptoObject crypto;
    void notifyUser(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    Boolean checkBiometricSupport(){
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if(!keyguardManager.isKeyguardSecure()){
            notifyUser("Fingerprint authentication has not been enable in settings");
            return false;
                    }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC)!= PackageManager.PERMISSION_GRANTED) //Check Permission
        { notifyUser("Fingerprint authentication permission is not enabled");
        return false;}
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            return true;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Executor executor = Executors.newSingleThreadExecutor();
        loginButton= findViewById(R.id.login_button);
        if(checkBiometricSupport()){
            biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    Toast.makeText(MainActivity.this, errString, Toast.LENGTH_LONG).show();
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        // user clicked negative button
                        //Toast.makeText(activity, "Operation Cancelled By User!", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(activity, "Unknown Error!", Toast.LENGTH_SHORT).show();
                        // Called when an unrecoverable error has been encountered and the operation is complete.
                    }
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    startActivity(new Intent(MainActivity.this, LoggedInActivity.class));
                    //Toast.makeText(activity, "Login Successful!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    //Toast.makeText(activity, "Fingerprint not recognized!", Toast.LENGTH_SHORT).show();
                }
            });
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Login")
                    .setSubtitle("Login to your account!")
                    .setDescription("Place your finger on the device home button to verify your identity")
                    .setNegativeButtonText("CANCEL")
                    .build();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    biometricPrompt.authenticate(promptInfo);
                }
            });
        }

    }
}
