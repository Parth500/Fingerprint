package com.example.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    KeyStore keyStore;
    public static final String KEY_NAME = "fingerPrint";
    Cipher cipher;
    KeyGenerator keyGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {

            Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_SHORT).show();
            } else {

                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(this, "Register atlist one fingerprint in settings", Toast.LENGTH_SHORT).show();
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(this, "Lock screen security not enabled in settings", Toast.LENGTH_SHORT).show();
                    } else {
                        generatekey();

                        if (cipherInt()) {
                            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                            FingerPrintHandler helper = new FingerPrintHandler(this);
                            helper.startAuth(fingerprintManager,cryptoObject);

                        }

                    }
                }
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void generatekey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get keygenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean cipherInt(){
        try {
            cipher=Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC +"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher'",e);
       }

        try {
            keyStore.load(null);
            SecretKey key=(SecretKey) keyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE,key);

            return true;

        } catch (CertificateException |IOException |NoSuchAlgorithmException |UnrecoverableKeyException |KeyStoreException |InvalidKeyException e) {

            throw new RuntimeException("Failed to init cipher",e);
        }


    }


}
