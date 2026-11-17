package com.example.foodappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class profile_activity extends AppCompatActivity {

    public static final String PREFS_NAME = "UserProfilePrefs";
    public static final String KEY_NAME = "user_name";
    public static final String KEY_ADDRESS = "user_address";
    public static final String KEY_EMAIL = "user_email";
    public static final String KEY_PHONE = "user_phone";

    private TextView tvProfileName;
    private TextView tvProfileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileInfo = findViewById(R.id.tvProfileInfo);

        Button btnEditProfile = findViewById(R.id.MovetoEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(profile_activity.this, edit_profile_activity.class);
            startActivity(intent);
        });

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(profile_activity.this, history_activity.class);
            startActivity(intent);
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản mua hàng?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            FoodDataManager.setCurrentRole(this, "NONE");
                            Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(profile_activity.this, login_activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show()
        );

        FoodDataManager.setupBottomNav(this, "profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_NAME, "Pham Ngoc Thanh Phong");
        String address = prefs.getString(KEY_ADDRESS, "TP. Hồ Chí Minh");
        String email = prefs.getString(KEY_EMAIL, "buu@foodapp.vn");
        String phone = prefs.getString(KEY_PHONE, "0352847621");

        tvProfileName.setText(name);
        tvProfileInfo.setText(email + " • " + phone + "\n" + address);
    }
}
