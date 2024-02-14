package com.example.chomg;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class SecureStorage {
    private static final String FILE_NAME = "secure_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";

    public static void saveAuthToken(Context context, String token) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_AUTH_TOKEN, token);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            // Consider handling the error more gracefully in a real app
        }
    }
    public static String getAuthToken(Context context) {
        try {
            // Correct way to create a MasterKey with default specification
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    FILE_NAME,
                    masterKey, // Use the created masterKey here
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Return the token, or null if not found
            return sharedPreferences.getString(KEY_AUTH_TOKEN, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exception or return null
        }
    }
}
