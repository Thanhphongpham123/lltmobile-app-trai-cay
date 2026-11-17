package com.example.foodappproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class change_password_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText edtOld = findViewById(R.id.edtOldPass);
        EditText edtNew = findViewById(R.id.edtNewPass);
        EditText edtConfirm = findViewById(R.id.edtConfirmPass);
        Button btnUpdate = findViewById(R.id.btnUpdatePassword);

        btnUpdate.setOnClickListener(v -> {
            String oldPass = edtOld.getText().toString().trim();
            String newPass = edtNew.getText().toString().trim();
            String confirmPass = edtConfirm.getText().toString().trim();

            if (newPass.isEmpty()) {
                edtNew.setError("Vui lòng nhập mật khẩu mới");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                edtConfirm.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            FoodDataManager.setPassword(this, newPass);
            edtOld.setText("");
            edtNew.setText("");
            edtConfirm.setText("");
            Toast.makeText(this, "Đã đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
        });

        FoodDataManager.setupBottomNav(this, "lock");
    }
}
