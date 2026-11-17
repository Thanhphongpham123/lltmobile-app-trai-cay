package com.example.foodappproject;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class admin_activity extends AppCompatActivity {

    private int activeTab = 0; // 0 = Orders, 1 = Products, 2 = Store

    private TextView tvAdminStoreSubtitle;
    private TextView tvAdminRevenue;
    private TextView tvAdminOrdersCount;
    private TextView tvAdminProductsCount;

    private Button btnAdminTabOrders;
    private Button btnAdminTabProducts;
    private Button btnAdminTabStore;
    private LinearLayout llAdminContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvAdminStoreSubtitle = findViewById(R.id.tvAdminStoreSubtitle);
        tvAdminRevenue = findViewById(R.id.tvAdminRevenue);
        tvAdminOrdersCount = findViewById(R.id.tvAdminOrdersCount);
        tvAdminProductsCount = findViewById(R.id.tvAdminProductsCount);

        btnAdminTabOrders = findViewById(R.id.btnAdminTabOrders);
        btnAdminTabProducts = findViewById(R.id.btnAdminTabProducts);
        btnAdminTabStore = findViewById(R.id.btnAdminTabStore);
        llAdminContent = findViewById(R.id.llAdminContent);

        if (!"ADMIN".equals(FoodDataManager.getCurrentRole(this))) {
            Toast.makeText(this, "Chỉ tài khoản Admin mới được phép truy cập!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(admin_activity.this, login_activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        Button btnSwitchToCustomer = findViewById(R.id.btnSwitchToCustomer);
        btnSwitchToCustomer.setOnClickListener(v -> {
            FoodDataManager.setCurrentRole(this, "NONE");
            Toast.makeText(this, "Đã đăng xuất tài khoản Admin", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(admin_activity.this, login_activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        btnAdminTabOrders.setOnClickListener(v -> selectTab(0));
        btnAdminTabProducts.setOnClickListener(v -> selectTab(1));
        btnAdminTabStore.setOnClickListener(v -> selectTab(2));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDashboard();
    }

    private void selectTab(int tabIndex) {
        activeTab = tabIndex;
        refreshDashboard();
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    private void refreshDashboard() {
        List<FoodDataManager.OrderRecord> orders = FoodDataManager.getOrders(this);
        List<FoodDataManager.FoodItem> foods = FoodDataManager.getAllFoods(this);
        int revenue = FoodDataManager.getTotalRevenue(this);

        String storeName = FoodDataManager.getStoreName(this);
        boolean isOpen = FoodDataManager.isStoreOpen(this);
        tvAdminStoreSubtitle.setText(storeName + " • " + (isOpen ? "🟢 Đang mở cửa" : "🔴 Đang đóng cửa"));

        tvAdminRevenue.setText(FoodDataManager.formatCurrency(revenue));
        tvAdminOrdersCount.setText(orders.size() + " đơn");
        tvAdminProductsCount.setText(foods.size() + " món");

        styleTabButton(btnAdminTabOrders, activeTab == 0);
        styleTabButton(btnAdminTabProducts, activeTab == 1);
        styleTabButton(btnAdminTabStore, activeTab == 2);

        if (activeTab == 0) {
            renderOrdersManagement(orders);
        } else if (activeTab == 1) {
            renderProductsManagement(foods);
        } else {
            renderStoreSettings();
        }
    }

    private void styleTabButton(Button btn, boolean selected) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(selected ? "#2E7D32" : "#C8E6C9")));
        btn.setTextColor(selected ? Color.WHITE : Color.parseColor("#1B5E20"));
    }

    // ==================== TAB 1: QUẢN LÝ ĐƠN HÀNG ====================
    private void renderOrdersManagement(List<FoodDataManager.OrderRecord> orders) {
        llAdminContent.removeAllViews();

        TextView header = new TextView(this);
        header.setText("📋 DANH SÁCH ĐƠN ĐẶT HÀNG (" + orders.size() + ")");
        header.setTextSize(14f);
        header.setTypeface(null, Typeface.BOLD);
        header.setTextColor(Color.parseColor("#1B5E20"));
        header.setPadding(dp(4), dp(2), dp(4), dp(8));
        llAdminContent.addView(header);

        if (orders.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("Chưa có đơn hàng nào.");
            empty.setTextSize(15f);
            empty.setTextColor(Color.parseColor("#33691E"));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(20), dp(40), dp(20), dp(20));
            llAdminContent.addView(empty);
            return;
        }

        for (FoodDataManager.OrderRecord order : orders) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card_white);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardLp.bottomMargin = dp(12);
            card.setLayoutParams(cardLp);

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvId = new TextView(this);
            tvId.setText("Đơn " + order.orderId);
            tvId.setTextSize(16.5f);
            tvId.setTypeface(null, Typeface.BOLD);
            tvId.setTextColor(Color.parseColor("#1B5E20"));
            tvId.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            topRow.addView(tvId);

            TextView tvStatus = new TextView(this);
            tvStatus.setText(order.status);
            tvStatus.setTextSize(12f);
            tvStatus.setTypeface(null, Typeface.BOLD);
            tvStatus.setTextColor(Color.WHITE);
            tvStatus.setBackgroundResource(R.drawable.bg_chip_selected);
            tvStatus.setPadding(dp(10), dp(4), dp(10), dp(4));
            topRow.addView(tvStatus);

            card.addView(topRow);

            TextView tvMeta = new TextView(this);
            tvMeta.setText("🕒 " + order.dateTime + "\n📍 Giao tới: " + order.address);
            tvMeta.setTextSize(13f);
            tvMeta.setTextColor(Color.parseColor("#558B2F"));
            tvMeta.setPadding(0, dp(6), 0, dp(6));
            card.addView(tvMeta);

            TextView tvItems = new TextView(this);
            tvItems.setText(order.itemsSummary);
            tvItems.setTextSize(14.5f);
            tvItems.setTextColor(Color.parseColor("#1B5E20"));
            card.addView(tvItems);

            TextView tvTotal = new TextView(this);
            tvTotal.setText("Tổng thu: " + FoodDataManager.formatCurrency(order.totalPrice));
            tvTotal.setTextSize(16f);
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(Color.parseColor("#E65100"));
            tvTotal.setPadding(0, dp(8), 0, dp(10));
            card.addView(tvTotal);

            // Hàng nút thao tác của Admin cho từng đơn hàng
            LinearLayout actionRow = new LinearLayout(this);
            actionRow.setOrientation(LinearLayout.HORIZONTAL);

            Button btnApprove = createActionButton("🚚 Giao lạnh", "#0288D1");
            btnApprove.setOnClickListener(v -> {
                FoodDataManager.updateOrderStatus(this, order.orderId, "Đang giao lạnh");
                Toast.makeText(this, "Đã chuyển đơn " + order.orderId + " sang Đang giao lạnh", Toast.LENGTH_SHORT).show();
                refreshDashboard();
            });
            actionRow.addView(btnApprove);

            Button btnComplete = createActionButton("✅ Hoàn thành", "#2E7D32");
            btnComplete.setOnClickListener(v -> {
                FoodDataManager.updateOrderStatus(this, order.orderId, "Đã thanh toán & Hoàn thành");
                Toast.makeText(this, "Đã hoàn thành đơn " + order.orderId, Toast.LENGTH_SHORT).show();
                refreshDashboard();
            });
            actionRow.addView(btnComplete);

            Button btnCancel = createActionButton("❌ Hủy", "#D32F2F");
            btnCancel.setOnClickListener(v -> {
                FoodDataManager.updateOrderStatus(this, order.orderId, "Đã hủy");
                Toast.makeText(this, "Đã hủy đơn " + order.orderId, Toast.LENGTH_SHORT).show();
                refreshDashboard();
            });
            actionRow.addView(btnCancel);

            Button btnDelete = createActionButton("🗑️", "#757575");
            LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(dp(44), dp(36));
            btnDelete.setLayoutParams(delLp);
            btnDelete.setOnClickListener(v -> {
                FoodDataManager.deleteOrder(this, order.orderId);
                Toast.makeText(this, "Đã xóa đơn " + order.orderId, Toast.LENGTH_SHORT).show();
                refreshDashboard();
            });
            actionRow.addView(btnDelete);

            card.addView(actionRow);
            llAdminContent.addView(card);
        }
    }

    private Button createActionButton(String text, String hexColor) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextSize(11.5f);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
        btn.setPadding(dp(4), 0, dp(4), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(36), 1f);
        lp.setMarginEnd(dp(4));
        btn.setLayoutParams(lp);
        return btn;
    }

    // ==================== TAB 2: QUẢN LÝ SẢN PHẨM ====================
    private void renderProductsManagement(List<FoodDataManager.FoodItem> foods) {
        llAdminContent.removeAllViews();

        Button btnAddNew = new Button(this);
        btnAddNew.setText("➕ Thêm Hộp Trái Cây Mới Vào Cửa Hàng");
        btnAddNew.setAllCaps(false);
        btnAddNew.setTextSize(15f);
        btnAddNew.setTypeface(null, Typeface.BOLD);
        btnAddNew.setTextColor(Color.WHITE);
        btnAddNew.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E65100")));
        LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        addLp.bottomMargin = dp(12);
        btnAddNew.setLayoutParams(addLp);
        btnAddNew.setOnClickListener(v -> startActivity(new Intent(admin_activity.this, add_food_activity.class)));
        llAdminContent.addView(btnAddNew);

        for (FoodDataManager.FoodItem food : foods) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setGravity(Gravity.CENTER_VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card_white);
            card.setPadding(dp(12), dp(12), dp(12), dp(12));

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardLp.bottomMargin = dp(10);
            card.setLayoutParams(cardLp);

            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(food.emoji);
            tvEmoji.setTextSize(28f);
            tvEmoji.setGravity(Gravity.CENTER);
            tvEmoji.setBackgroundResource(R.drawable.bg_food_thumb);
            LinearLayout.LayoutParams emojiLp = new LinearLayout.LayoutParams(dp(56), dp(56));
            emojiLp.setMarginEnd(dp(12));
            tvEmoji.setLayoutParams(emojiLp);
            card.addView(tvEmoji);

            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(food.name);
            tvName.setTextSize(15f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(Color.parseColor("#1B5E20"));
            infoCol.addView(tvName);

            TextView tvCatPrice = new TextView(this);
            tvCatPrice.setText(food.category + "  •  " + FoodDataManager.formatCurrency(food.price));
            tvCatPrice.setTextSize(13.5f);
            tvCatPrice.setTypeface(null, Typeface.BOLD);
            tvCatPrice.setTextColor(Color.parseColor("#E65100"));
            tvCatPrice.setPadding(0, dp(3), 0, 0);
            infoCol.addView(tvCatPrice);

            card.addView(infoCol);

            Button btnEdit = new Button(this);
            btnEdit.setText("✏️ Sửa");
            btnEdit.setAllCaps(false);
            btnEdit.setTextSize(12f);
            btnEdit.setTextColor(Color.WHITE);
            btnEdit.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
            LinearLayout.LayoutParams editLp = new LinearLayout.LayoutParams(dp(68), dp(38));
            editLp.setMarginEnd(dp(4));
            btnEdit.setLayoutParams(editLp);
            btnEdit.setOnClickListener(v -> showEditProductDialog(food));
            card.addView(btnEdit);

            Button btnDel = new Button(this);
            btnDel.setText("🗑️");
            btnDel.setTextSize(12f);
            btnDel.setTextColor(Color.WHITE);
            btnDel.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D32F2F")));
            btnDel.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(38)));
            btnDel.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn muốn xóa '" + food.name + "' khỏi cửa hàng?")
                        .setPositiveButton("Xóa", (d, w) -> {
                            FoodDataManager.deleteFood(this, food.id);
                            Toast.makeText(this, "Đã xóa " + food.name, Toast.LENGTH_SHORT).show();
                            refreshDashboard();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            });
            card.addView(btnDel);

            llAdminContent.addView(card);
        }
    }

    private void showEditProductDialog(FoodDataManager.FoodItem food) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(12), dp(20), 0);

        EditText edtEmoji = new EditText(this);
        edtEmoji.setHint("Biểu tượng (VD: 🍉, 🥭, 🍓)");
        edtEmoji.setText(food.emoji);
        layout.addView(edtEmoji);

        EditText edtName = new EditText(this);
        edtName.setHint("Tên sản phẩm");
        edtName.setText(food.name);
        layout.addView(edtName);

        EditText edtCat = new EditText(this);
        edtCat.setHint("Danh mục (Hộp cắt sẵn / Mix thập cẩm / Trái cây dầm / Nước ép tươi)");
        edtCat.setText(food.category);
        layout.addView(edtCat);

        EditText edtPrice = new EditText(this);
        edtPrice.setHint("Giá bán (VNĐ)");
        edtPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtPrice.setText(String.valueOf(food.price));
        layout.addView(edtPrice);

        EditText edtDesc = new EditText(this);
        edtDesc.setHint("Mô tả");
        edtDesc.setText(food.description);
        layout.addView(edtDesc);

        new AlertDialog.Builder(this)
                .setTitle("✏️ Chỉnh Sửa Sản Phẩm")
                .setView(layout)
                .setPositiveButton("Lưu Thay Đổi", (d, w) -> {
                    String name = edtName.getText().toString().trim();
                    String cat = edtCat.getText().toString().trim();
                    String desc = edtDesc.getText().toString().trim();
                    String emoji = edtEmoji.getText().toString().trim();
                    int price = food.price;
                    try {
                        price = Integer.parseInt(edtPrice.getText().toString().trim());
                    } catch (Exception ignored) {
                    }
                    if (!name.isEmpty()) {
                        FoodDataManager.updateFood(this, food.id, name, cat, price, desc, emoji);
                        Toast.makeText(this, "Đã cập nhật sản phẩm!", Toast.LENGTH_SHORT).show();
                        refreshDashboard();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ==================== TAB 3: QUẢN LÝ CỬA HÀNG ====================
    private void renderStoreSettings() {
        llAdminContent.removeAllViews();

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card_white);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        TextView title = new TextView(this);
        title.setText("🏪 CẤU HÌNH THÔNG TIN CỬA HÀNG");
        title.setTextSize(16f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#1B5E20"));
        title.setPadding(0, 0, 0, dp(12));
        card.addView(title);

        card.addView(createLabel("Tên cửa hàng:"));
        EditText edtStoreName = createInput(FoodDataManager.getStoreName(this));
        card.addView(edtStoreName);

        card.addView(createLabel("Địa chỉ cửa hàng:"));
        EditText edtStoreAddr = createInput(FoodDataManager.getStoreAddress(this));
        card.addView(edtStoreAddr);

        card.addView(createLabel("Hotline cửa hàng:"));
        EditText edtStorePhone = createInput(FoodDataManager.getStorePhone(this));
        card.addView(edtStorePhone);

        card.addView(createLabel("Phí giao hàng lạnh mặc định (VNĐ):"));
        EditText edtFee = createInput(String.valueOf(FoodDataManager.getDeliveryFee(this)));
        edtFee.setInputType(InputType.TYPE_CLASS_NUMBER);
        card.addView(edtFee);

        CheckBox cbOpen = new CheckBox(this);
        cbOpen.setText("🟢 Cửa hàng đang mở cửa nhận đơn giao lạnh");
        cbOpen.setTextSize(15f);
        cbOpen.setTypeface(null, Typeface.BOLD);
        cbOpen.setTextColor(Color.parseColor("#1B5E20"));
        cbOpen.setChecked(FoodDataManager.isStoreOpen(this));
        cbOpen.setPadding(dp(6), dp(10), 0, dp(10));
        card.addView(cbOpen);

        Button btnSaveStore = new Button(this);
        btnSaveStore.setText("💾 Lưu Cài Đặt Cửa Hàng");
        btnSaveStore.setAllCaps(false);
        btnSaveStore.setTextSize(16f);
        btnSaveStore.setTypeface(null, Typeface.BOLD);
        btnSaveStore.setTextColor(Color.WHITE);
        btnSaveStore.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
        LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
        );
        saveLp.topMargin = dp(8);
        btnSaveStore.setLayoutParams(saveLp);
        btnSaveStore.setOnClickListener(v -> {
            String name = edtStoreName.getText().toString().trim();
            String addr = edtStoreAddr.getText().toString().trim();
            String phone = edtStorePhone.getText().toString().trim();
            int fee = 15000;
            try {
                fee = Integer.parseInt(edtFee.getText().toString().trim());
            } catch (Exception ignored) {
            }
            FoodDataManager.saveStoreSettings(
                    this,
                    name.isEmpty() ? "Quán Trái Cây Cắt Sẵn" : name,
                    addr,
                    phone,
                    fee,
                    cbOpen.isChecked()
            );
            Toast.makeText(this, "Đã lưu cấu hình cửa hàng thành công!", Toast.LENGTH_SHORT).show();
            refreshDashboard();
        });
        card.addView(btnSaveStore);

        Button btnLogoutAdmin = new Button(this);
        btnLogoutAdmin.setText("🚪 Đăng Xuất Tài Khoản Admin");
        btnLogoutAdmin.setAllCaps(false);
        btnLogoutAdmin.setTextSize(15f);
        btnLogoutAdmin.setTypeface(null, Typeface.BOLD);
        btnLogoutAdmin.setTextColor(Color.WHITE);
        btnLogoutAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D32F2F")));
        LinearLayout.LayoutParams outLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        );
        outLp.topMargin = dp(10);
        btnLogoutAdmin.setLayoutParams(outLp);
        btnLogoutAdmin.setOnClickListener(v -> {
            Intent i = new Intent(admin_activity.this, login_activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        card.addView(btnLogoutAdmin);

        llAdminContent.addView(card);
    }

    private TextView createLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(13.5f);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor("#33691E"));
        tv.setPadding(0, dp(8), 0, dp(2));
        return tv;
    }

    private EditText createInput(String value) {
        EditText edt = new EditText(this);
        edt.setText(value);
        edt.setTextSize(15f);
        edt.setTextColor(Color.parseColor("#1B5E20"));
        edt.setBackgroundResource(R.drawable.bg_food_thumb);
        edt.setPadding(dp(14), dp(10), dp(14), dp(10));
        return edt;
    }
}
