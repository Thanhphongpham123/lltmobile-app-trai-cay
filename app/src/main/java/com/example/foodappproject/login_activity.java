package com.example.foodappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login_activity extends AppCompatActivity {

    private static final String ADMIN_EMAIL_1 = "admin@fruit.vn";
    private static final String ADMIN_EMAIL_2 = "admin";
    private static final String ADMIN_PASS = "admin123";

    private EditText edtEmail;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FoodDataManager.setCurrentRole(this, "NONE");

        edtEmail = findViewById(R.id.edtLoginEmail);
        edtPassword = findViewById(R.id.edtLoginPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegisterDialog);

        // Tự động điền lại tài khoản & mật khẩu đã đăng nhập/đăng ký gần nhất
        edtEmail.setText(FoodDataManager.getLastLoginEmail(this));
        edtPassword.setText(FoodDataManager.getLastLoginPass(this));

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String pass = edtPassword.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập Email tài khoản");
                edtEmail.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                edtPassword.requestFocus();
                return;
            }

            // 1. Kiểm tra nếu là tài khoản ADMIN riêng
            if (email.equalsIgnoreCase(ADMIN_EMAIL_1) || email.equalsIgnoreCase(ADMIN_EMAIL_2)) {
                if (!pass.equals(ADMIN_PASS)) {
                    edtPassword.setError("Sai mật khẩu Admin!");
                    edtPassword.requestFocus();
                    Toast.makeText(this, "Sai mật khẩu tài khoản Admin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                FoodDataManager.setCurrentRole(this, "ADMIN");
                Toast.makeText(this, "Đăng nhập quyền Quản trị viên (Admin) thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login_activity.this, admin_activity.class);
                startActivity(intent);
                finish();
                return;
            }

            // 2. Kiểm tra tài khoản KHÁCH MUA HÀNG riêng (tra cứu trong danh sách tài khoản đã lưu)
            if (!FoodDataManager.authenticateCustomer(this, email, pass)) {
                edtPassword.setError("Sai email hoặc mật khẩu!");
                edtPassword.requestFocus();
                Toast.makeText(this, "Sai thông tin tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            FoodDataManager.setCurrentRole(this, "CUSTOMER");
            Toast.makeText(this, "Đăng nhập tài khoản mua hàng thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(login_activity.this, home_activity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private void showRegisterDialog() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, 0);

        EditText inputName = new EditText(this);
        inputName.setHint("Họ và tên khách hàng");
        layout.addView(inputName);

        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Email đăng nhập");
        layout.addView(inputEmail);

        EditText inputPhone = new EditText(this);
        inputPhone.setHint("Số điện thoại");
        layout.addView(inputPhone);

        EditText inputPass = new EditText(this);
        inputPass.setHint("Mật khẩu");
        layout.addView(inputPass);

        new AlertDialog.Builder(this)
                .setTitle("Đăng ký tài khoản mua hàng")
                .setView(layout)
                .setPositiveButton("Đăng ký", (dialog, which) -> {
                    String name = inputName.getText().toString().trim();
                    String email = inputEmail.getText().toString().trim();
                    String phone = inputPhone.getText().toString().trim();
                    String pass = inputPass.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ họ tên, email và mật khẩu!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (email.equalsIgnoreCase(ADMIN_EMAIL_1) || email.equalsIgnoreCase(ADMIN_EMAIL_2)) {
                        Toast.makeText(this, "Email này dành riêng cho Admin, vui lòng chọn email khác!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String finalPhone = phone;

                    // Lưu vĩnh viễn tài khoản mới vào bộ nhớ
                    FoodDataManager.registerAccount(this, name, email, finalPhone, pass);

                    SharedPreferences prefs = FoodDataManager.currentProfile(this);
                    prefs.edit()
                            .putString(profile_activity.KEY_NAME, name)
                            .putString(profile_activity.KEY_EMAIL, email)
                            .putString(profile_activity.KEY_ADDRESS, "")
                            .putString(profile_activity.KEY_PHONE, finalPhone)
                            .apply();

                    FoodDataManager.setCurrentRole(this, "CUSTOMER");
                    Toast.makeText(this, "Đã tạo và lưu tài khoản " + email + " thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login_activity.this, home_activity.class));
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
