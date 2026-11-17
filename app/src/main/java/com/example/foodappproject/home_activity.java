package com.example.foodappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Locale;

public class home_activity extends AppCompatActivity {

    private final String[] categories = {"Tất cả", "Hộp cắt sẵn", "Mix thập cẩm", "Trái cây dầm", "Nước ép tươi"};
    private String selectedCategory = "Tất cả";
    private String searchQuery = "";

    private TextView tvHomeGreeting;
    private Button btnTopCart;
    private LinearLayout llCategories;
    private LinearLayout llFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvHomeGreeting = findViewById(R.id.tvHomeGreeting);
        btnTopCart = findViewById(R.id.btnTopCart);
        llCategories = findViewById(R.id.llCategories);
        llFoodList = findViewById(R.id.llFoodList);
        EditText edtSearch = findViewById(R.id.edtSearchFood);

        btnTopCart.setOnClickListener(v -> startActivity(new Intent(home_activity.this, cart_activity.class)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim().toLowerCase(Locale.getDefault());
                renderFoodList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        FoodDataManager.setupBottomNav(this, "home");
        renderCategories();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(profile_activity.PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(profile_activity.KEY_NAME, "Trần Thiên Bửu");
        boolean isOpen = FoodDataManager.isStoreOpen(this);
        tvHomeGreeting.setText("Xin chào, " + name + (isOpen ? " 🍉" : " (Quán tạm nghỉ)"));
        updateCartBadge();
        renderFoodList();
    }

    private void updateCartBadge() {
        int count = FoodDataManager.getCartTotalCount(this);
        btnTopCart.setText("🛒 " + count);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    private void renderCategories() {
        llCategories.removeAllViews();
        for (String cat : categories) {
            TextView chip = new TextView(this);
            chip.setText(cat);
            chip.setTextSize(14f);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setPadding(dp(18), dp(9), dp(18), dp(9));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMarginEnd(dp(10));
            chip.setLayoutParams(lp);

            boolean isSelected = cat.equals(selectedCategory);
            chip.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            chip.setTextColor(isSelected ? Color.WHITE : Color.parseColor("#1B5E20"));

            chip.setOnClickListener(v -> {
                selectedCategory = cat;
                renderCategories();
                renderFoodList();
            });
            llCategories.addView(chip);
        }
    }

    private void renderFoodList() {
        llFoodList.removeAllViews();
        List<FoodDataManager.FoodItem> foods = FoodDataManager.getAllFoods(this);

        for (FoodDataManager.FoodItem food : foods) {
            boolean matchCat = "Tất cả".equals(selectedCategory) || food.category.equalsIgnoreCase(selectedCategory);
            boolean matchSearch = searchQuery.isEmpty()
                    || food.name.toLowerCase(Locale.getDefault()).contains(searchQuery)
                    || food.category.toLowerCase(Locale.getDefault()).contains(searchQuery);

            if (!matchCat || !matchSearch) continue;

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card_white);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardLp.bottomMargin = dp(12);
            card.setLayoutParams(cardLp);

            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(food.emoji);
            tvEmoji.setTextSize(34f);
            tvEmoji.setGravity(Gravity.CENTER);
            tvEmoji.setBackgroundResource(R.drawable.bg_food_thumb);
            LinearLayout.LayoutParams emojiLp = new LinearLayout.LayoutParams(dp(74), dp(74));
            emojiLp.setMarginEnd(dp(14));
            tvEmoji.setLayoutParams(emojiLp);
            card.addView(tvEmoji);

            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            infoCol.setLayoutParams(infoLp);

            TextView tvName = new TextView(this);
            tvName.setText(food.name);
            tvName.setTextSize(16.5f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(Color.parseColor("#1B5E20"));
            infoCol.addView(tvName);

            TextView tvMeta = new TextView(this);
            tvMeta.setText(food.category + "  •  ⭐ " + food.rating);
            tvMeta.setTextSize(13f);
            tvMeta.setTextColor(Color.parseColor("#558B2F"));
            tvMeta.setPadding(0, dp(3), 0, dp(4));
            infoCol.addView(tvMeta);

            TextView tvPrice = new TextView(this);
            tvPrice.setText(FoodDataManager.formatCurrency(food.price));
            tvPrice.setTextSize(16f);
            tvPrice.setTypeface(null, Typeface.BOLD);
            tvPrice.setTextColor(Color.parseColor("#E65100"));
            infoCol.addView(tvPrice);

            card.addView(infoCol);

            Button btnAdd = new Button(this);
            btnAdd.setText("+ Thêm");
            btnAdd.setAllCaps(false);
            btnAdd.setTextColor(Color.WHITE);
            btnAdd.setTextSize(14f);
            btnAdd.setTypeface(null, Typeface.BOLD);
            btnAdd.setBackgroundResource(R.drawable.bg_chip_selected);
            LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(dp(82), dp(40));
            btnLp.setMarginStart(dp(8));
            btnAdd.setLayoutParams(btnLp);

            btnAdd.setOnClickListener(v -> {
                FoodDataManager.addToCart(this, food, 1);
                updateCartBadge();
                Toast.makeText(this, "Đã thêm " + food.name + " vào giỏ!", Toast.LENGTH_SHORT).show();
            });
            card.addView(btnAdd);

            card.setOnClickListener(v -> showFoodDetailDialog(food));
            llFoodList.addView(card);
        }

        if (llFoodList.getChildCount() == 0) {
            TextView empty = new TextView(this);
            empty.setText("Không tìm thấy hộp trái cây phù hợp.");
            empty.setTextSize(16f);
            empty.setTextColor(Color.parseColor("#33691E"));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(20), dp(40), dp(20), dp(20));
            llFoodList.addView(empty);
        }
    }

    private void showFoodDetailDialog(FoodDataManager.FoodItem food) {
        new AlertDialog.Builder(this)
                .setTitle(food.emoji + "  " + food.name)
                .setMessage("Phân loại: " + food.category + "  |  Đánh giá: ⭐ " + food.rating + "\n"
                        + "Giá bán: " + FoodDataManager.formatCurrency(food.price) + "\n\n"
                        + food.description)
                .setPositiveButton("Thêm vào giỏ hàng", (dialog, which) -> {
                    FoodDataManager.addToCart(this, food, 1);
                    updateCartBadge();
                    Toast.makeText(this, "Đã thêm " + food.name + " vào giỏ!", Toast.LENGTH_SHORT).show();
                })
                .setNeutralButton("Xem giỏ hàng", (dialog, which) -> {
                    FoodDataManager.addToCart(this, food, 1);
                    startActivity(new Intent(home_activity.this, cart_activity.class));
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}
