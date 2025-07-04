package com.example.filemanager.safefolder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filemanager.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.iconics.view.IconicsImageView;

public class PasswordActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;


    TextInputEditText createPassword;
    TextInputEditText createConfirmPassword;
    MaterialButton btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);

        createPassword = findViewById(R.id.createPassword);
        createConfirmPassword = findViewById(R.id.createConfirmPassword);
        btnCreateAccount = findViewById(R.id.createAccountBtn);

        SharedPreferences.Editor userPassword = sharedPreferences.edit();

        btnCreateAccount.setOnClickListener(v -> {

            String password = createPassword.getText().toString().trim();
            String confirmPassword = createConfirmPassword.getText().toString().trim();

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(PasswordActivity.this, "Plz enter password first", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(confirmPassword)) {

                userPassword.putString("password", password);
                userPassword.putBoolean("isOpenSaveFolder", true);
                userPassword.apply();
                Intent intent = new Intent(PasswordActivity.this, LogInActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(PasswordActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }


        });


    }
}