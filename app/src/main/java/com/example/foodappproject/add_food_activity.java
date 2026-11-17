package com.example.foodappproject;

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

public class add_food_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_food);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBackAddFood);
        btnBack.setOnClickListener(v -> finish());

        EditText edtName = findViewById(R.id.edtFoodName);
        EditText edtCat = findViewById(R.id.edtFoodCategory);
        EditText edtPrice = findViewById(R.id.edtFoodPrice);
        EditText edtDesc = findViewById(R.id.edtFoodDesc);
        Button btnSave = findViewById(R.id.btnSaveNewFood);

        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String cat = edtCat.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();

            if (name.isEmpty()) {
                edtName.setError("Vui lòng nhập tên hộp trái cây");
                return;
            }
            int price = 45000;
            try {
                price = Integer.parseInt(priceStr);
            } catch (Exception ignored) {
            }

            FoodDataManager.addCustomFood(
                    this,
                    name,
                    cat.isEmpty() ? "Hộp cắt sẵn" : cat,
                    price,
                    desc.isEmpty() ? "Trái cây tươi gọt sẵn trong ngày, tặng kèm muối ớt Tây Ninh." : desc,
                    "🍇"
            );

            Toast.makeText(this, "Đã thêm " + name + " vào quầy trái cây!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
