package com.example.foodappproject;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class cart_activity extends AppCompatActivity {

    private boolean showingPaidTab = false;

    private Button btnTabCart;
    private Button btnTabPaid;
    private LinearLayout addressCard;
    private LinearLayout checkoutSummaryCard;
    private TextView tvDeliveryAddress;
    private TextView tvCartTotal;
    private LinearLayout llCartItems;
    private RadioButton rbQrPayment;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnTabCart = findViewById(R.id.btnTabCart);
        btnTabPaid = findViewById(R.id.btnTabPaid);
        addressCard = findViewById(R.id.addressCard);
        checkoutSummaryCard = findViewById(R.id.checkoutSummaryCard);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        llCartItems = findViewById(R.id.llCartItems);
        rbQrPayment = findViewById(R.id.rbQrPayment);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnCheckout = findViewById(R.id.btnCheckout);

        btnTabCart.setOnClickListener(v -> switchTab(false));
        btnTabPaid.setOnClickListener(v -> switchTab(true));

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbQrPayment) {
                btnCheckout.setText("📱 Hiện Mã QR Để Thanh Toán");
            } else {
                btnCheckout.setText("💵 Đặt Hàng Ngay (Thanh Toán COD)");
            }
        });

        btnCheckout.setOnClickListener(v -> {
            List<FoodDataManager.CartItem> items = FoodDataManager.getCartItems(this);
            if (items.isEmpty()) {
                Toast.makeText(this, "Giỏ trái cây đang trống! Hãy chọn hộp trái cây trước nhé.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (rbQrPayment.isChecked()) {
                showQrPaymentDialog();
            } else {
                completeOrder("Thanh toán khi nhận (COD)");
            }
        });

        FoodDataManager.setupBottomNav(this, "cart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = FoodDataManager.currentProfile(this);
        String name = prefs.getString(profile_activity.KEY_NAME, "Pham Ngoc Thanh Phong");
        String phone = prefs.getString(profile_activity.KEY_PHONE, "0352847621");
        String address = prefs.getString(profile_activity.KEY_ADDRESS, "TP. Hồ Chí Minh");
        tvDeliveryAddress.setText(name + " • " + phone + "\n" + address);
        refreshScreen();
    }

    private void switchTab(boolean showPaid) {
        showingPaidTab = showPaid;
        refreshScreen();
    }

    private void refreshScreen() {
        int cartCount = FoodDataManager.getCartTotalCount(this);
        List<FoodDataManager.OrderRecord> orders = FoodDataManager.getOrders(this);

        btnTabCart.setText("🛒 Giỏ hàng (" + cartCount + ")");
        btnTabPaid.setText("✅ Đã thanh toán (" + orders.size() + ")");

        if (!showingPaidTab) {
            btnTabCart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
            btnTabCart.setTextColor(Color.WHITE);
            btnTabPaid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
            btnTabPaid.setTextColor(Color.parseColor("#1B5E20"));

            addressCard.setVisibility(View.VISIBLE);
            checkoutSummaryCard.setVisibility(View.VISIBLE);
            renderCartItems();
        } else {
            btnTabPaid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
            btnTabPaid.setTextColor(Color.WHITE);
            btnTabCart.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
            btnTabCart.setTextColor(Color.parseColor("#1B5E20"));

            addressCard.setVisibility(View.GONE);
            checkoutSummaryCard.setVisibility(View.GONE);
            renderPaidOrders(orders);
        }
    }

    private void showQrPaymentDialog() {
        int totalAmount = FoodDataManager.getCartSubtotal(this) + FoodDataManager.getDeliveryFee(this);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setBackgroundColor(Color.WHITE);
        container.setPadding(dp(20), dp(16), dp(20), dp(10));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("QUÉT MÃ VIETQR ĐỂ THANH TOÁN");
        tvTitle.setTextSize(17f);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextColor(Color.parseColor("#1B5E20"));
        tvTitle.setGravity(Gravity.CENTER);
        container.addView(tvTitle);

        TextView tvBank = new TextView(this);
        tvBank.setText("Chủ TK: PHAM NGOC THANH PHONG\nSố TK: 0352847621 (MB Bank)");
        tvBank.setTextSize(14f);
        tvBank.setTextColor(Color.parseColor("#33691E"));
        tvBank.setGravity(Gravity.CENTER);
        tvBank.setPadding(0, dp(6), 0, dp(10));
        container.addView(tvBank);

        ImageView imgQr = new ImageView(this);
        imgQr.setImageResource(R.drawable.qr_payment);
        imgQr.setAdjustViewBounds(true);
        imgQr.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams qrLp = new LinearLayout.LayoutParams(dp(260), dp(300));
        imgQr.setLayoutParams(qrLp);
        container.addView(imgQr);

        TextView tvAmount = new TextView(this);
        tvAmount.setText("Số tiền thanh toán: " + FoodDataManager.formatCurrency(totalAmount));
        tvAmount.setTextSize(18f);
        tvAmount.setTypeface(null, Typeface.BOLD);
        tvAmount.setTextColor(Color.parseColor("#E65100"));
        tvAmount.setGravity(Gravity.CENTER);
        tvAmount.setPadding(0, dp(10), 0, dp(4));
        container.addView(tvAmount);

        TextView tvNote = new TextView(this);
        tvNote.setText("Nội dung CK: THANH TOAN TRAI CAY CAT SAN");
        tvNote.setTextSize(13f);
        tvNote.setTextColor(Color.parseColor("#558B2F"));
        tvNote.setGravity(Gravity.CENTER);
        container.addView(tvNote);

        scrollView.addView(container);

        new AlertDialog.Builder(this)
                .setView(scrollView)
                .setCancelable(true)
                .setPositiveButton("✅ Đã Thanh Toán Thành Công", (dialog, which) -> {
                    completeOrder("Đã thanh toán (QR)");
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void completeOrder(String paymentStatus) {
        SharedPreferences prefs = FoodDataManager.currentProfile(this);
        String address = prefs.getString(profile_activity.KEY_ADDRESS, "TP. Hồ Chí Minh");
        FoodDataManager.OrderRecord order = FoodDataManager.placeOrder(this, address, paymentStatus);

        Toast.makeText(this, "🎉 Thanh toán thành công đơn " + order.orderId + "!", Toast.LENGTH_LONG).show();

        // Tự động chuyển sang mục "Đã thanh toán" để hiển thị ngay đơn vừa thanh toán
        switchTab(true);
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density);
    }

    private void renderCartItems() {
        llCartItems.removeAllViews();
        List<FoodDataManager.CartItem> cart = FoodDataManager.getCartItems(this);

        if (cart.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("🍉 Giỏ trái cây của bạn đang trống.\nHãy qua Trang chủ chọn hộp trái cây tươi hoặc bấm sang mục '✅ Đã thanh toán' ở trên để xem đơn hàng nhé!");
            empty.setTextSize(15.5f);
            empty.setTextColor(Color.parseColor("#33691E"));
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(dp(20), dp(40), dp(20), dp(20));
            llCartItems.addView(empty);
            tvCartTotal.setText("0đ");
            return;
        }

        for (int i = 0; i < cart.size(); i++) {
            final int index = i;
            FoodDataManager.CartItem item = cart.get(i);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.bg_card_white);
            row.setPadding(dp(12), dp(12), dp(12), dp(12));

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rowLp.bottomMargin = dp(10);
            row.setLayoutParams(rowLp);

            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(item.food.emoji);
            tvEmoji.setTextSize(28f);
            tvEmoji.setGravity(Gravity.CENTER);
            tvEmoji.setBackgroundResource(R.drawable.bg_food_thumb);
            LinearLayout.LayoutParams emojiLp = new LinearLayout.LayoutParams(dp(54), dp(54));
            emojiLp.setMarginEnd(dp(12));
            tvEmoji.setLayoutParams(emojiLp);
            row.addView(tvEmoji);

            LinearLayout infoCol = new LinearLayout(this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvName = new TextView(this);
            tvName.setText(item.food.name);
            tvName.setTextSize(15f);
            tvName.setTypeface(null, Typeface.BOLD);
            tvName.setTextColor(Color.parseColor("#1B5E20"));
            infoCol.addView(tvName);

            TextView tvPrice = new TextView(this);
            tvPrice.setText(FoodDataManager.formatCurrency(item.food.price * item.quantity));
            tvPrice.setTextSize(15f);
            tvPrice.setTypeface(null, Typeface.BOLD);
            tvPrice.setTextColor(Color.parseColor("#E65100"));
            tvPrice.setPadding(0, dp(4), 0, 0);
            infoCol.addView(tvPrice);

            row.addView(infoCol);

            Button btnMinus = new Button(this);
            btnMinus.setText("-");
            btnMinus.setTextColor(Color.WHITE);
            btnMinus.setBackgroundResource(R.drawable.bg_chip_selected);
            btnMinus.setLayoutParams(new LinearLayout.LayoutParams(dp(38), dp(38)));
            btnMinus.setOnClickListener(v -> {
                item.quantity--;
                if (item.quantity <= 0) {
                    cart.remove(index);
                }
                FoodDataManager.saveCartItems(this, cart);
                refreshScreen();
            });
            row.addView(btnMinus);

            TextView tvQty = new TextView(this);
            tvQty.setText(String.valueOf(item.quantity));
            tvQty.setTextSize(16f);
            tvQty.setTypeface(null, Typeface.BOLD);
            tvQty.setTextColor(Color.parseColor("#1B5E20"));
            tvQty.setGravity(Gravity.CENTER);
            tvQty.setLayoutParams(new LinearLayout.LayoutParams(dp(32), ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(tvQty);

            Button btnPlus = new Button(this);
            btnPlus.setText("+");
            btnPlus.setTextColor(Color.WHITE);
            btnPlus.setBackgroundResource(R.drawable.bg_chip_selected);
            btnPlus.setLayoutParams(new LinearLayout.LayoutParams(dp(38), dp(38)));
            btnPlus.setOnClickListener(v -> {
                item.quantity++;
                FoodDataManager.saveCartItems(this, cart);
                refreshScreen();
            });
            row.addView(btnPlus);

            llCartItems.addView(row);
        }

        int subtotal = FoodDataManager.getCartSubtotal(this);
        tvCartTotal.setText(FoodDataManager.formatCurrency(subtotal + FoodDataManager.getDeliveryFee(this)));
    }

    private void renderPaidOrders(List<FoodDataManager.OrderRecord> orders) {
        llCartItems.removeAllViews();

        TextView banner = new TextView(this);
        banner.setText("📋 DANH SÁCH ĐƠN HÀNG ĐÃ THANH TOÁN");
        banner.setTextSize(14f);
        banner.setTypeface(null, Typeface.BOLD);
        banner.setTextColor(Color.parseColor("#1B5E20"));
        banner.setPadding(dp(4), dp(4), dp(4), dp(10));
        llCartItems.addView(banner);

        for (FoodDataManager.OrderRecord order : orders) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card_white);
            card.setPadding(dp(14), dp(14), dp(14), dp(14));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.bottomMargin = dp(12);
            card.setLayoutParams(lp);

            // Dòng tiêu đề + Badge ĐÃ THANH TOÁN
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvOrderId = new TextView(this);
            tvOrderId.setText("🍉 Đơn " + order.orderId);
            tvOrderId.setTextSize(16f);
            tvOrderId.setTypeface(null, Typeface.BOLD);
            tvOrderId.setTextColor(Color.parseColor("#1B5E20"));
            tvOrderId.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            topRow.addView(tvOrderId);

            TextView tvBadge = new TextView(this);
            tvBadge.setText("✅ " + order.status);
            tvBadge.setTextSize(12.5f);
            tvBadge.setTypeface(null, Typeface.BOLD);
            tvBadge.setTextColor(Color.WHITE);
            tvBadge.setBackgroundResource(R.drawable.bg_chip_selected);
            tvBadge.setPadding(dp(10), dp(4), dp(10), dp(4));
            topRow.addView(tvBadge);

            card.addView(topRow);

            TextView tvDate = new TextView(this);
            tvDate.setText("🕒 " + order.dateTime + "  |  📍 " + order.address);
            tvDate.setTextSize(13f);
            tvDate.setTextColor(Color.parseColor("#558B2F"));
            tvDate.setPadding(0, dp(6), 0, dp(8));
            card.addView(tvDate);

            TextView tvItems = new TextView(this);
            tvItems.setText(order.itemsSummary);
            tvItems.setTextSize(14.5f);
            tvItems.setTextColor(Color.parseColor("#1B5E20"));
            card.addView(tvItems);

            TextView tvTotal = new TextView(this);
            tvTotal.setText("Đã thanh toán: " + FoodDataManager.formatCurrency(order.totalPrice));
            tvTotal.setTextSize(16f);
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(Color.parseColor("#E65100"));
            tvTotal.setPadding(0, dp(8), 0, 0);
            card.addView(tvTotal);

            llCartItems.addView(card);
        }
    }
}
