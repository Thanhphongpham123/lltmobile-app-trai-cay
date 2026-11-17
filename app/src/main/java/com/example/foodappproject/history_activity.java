package com.example.foodappproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class history_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        LinearLayout llOrders = findViewById(R.id.llOrdersHistory);
        List<FoodDataManager.OrderRecord> orders = FoodDataManager.getOrders(this);
        int dp14 = (int) (14 * getResources().getDisplayMetrics().density);

        for (FoodDataManager.OrderRecord order : orders) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundResource(R.drawable.bg_card_white);
            card.setPadding(dp14, dp14, dp14, dp14);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.bottomMargin = dp14;
            card.setLayoutParams(lp);

            TextView tvTitle = new TextView(this);
            tvTitle.setText("🍉 Đơn " + order.orderId + "  •  " + order.status);
            tvTitle.setTextSize(16f);
            tvTitle.setTypeface(null, Typeface.BOLD);
            tvTitle.setTextColor(Color.parseColor("#1B5E20"));
            card.addView(tvTitle);

            TextView tvDate = new TextView(this);
            tvDate.setText("🕒 " + order.dateTime + "  |  📍 " + order.address);
            tvDate.setTextSize(13f);
            tvDate.setTextColor(Color.parseColor("#558B2F"));
            tvDate.setPadding(0, 6, 0, 10);
            card.addView(tvDate);

            TextView tvItems = new TextView(this);
            tvItems.setText(order.itemsSummary);
            tvItems.setTextSize(15f);
            tvItems.setTextColor(Color.parseColor("#1B5E20"));
            card.addView(tvItems);

            TextView tvTotal = new TextView(this);
            tvTotal.setText("Tổng thanh toán: " + FoodDataManager.formatCurrency(order.totalPrice));
            tvTotal.setTextSize(16f);
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(Color.parseColor("#E65100"));
            tvTotal.setPadding(0, 10, 0, 0);
            card.addView(tvTotal);

            llOrders.addView(card);
        }
    }
}
