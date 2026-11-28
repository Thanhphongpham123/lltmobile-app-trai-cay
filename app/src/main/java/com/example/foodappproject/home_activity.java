package com.example.foodappproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class home_activity extends AppCompatActivity {

    private static final String STATE_CATEGORY = "selected_category";
    private static final String ALL_CATEGORIES = "";
    private final Map<String, String> categories = new LinkedHashMap<>();
    private List<FoodDataManager.FoodItem> foods;
    private String selectedCategory = ALL_CATEGORIES;
    private String searchQuery = "";

    private TextView tvHomeGreeting;
    private Button btnTopCart;
    private LinearLayout llCategories;
    private ArrayAdapter<FoodDataManager.FoodItem> foodAdapter;

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
        ListView list = findViewById(R.id.foodList);
        list.setEmptyView(findViewById(R.id.emptyFoodList));
        foodAdapter = new ArrayAdapter<FoodDataManager.FoodItem>(this, 0, new ArrayList<>()) {
            @Override
            public View getView(int position, View recycled, ViewGroup parent) {
                View row = recycled != null ? recycled
                        : getLayoutInflater().inflate(R.layout.row_shop_food, parent, false);
                bindFood(row, getItem(position));
                return row;
            }
        };
        list.setAdapter(foodAdapter);
        EditText edtSearch = findViewById(R.id.edtSearchFood);
        if (savedInstanceState != null) {
            selectedCategory = savedInstanceState.getString(STATE_CATEGORY, ALL_CATEGORIES);
        }

        btnTopCart.setOnClickListener(v -> startActivity(new Intent(home_activity.this, cart_activity.class)));

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = normalizeSearch(s.toString());
                if (foods != null) renderFoodList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        FoodDataManager.setupBottomNav(this, "home");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CATEGORY, selectedCategory);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = FoodDataManager.currentProfile(this);
        String name = prefs.getString(profile_activity.KEY_NAME, profile_activity.DEFAULT_NAME);
        boolean isOpen = FoodDataManager.isStoreOpen(this);
        tvHomeGreeting.setText("Xin chào, " + name);
        TextView storeStatus = findViewById(R.id.tvStoreStatus);
        storeStatus.setText(isOpen ? "Đang mở cửa" : "Quầy tạm nghỉ");
        updateCartBadge();
        foods = FoodDataManager.getAllFoods(this);
        categories.clear();
        categories.put(ALL_CATEGORIES, "Tất cả");
        for (FoodDataManager.FoodItem food : foods) {
            String category = food.category.trim();
            if (!category.isEmpty()) {
                categories.putIfAbsent(categoryKey(category), category);
            }
        }
        if (!categories.containsKey(selectedCategory)) selectedCategory = ALL_CATEGORIES;
        renderCategories();
        renderFoodList();
    }

    private static String normalizeSearch(String text) {
        return Normalizer.normalize(text.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .trim()
                .replaceAll("\\s+", " ");
    }

    private static String categoryKey(String category) {
        return category.trim().toLowerCase(Locale.ROOT);
    }

    private void updateCartBadge() {
        int count = FoodDataManager.getCartTotalCount(this);
        btnTopCart.setText(String.valueOf(count));
        btnTopCart.setContentDescription(getString(R.string.shop_cart_count, count));
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    private void renderCategories() {
        llCategories.removeAllViews();
        for (Map.Entry<String, String> category : categories.entrySet()) {
            String key = category.getKey();
            TextView chip = new TextView(this);
            chip.setText(category.getValue());
            chip.setFocusable(true);
            chip.setMinHeight(dp(48));
            chip.setGravity(Gravity.CENTER);
            chip.setTextSize(14f);
            chip.setTypeface(null, Typeface.BOLD);
            chip.setPadding(dp(18), dp(9), dp(18), dp(9));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMarginEnd(dp(10));
            chip.setLayoutParams(lp);

            boolean isSelected = key.equals(selectedCategory);
            chip.setSelected(isSelected);
            chip.setBackgroundResource(isSelected ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
            chip.setTextColor(isSelected ? Color.WHITE : Color.parseColor("#1B5E20"));

            chip.setOnClickListener(v -> {
                selectedCategory = key;
                renderCategories();
                renderFoodList();
            });
            llCategories.addView(chip);
        }
    }

    private void renderFoodList() {
        foodAdapter.setNotifyOnChange(false);
        foodAdapter.clear();
        for (FoodDataManager.FoodItem food : foods) {
            boolean matchCat = ALL_CATEGORIES.equals(selectedCategory)
                    || categoryKey(food.category).equals(selectedCategory);
            boolean matchSearch = searchQuery.isEmpty()
                    || normalizeSearch(food.name).contains(searchQuery)
                    || normalizeSearch(food.category).contains(searchQuery);

            if (matchCat && matchSearch) foodAdapter.add(food);
        }
        foodAdapter.notifyDataSetChanged();
        TextView empty = findViewById(R.id.emptyFoodList);
        empty.setText(foods.isEmpty() ? "Quầy chưa có sản phẩm." : "Không tìm thấy sản phẩm phù hợp.");
    }

    private void bindFood(View row, FoodDataManager.FoodItem food) {
        TextView name = row.findViewById(R.id.foodName);
        TextView meta = row.findViewById(R.id.foodMeta);
        TextView price = row.findViewById(R.id.foodPrice);
        ImageView photo = row.findViewById(R.id.foodPhoto);
        TextView emoji = row.findViewById(R.id.foodEmoji);
        name.setText(food.name);
        meta.setText(food.category + "  •  ⭐ " + food.rating);
        price.setText(FoodDataManager.formatCurrency(food.price));
        int fallback;
        String photoId;
        switch (food.id) {
            case "fr1": fallback = R.drawable.fruit_fr1; photoId = "5562607"; break;
            case "fr2": fallback = R.drawable.fruit_fr2; photoId = "18408842"; break;
            case "fr3": fallback = R.drawable.fruit_fr3; photoId = "15076701"; break;
            case "fr4": fallback = R.drawable.fruit_fr4; photoId = "30318205"; break;
            case "fr5": fallback = R.drawable.fruit_fr5; photoId = "5945709"; break;
            case "fr6": fallback = R.drawable.fruit_fr6; photoId = "5945789"; break;
            case "fr7": fallback = R.drawable.fruit_fr7; photoId = "38928050"; break;
            case "fr8": fallback = R.drawable.fruit_fr8; photoId = "18237485"; break;
            case "fr9": fallback = R.drawable.fruit_fr9; photoId = "7655897"; break;
            default: fallback = 0; photoId = null;
        }
        photo.setVisibility(photoId == null ? View.GONE : View.VISIBLE);
        emoji.setVisibility(photoId == null ? View.VISIBLE : View.GONE);
        if (photoId == null) {
            Glide.with(this).clear(photo);
            emoji.setText(food.emoji);
        } else {
            String url = "https://images.pexels.com/photos/" + photoId + "/pexels-photo-" + photoId
                    + ".jpeg?auto=compress&w=400&h=400&fit=crop";
            Glide.with(this).load(url).centerCrop().placeholder(fallback).error(fallback).into(photo);
        }
        row.findViewById(R.id.addFood).setOnClickListener(v -> {
            FoodDataManager.addToCart(this, food, 1);
            updateCartBadge();
            Toast.makeText(this, "Đã thêm " + food.name + " vào giỏ!", Toast.LENGTH_SHORT).show();
        });
        row.setOnClickListener(v -> showFoodDetailDialog(food));
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
                    startActivity(new Intent(home_activity.this, cart_activity.class));
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}
