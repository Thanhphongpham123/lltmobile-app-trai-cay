package com.example.foodappproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

    private android.net.Uri pendingAvatar;
    private android.widget.ImageView avatar;
    private final androidx.activity.result.ActivityResultLauncher<String> pickAvatar =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pendingAvatar = uri;
                    previewAvatar(uri);
                }
            });

    private void previewAvatar(Object model) {
        com.bumptech.glide.Glide.with(this).load(model).circleCrop().placeholder(R.drawable.user)
                .error(R.drawable.user).into(avatar);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (pendingAvatar != null) state.putString("selected_avatar", pendingAvatar.toString());
    }

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

        SharedPreferences prefs = FoodDataManager.currentProfile(this);
        edtName.setText(prefs.getString(profile_activity.KEY_NAME, ""));
        edtAddress.setText(prefs.getString(profile_activity.KEY_ADDRESS, ""));
        edtEmail.setText(prefs.getString(profile_activity.KEY_EMAIL, ""));
        edtPhone.setText(prefs.getString(profile_activity.KEY_PHONE, ""));

        ImageButton btnBack = findViewById(R.id.BackToProfile);
        btnBack.setOnClickListener(v -> finish());

        Button btnSave = findViewById(R.id.btnSaveProfile);
        avatar = findViewById(R.id.imgEditAvatar);
        if (savedInstanceState != null && savedInstanceState.containsKey("selected_avatar")) {
            pendingAvatar = android.net.Uri.parse(savedInstanceState.getString("selected_avatar"));
        }
        previewAvatar(pendingAvatar != null ? pendingAvatar : prefs.getString(profile_activity.KEY_AVATAR, null));
        android.widget.TextView change = findViewById(R.id.changeAvatar);
        change.setOnClickListener(v -> pickAvatar.launch("image/*"));
        avatar.setOnClickListener(v -> change.performClick());
        AvatarUpload upload = new androidx.lifecycle.ViewModelProvider(this).get(AvatarUpload.class);
        upload.state.observe(this, state -> {
            boolean busy = state == AvatarUpload.State.UPLOADING;
            btnSave.setEnabled(!busy);
            btnSave.setText(busy ? "Đang tải ảnh…" : "Lưu thông tin");
            change.setEnabled(!busy);
            avatar.setEnabled(!busy);
            edtName.setEnabled(!busy);
            edtEmail.setEnabled(!busy);
            edtPhone.setEnabled(!busy);
            edtAddress.setEnabled(!busy);
            if (state == AvatarUpload.State.SAVED) {
                Toast.makeText(this, "Đã lưu hồ sơ và ảnh đại diện", Toast.LENGTH_SHORT).show();
                finish();
            } else if (state == AvatarUpload.State.FAILED) {
                Toast.makeText(this, "Tải ảnh thất bại. Kiểm tra kết nối hoặc chọn ảnh khác rồi thử lại.", Toast.LENGTH_LONG).show();
            }
        });
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
            if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không đúng định dạng");
                edtEmail.requestFocus();
                return;
            }
            String phoneDigits = phone.replaceAll("[ .()-]", "");
            if (!phone.isEmpty() && !phoneDigits.matches("\\+?[0-9]{7,15}")) {
                edtPhone.setError("Số điện thoại không đúng định dạng");
                edtPhone.requestFocus();
                return;
            }

            SharedPreferences.Editor changes = prefs.edit()
                    .putString(profile_activity.KEY_NAME, name)
                    .putString(profile_activity.KEY_ADDRESS, address)
                    .putString(profile_activity.KEY_EMAIL, email)
                    .putString(profile_activity.KEY_PHONE, phone);

            if (pendingAvatar != null) {
                if (getString(R.string.avatar_cloud_name).isEmpty() || getString(R.string.avatar_upload_preset).isEmpty()) {
                    Toast.makeText(this, "Chưa cấu hình Cloudinary để lưu ảnh.", Toast.LENGTH_LONG).show();
                    return;
                }
                upload.save(this, pendingAvatar, changes);
                return;
            }
            changes.apply();
            Toast.makeText(this, "Đã lưu thông tin hồ sơ!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
