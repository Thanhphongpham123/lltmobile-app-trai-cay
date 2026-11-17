package com.example.foodappproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class edit_profile_activity extends AppCompatActivity {

    private EditText edtName;
    private EditText edtAddress;
    private EditText edtEmail;
    private EditText edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        SharedPreferences prefs = getSharedPreferences(profile_activity.PREFS_NAME, MODE_PRIVATE);
        edtName.setText(prefs.getString(profile_activity.KEY_NAME, "Trần Thiên Bửu"));
        edtAddress.setText(prefs.getString(profile_activity.KEY_ADDRESS, "TP. Hồ Chí Minh"));
        edtEmail.setText(prefs.getString(profile_activity.KEY_EMAIL, "buu@foodapp.vn"));
        edtPhone.setText(prefs.getString(profile_activity.KEY_PHONE, "0901234567"));

        ImageButton btnBack = findViewById(R.id.BackToProfile);
        btnBack.setOnClickListener(v -> finish());

        Button btnSave = findViewById(R.id.btnSaveProfile);
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (name.isEmpty()) {
                edtName.setError("Vui lòng nhập họ tên");
                edtName.requestFocus();
                return;
            }

            prefs.edit()
                    .putString(profile_activity.KEY_NAME, name)
                    .putString(profile_activity.KEY_ADDRESS, address)
                    .putString(profile_activity.KEY_EMAIL, email)
                    .putString(profile_activity.KEY_PHONE, phone)
                    .apply();

            Toast.makeText(this, "Đã lưu thông tin hồ sơ!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
