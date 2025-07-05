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

public class LogInActivity extends AppCompatActivity {


    TextInputEditText logInPassword;
    MaterialButton bntLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_log_in);

        SharedPreferences sharedPrefLogIn = getSharedPreferences("userInfo", MODE_PRIVATE);

        logInPassword = findViewById(R.id.logInPassword);
        bntLogIn = findViewById(R.id.bntLogIn);

        String savedPassword = sharedPrefLogIn.getString("password", "0L0U0F0I0C0k");

        boolean isOpenSafeFolder = sharedPrefLogIn.getBoolean("isOpenSaveFolder", false);

        if (!isOpenSafeFolder) {
            Intent intent = new Intent(LogInActivity.this, PasswordActivity.class);
            startActivity(intent);
        }


        bntLogIn.setOnClickListener(v -> {

            String enterPassWord = logInPassword.getText().toString().trim();

            if (enterPassWord.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enterPassWord.equals(savedPassword)) {
                Intent intent = new Intent(this, SafeFolderActivity.class);
                startActivity(intent);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
            } else {
                Toast.makeText(this, "wrongPassword", Toast.LENGTH_SHORT).show();
            }

        });

    }
}