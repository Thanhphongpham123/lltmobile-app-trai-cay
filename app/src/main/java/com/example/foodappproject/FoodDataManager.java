package com.example.foodappproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FoodDataManager {

    private static final String PREFS_FOOD = "FreshFruitShopPrefs_v3";
    private static final String KEY_ALL_FOODS = "all_fruits_catalog_json";
    private static final String KEY_CART = "cart_fruits_json";
    private static final String KEY_ORDERS = "orders_fruits_json";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_USERS_LIST = "registered_users_json";
    private static final String KEY_LAST_EMAIL = "last_login_email";
    private static final String KEY_LAST_PASS = "last_login_pass";

    private static final String KEY_STORE_NAME = "store_name";
    private static final String KEY_STORE_ADDRESS = "store_address";
    private static final String KEY_STORE_PHONE = "store_phone";
    private static final String KEY_DELIVERY_FEE = "store_delivery_fee";
    private static final String KEY_STORE_OPEN = "store_is_open";
    private static final String KEY_CURRENT_ROLE = "current_user_role";

    public static void setCurrentRole(Context context, String role) {
        context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_CURRENT_ROLE, role)
                .apply();
    }

    public static String getCurrentRole(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getString(KEY_CURRENT_ROLE, "NONE");
    }

    public static class FoodItem {
        public String id;
        public String name;
        public String category;
        public int price;
        public String rating;
        public String description;
        public String emoji;

        public FoodItem(String id, String name, String category, int price, String rating, String description, String emoji) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.rating = rating;
            this.description = description;
            this.emoji = emoji;
        }
    }

    public static class CartItem {
        public FoodItem food;
        public int quantity;

        public CartItem(FoodItem food, int quantity) {
            this.food = food;
            this.quantity = quantity;
        }
    }

    public static class OrderRecord {
        public String orderId;
        public String dateTime;
        public String itemsSummary;
        public int totalPrice;
        public String address;
        public String status;

        public OrderRecord(String orderId, String dateTime, String itemsSummary, int totalPrice, String address, String status) {
            this.orderId = orderId;
            this.dateTime = dateTime;
            this.itemsSummary = itemsSummary;
            this.totalPrice = totalPrice;
            this.address = address;
            this.status = status;
        }
    }

    public static String formatCurrency(int amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + "đ";
    }

    private static List<FoodItem> getDefaultFoods() {
        List<FoodItem> list = new ArrayList<>();
        list.add(new FoodItem("fr1", "Hộp Xoài Cát Hòa Lộc Cắt Sẵn (400g)", "Hộp cắt sẵn", 45000, "4.9",
                "Xoài chín tới dẻo thơm hoặc xoài keo giòn chua ngọt, gọt vỏ cắt miếng sẵn, tặng kèm hũ muối tôm Tây Ninh & mắm đường.", "🥭"));
        list.add(new FoodItem("fr2", "Hộp Dưa Hấu Không Hạt Ướp Lạnh (450g)", "Hộp cắt sẵn", 35000, "4.9",
                "Dưa hấu đỏ mọng ngọt lịm, không hạt, cắt miếng vuông vừa ăn, ướp lạnh mát sảng khoái kèm nĩa và muối ớt.", "🍉"));
        list.add(new FoodItem("fr3", "Khay Trái Cây Nhiệt Đới Mix 5 Vị (600g)", "Mix thập cẩm", 75000, "5.0",
                "Kết hợp 5 loại trái cây tươi: Xoài keo, Dưa hấu, Dứa mật, Ổi nữ hoàng và Thanh long đỏ, kèm 2 loại muối chấm.", "🍍"));
        list.add(new FoodItem("fr4", "Hộp Dâu Tây Đà Lạt & Nho Xanh (350g)", "Mix thập cẩm", 69000, "4.9",
                "Dâu tây Đà Lạt mọng đỏ chua ngọt dịu nhẹ kết hợp nho xanh không hạt giòn tan, rửa sạch bằng nước lọc RO.", "🍓"));
        list.add(new FoodItem("fr5", "Hộp Kiwi Vàng & Táo Envy Cắt Sẵn (400g)", "Mix thập cẩm", 79000, "4.8",
                "Kiwi vàng New Zealand giàu Vitamin C mix cùng táo Envy Mỹ giòn ngọt, gọt sẵn đóng hộp tiện lợi.", "🥝"));
        list.add(new FoodItem("fr6", "Hộp Ổi Hồng & Mận An Phước Giòn Ngọt", "Hộp cắt sẵn", 35000, "4.8",
                "Ổi hồng nữ hoàng bỏ ruột giòn rụm cùng mận An Phước ngọt thanh, tặng kèm muối ớt xanh chấm cực cuốn.", "🍑"));
        list.add(new FoodItem("fr7", "Tô Trái Cây Dầm Sữa Chua Hy Lạp", "Trái cây dầm", 55000, "4.9",
                "Bơ sáp, dâu tây, xoài chín, chuối và thanh long dầm cùng sữa chua Hy Lạp sánh mịn, rắc hạt granola & hạnh nhân.", "🥑"));
        list.add(new FoodItem("fr8", "Trái Cây Tô Dầm Sữa Đặc Đá Tuyết", "Trái cây dầm", 45000, "4.8",
                "Đầy ắp 7 loại trái cây tươi theo mùa dầm sữa đặc, cốt dừa béo ngậy và trân châu trắng giòn dai.", "🍧"));
        list.add(new FoodItem("fr9", "Nước Ép Cam Tươi & Dứa Mật Nguyên Chất", "Nước ép tươi", 38000, "4.9",
                "Ép lạnh 100% từ cam vàng tươi và dứa mật nguyên chất, không đường hóa học, giữ trọn dưỡng chất.", "🍊"));
        return list;
    }

    public static List<FoodItem> getAllFoods(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ALL_FOODS, "");
        if (json.isEmpty()) {
            List<FoodItem> defaults = getDefaultFoods();
            saveAllFoods(context, defaults);
            return defaults;
        }
        List<FoodItem> list = new ArrayList<>();
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                list.add(new FoodItem(
                        o.optString("id", "fr_" + i),
                        o.optString("name", "Hộp trái cây"),
                        o.optString("category", "Hộp cắt sẵn"),
                        o.optInt("price", 40000),
                        o.optString("rating", "4.9"),
                        o.optString("description", ""),
                        o.optString("emoji", "🍉")
                ));
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    public static void saveAllFoods(Context context, List<FoodItem> foods) {
        try {
            JSONArray arr = new JSONArray();
            for (FoodItem f : foods) {
                JSONObject o = new JSONObject();
                o.put("id", f.id);
                o.put("name", f.name);
                o.put("category", f.category);
                o.put("price", f.price);
                o.put("rating", f.rating);
                o.put("description", f.description);
                o.put("emoji", f.emoji);
                arr.put(o);
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_ALL_FOODS, arr.toString()).apply();
        } catch (Exception ignored) {
        }
    }

    public static void addCustomFood(Context context, String name, String category, int price, String description, String emoji) {
        List<FoodItem> foods = getAllFoods(context);
        foods.add(0, new FoodItem(
                "custom_" + System.currentTimeMillis(),
                name,
                category,
                price,
                "5.0",
                description,
                emoji == null || emoji.isEmpty() ? "🍇" : emoji
        ));
        saveAllFoods(context, foods);
    }

    public static void updateFood(Context context, String id, String newName, String newCategory, int newPrice, String newDesc, String newEmoji) {
        List<FoodItem> foods = getAllFoods(context);
        for (FoodItem f : foods) {
            if (f.id.equals(id)) {
                f.name = newName;
                f.category = newCategory;
                f.price = newPrice;
                f.description = newDesc;
                if (newEmoji != null && !newEmoji.isEmpty()) {
                    f.emoji = newEmoji;
                }
                break;
            }
        }
        saveAllFoods(context, foods);
    }

    public static void deleteFood(Context context, String id) {
        List<FoodItem> foods = getAllFoods(context);
        for (int i = 0; i < foods.size(); i++) {
            if (foods.get(i).id.equals(id)) {
                foods.remove(i);
                break;
            }
        }
        saveAllFoods(context, foods);
    }

    public static List<CartItem> getCartItems(Context context) {
        List<CartItem> cart = new ArrayList<>();
        List<FoodItem> allFoods = getAllFoods(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String cartJson = prefs.getString(KEY_CART, "");

        if (cartJson.isEmpty()) {
            if (allFoods.size() >= 3) {
                cart.add(new CartItem(allFoods.get(0), 1));
                cart.add(new CartItem(allFoods.get(2), 1));
                saveCartItems(context, cart);
            }
            return cart;
        }

        try {
            JSONArray arr = new JSONArray(cartJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String foodId = o.getString("id");
                int qty = o.getInt("qty");
                for (FoodItem f : allFoods) {
                    if (f.id.equals(foodId) && qty > 0) {
                        cart.add(new CartItem(f, qty));
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return cart;
    }

    public static void saveCartItems(Context context, List<CartItem> cart) {
        try {
            JSONArray arr = new JSONArray();
            for (CartItem item : cart) {
                if (item.quantity > 0) {
                    JSONObject o = new JSONObject();
                    o.put("id", item.food.id);
                    o.put("qty", item.quantity);
                    arr.put(o);
                }
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_CART, arr.toString()).apply();
        } catch (Exception ignored) {
        }
    }

    public static void addToCart(Context context, FoodItem food, int addQty) {
        List<CartItem> cart = getCartItems(context);
        boolean found = false;
        for (CartItem item : cart) {
            if (item.food.id.equals(food.id)) {
                item.quantity += addQty;
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(food, addQty));
        }
        saveCartItems(context, cart);
    }

    public static int getCartTotalCount(Context context) {
        int count = 0;
        for (CartItem item : getCartItems(context)) {
            count += item.quantity;
        }
        return count;
    }

    public static int getCartSubtotal(Context context) {
        int total = 0;
        for (CartItem item : getCartItems(context)) {
            total += item.food.price * item.quantity;
        }
        return total;
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_CART, "[]").apply();
    }

    public static List<OrderRecord> getOrders(Context context) {
        List<OrderRecord> orders = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_ORDERS, "");

        if (json.isEmpty()) {
            orders.add(new OrderRecord(
                    "#FR-2041",
                    "17/11/2026 - 15:20",
                    "1x Khay Trái Cây Nhiệt Đới Mix 5 Vị (600g)\n1x Tô Trái Cây Dầm Sữa Chua Hy Lạp",
                    145000,
                    "TP. Hồ Chí Minh",
                    "Đã thanh toán (QR)"
            ));
            orders.add(new OrderRecord(
                    "#FR-2035",
                    "16/11/2026 - 10:05",
                    "1x Hộp Dâu Tây Đà Lạt & Nho Xanh (350g)\n1x Hộp Xoài Cát Hòa Lộc Cắt Sẵn (400g)",
                    129000,
                    "TP. Hồ Chí Minh",
                    "Hoàn thành"
            ));
            saveOrders(context, orders);
            return orders;
        }

        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                orders.add(new OrderRecord(
                        o.optString("orderId"),
                        o.optString("dateTime"),
                        o.optString("itemsSummary"),
                        o.optInt("totalPrice"),
                        o.optString("address"),
                        o.optString("status")
                ));
            }
        } catch (Exception ignored) {
        }
        return orders;
    }

    public static void saveOrders(Context context, List<OrderRecord> orders) {
        try {
            JSONArray arr = new JSONArray();
            for (OrderRecord r : orders) {
                JSONObject o = new JSONObject();
                o.put("orderId", r.orderId);
                o.put("dateTime", r.dateTime);
                o.put("itemsSummary", r.itemsSummary);
                o.put("totalPrice", r.totalPrice);
                o.put("address", r.address);
                o.put("status", r.status);
                arr.put(o);
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_ORDERS, arr.toString()).apply();
        } catch (Exception ignored) {
        }
    }

    public static OrderRecord placeOrder(Context context, String address, String paymentStatus) {
        List<CartItem> cart = getCartItems(context);
        StringBuilder sb = new StringBuilder();
        int subtotal = 0;
        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            if (i > 0) sb.append("\n");
            sb.append(item.quantity).append("x ").append(item.food.name);
            subtotal += item.food.price * item.quantity;
        }
        int total = subtotal + getDeliveryFee(context);
        String orderId = "#FR-" + (2042 + (int) (System.currentTimeMillis() % 7900));
        String dateTime = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault()).format(new Date());

        OrderRecord newOrder = new OrderRecord(orderId, dateTime, sb.toString(), total, address, paymentStatus);
        List<OrderRecord> orders = getOrders(context);
        orders.add(0, newOrder);
        saveOrders(context, orders);
        clearCart(context);
        return newOrder;
    }

    public static void updateOrderStatus(Context context, String orderId, String newStatus) {
        List<OrderRecord> orders = getOrders(context);
        for (OrderRecord o : orders) {
            if (o.orderId.equals(orderId)) {
                o.status = newStatus;
                break;
            }
        }
        saveOrders(context, orders);
    }

    public static void deleteOrder(Context context, String orderId) {
        List<OrderRecord> orders = getOrders(context);
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).orderId.equals(orderId)) {
                orders.remove(i);
                break;
            }
        }
        saveOrders(context, orders);
    }

    public static int getTotalRevenue(Context context) {
        int sum = 0;
        for (OrderRecord o : getOrders(context)) {
            if (!o.status.contains("Đã hủy")) {
                sum += o.totalPrice;
            }
        }
        return sum;
    }

    // Quản lý thông tin cửa hàng
    public static String getStoreName(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getString(KEY_STORE_NAME, "Quán Trái Cây Cắt Sẵn FreshCut");
    }

    public static String getStoreAddress(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getString(KEY_STORE_ADDRESS, "123 Nguyễn Văn Cừ, Quận 5, TP.HCM");
    }

    public static String getStorePhone(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getString(KEY_STORE_PHONE, "0352847621");
    }

    public static int getDeliveryFee(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getInt(KEY_DELIVERY_FEE, 15000);
    }

    public static boolean isStoreOpen(Context context) {
        return context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .getBoolean(KEY_STORE_OPEN, true);
    }

    public static void saveStoreSettings(Context context, String name, String address, String phone, int deliveryFee, boolean isOpen) {
        context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_STORE_NAME, name)
                .putString(KEY_STORE_ADDRESS, address)
                .putString(KEY_STORE_PHONE, phone)
                .putInt(KEY_DELIVERY_FEE, deliveryFee)
                .putBoolean(KEY_STORE_OPEN, isOpen)
                .apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PASSWORD, "123456");
    }

    public static void setPassword(Context context, String newPassword) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_PASSWORD, newPassword)
                .putString(KEY_LAST_PASS, newPassword)
                .apply();

        // Cập nhật luôn mật khẩu của email hiện tại trong danh sách tài khoản đã đăng ký
        SharedPreferences profilePrefs = context.getSharedPreferences(profile_activity.PREFS_NAME, Context.MODE_PRIVATE);
        String currentEmail = profilePrefs.getString(profile_activity.KEY_EMAIL, "buu@foodapp.vn");
        updateUserPasswordByEmail(context, currentEmail, newPassword);
    }

    public static void registerAccount(Context context, String name, String email, String phone, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String usersJson = prefs.getString(KEY_USERS_LIST, "[]");
        try {
            JSONArray arr = new JSONArray(usersJson);
            JSONArray newArr = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject u = arr.getJSONObject(i);
                if (!u.optString("email").equalsIgnoreCase(email)) {
                    newArr.put(u);
                }
            }
            JSONObject userObj = new JSONObject();
            userObj.put("name", name);
            userObj.put("email", email);
            userObj.put("phone", phone);
            userObj.put("password", password);
            newArr.put(userObj);

            prefs.edit()
                    .putString(KEY_USERS_LIST, newArr.toString())
                    .putString(KEY_PASSWORD, password)
                    .putString(KEY_LAST_EMAIL, email)
                    .putString(KEY_LAST_PASS, password)
                    .apply();
        } catch (Exception ignored) {
        }
    }

    public static boolean authenticateCustomer(Context context, String email, String password) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String usersJson = prefs.getString(KEY_USERS_LIST, "[]");
        try {
            JSONArray arr = new JSONArray(usersJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject u = arr.getJSONObject(i);
                if (u.optString("email").equalsIgnoreCase(email)) {
                    if (u.optString("password").equals(password)) {
                        SharedPreferences profilePrefs = context.getSharedPreferences(profile_activity.PREFS_NAME, Context.MODE_PRIVATE);
                        profilePrefs.edit()
                                .putString(profile_activity.KEY_NAME, u.optString("name", "Khách hàng"))
                                .putString(profile_activity.KEY_EMAIL, u.optString("email", email))
                                .putString(profile_activity.KEY_PHONE, u.optString("phone", "0352847621"))
                                .apply();
                        prefs.edit()
                                .putString(KEY_PASSWORD, password)
                                .putString(KEY_LAST_EMAIL, email)
                                .putString(KEY_LAST_PASS, password)
                                .apply();
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // Tài khoản mặc định ban đầu
        String savedDefaultPass = getPassword(context);
        if (password.equals(savedDefaultPass) || password.equals("123456")) {
            prefs.edit()
                    .putString(KEY_LAST_EMAIL, email)
                    .putString(KEY_LAST_PASS, password)
                    .apply();
            return true;
        }
        return false;
    }

    private static void updateUserPasswordByEmail(Context context, String email, String newPass) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        String usersJson = prefs.getString(KEY_USERS_LIST, "[]");
        try {
            JSONArray arr = new JSONArray(usersJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject u = arr.getJSONObject(i);
                if (u.optString("email").equalsIgnoreCase(email)) {
                    u.put("password", newPass);
                }
            }
            prefs.edit().putString(KEY_USERS_LIST, arr.toString()).apply();
        } catch (Exception ignored) {
        }
    }

    public static String getLastLoginEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_EMAIL, "buu@foodapp.vn");
    }

    public static String getLastLoginPass(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FOOD, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LAST_PASS, "123456");
    }

    public static void setupBottomNav(Activity activity, String activeTab) {
        ImageButton navHome = activity.findViewById(R.id.navHome);
        ImageButton navLock = activity.findViewById(R.id.navLock);
        ImageButton navAdd = activity.findViewById(R.id.navAdd);
        ImageButton navMoney = activity.findViewById(R.id.navMoney);
        ImageButton navProfile = activity.findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setAlpha("home".equals(activeTab) ? 1.0f : 0.55f);
            navHome.setOnClickListener(v -> {
                if (!"home".equals(activeTab)) {
                    Intent i = new Intent(activity, home_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(i);
                    activity.overridePendingTransition(0, 0);
                }
            });
        }
        if (navLock != null) {
            navLock.setAlpha("lock".equals(activeTab) ? 1.0f : 0.55f);
            navLock.setOnClickListener(v -> {
                if (!"lock".equals(activeTab)) {
                    Intent i = new Intent(activity, change_password_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(i);
                    activity.overridePendingTransition(0, 0);
                }
            });
        }
        if (navAdd != null) {
            navAdd.setOnClickListener(v -> {
                Intent i = new Intent(activity, history_activity.class);
                activity.startActivity(i);
            });
        }
        if (navMoney != null) {
            navMoney.setAlpha("cart".equals(activeTab) ? 1.0f : 0.55f);
            navMoney.setOnClickListener(v -> {
                if (!"cart".equals(activeTab)) {
                    Intent i = new Intent(activity, cart_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(i);
                    activity.overridePendingTransition(0, 0);
                }
            });
        }
        if (navProfile != null) {
            navProfile.setAlpha("profile".equals(activeTab) ? 1.0f : 0.55f);
            navProfile.setOnClickListener(v -> {
                if (!"profile".equals(activeTab)) {
                    Intent i = new Intent(activity, profile_activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(i);
                    activity.overridePendingTransition(0, 0);
                }
            });
        }
    }
}
