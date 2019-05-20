package com.example.fingerprint;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.util.concurrent.CancellationException;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    Context context;

    public FingerPrintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager manager,FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal=new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(context,Manifest.permission.USE_FINGERPRINT)!=PackageManager.PERMISSION_GRANTED){
            return;
        }
        manager.authenticate(cryptoObject,cancellationSignal,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        update(false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        update(false);

    }

    @Override
    public void onAuthenticationFailed() {
        update(false);

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        update(true);

    }

    public  void  update(boolean success){
        if (success){
            context.startActivity(new Intent(context,FirstActivity.class));
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }
    }
}
