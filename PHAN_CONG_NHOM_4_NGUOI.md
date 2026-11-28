# BẢNG PHÂN CÔNG NHIỆM VỤ ĐỒ ÁN LẬP TRÌNH MOBILE (NHÓM 4 THÀNH VIÊN)

Dự án: **Ứng Dụng Quán Trái Cây Cắt Sẵn (Android Java + REST API)**  
Link GitHub: https://github.com/Thanhphongpham123/lltmobile-app-trai-cay

---

## 1. BẢNG TỔNG QUAN PHÂN CHIA CÔNG VIỆC

| Thành viên | Phân hệ phụ trách | Số màn hình | Tỷ trọng |
| :--- | :--- | :---: | :---: |
| **Thành viên 1** | **Đăng nhập, Đăng ký, Phân quyền & Đổi mật khẩu** | 2 màn hình | 25% |
| **Thành viên 2** | **Trang chủ Quầy Trái cây, Tìm kiếm, Lọc danh mục & Hồ sơ cá nhân** | 3 màn hình | 25% |
| **Thành viên 3** | **Giỏ hàng, Thanh toán Mã VietQR, Mục Đã thanh toán & Lịch sử đơn** | 2 màn hình | 25% |
| **Thành viên 4** | **Quản trị Admin (Đơn hàng, CRUD Sản phẩm, Cửa hàng) & Kết nối API** | 2 màn hình + API | 25% |

---

## 2. CHI TIẾT CÔNG VIỆC, FILE CODE & API PHỤ TRÁCH CỦA TỪNG NGƯỜI

### 👤 Thành viên 1: Phân hệ Tài khoản, Phân quyền & Bảo mật (Auth & Security)

* **Các file Giao diện (XML) phụ trách:**
  * `app/src/main/res/layout/activity_login.xml`
  * `app/src/main/res/layout/activity_change_password.xml`
* **Các file Xử lý (Java) phụ trách:**
  * `app/src/main/java/com/example/foodappproject/login_activity.java`
  * `app/src/main/java/com/example/foodappproject/change_password_activity.java`
* **Chức năng chính cần nắm khi vấn đáp:**
  1. Kiểm tra thông tin đăng nhập và **phân quyền tách biệt**: Tài khoản `admin@fruit.vn` (MK: `admin123`) vào thẳng trang Quản trị Admin; tài khoản khách hàng vào trang mua trái cây (ngăn chặn khách mua hàng truy cập trang Admin).
  2. Xây dựng hộp thoại **Đăng ký tài khoản mua hàng mới** (`showRegisterDialog()`), kiểm tra hợp lệ dữ liệu đầu vào (Validation) và lưu trữ danh sách nhiều tài khoản.
  3. Tự động ghi nhớ tài khoản/mật khẩu đăng nhập gần nhất (`getLastLoginEmail()`, `getLastLoginPass()`) và chức năng **Đổi mật khẩu**.
* **API & Hàm dữ liệu phụ trách (`FoodDataManager.java` & `backend-api/server.js`):**
  * Endpoint: `POST /api/auth/login`, `POST /api/auth/register`, `PUT /api/auth/password`
  * Hàm Java: `authenticateCustomer()`, `registerAccount()`, `setPassword()`, `setCurrentRole()`, `getCurrentRole()`

---

### 👤 Thành viên 2: Phân hệ Trang chủ Thực đơn Trái cây & Hồ sơ Khách hàng (Catalog & Profile)

* **Các file Giao diện (XML) phụ trách:**
  * `app/src/main/res/layout/activity_home.xml`
  * `app/src/main/res/layout/activity_profile.xml`
  * `app/src/main/res/layout/activity_edit_profile.xml`
* **Các file Xử lý (Java) phụ trách:**
  * `app/src/main/java/com/example/foodappproject/home_activity.java`
  * `app/src/main/java/com/example/foodappproject/profile_activity.java`
  * `app/src/main/java/com/example/foodappproject/edit_profile_activity.java`
* **Chức năng chính cần nắm khi vấn đáp:**
  1. Hiển thị danh sách các hộp trái cây cắt sẵn, lọc sản phẩm theo **5 danh mục** (*Tất cả, Hộp cắt sẵn, Mix thập cẩm, Trái cây dầm, Nước ép tươi*).
  2. **Tìm kiếm món theo thời gian thực** (`TextWatcher` khi người dùng gõ từ khóa), xem hộp thoại chi tiết món ăn (`showFoodDetailDialog()`) và thêm món vào giỏ hàng (cập nhật số lượng trên nút `🛒`).
  3. Xem và chỉnh sửa **Hồ sơ khách hàng** (Họ tên, Địa chỉ nhận hàng, Email, Số điện thoại) và xử lý thanh điều hướng dưới đáy (`setupBottomNav()`).
* **API & Hàm dữ liệu phụ trách (`FoodDataManager.java` & `backend-api/server.js`):**
  * Endpoint: `GET /api/fruits`
  * Hàm Java: `getAllFoods()`, `setupBottomNav()`, lưu và đọc `UserProfilePrefs`

---

### 👤 Thành viên 3: Phân hệ Giỏ hàng, Thanh toán VietQR & Lịch sử Đơn hàng (Cart & QR Payment)

* **Các file Giao diện (XML) & Tài nguyên phụ trách:**
  * `app/src/main/res/layout/activity_cart.xml`
  * `app/src/main/res/layout/activity_history.xml`
  * `app/src/main/res/drawable/qr_payment.png`
* **Các file Xử lý (Java) phụ trách:**
  * `app/src/main/java/com/example/foodappproject/cart_activity.java`
  * `app/src/main/java/com/example/foodappproject/history_activity.java`
* **Chức năng chính cần nắm khi vấn đáp:**
  1. Quản lý giỏ hàng: Tăng/giảm số lượng từng hộp trái cây (`+` / `-`), tự động xóa món khi giảm số lượng về `0`, tính tổng tiền tạm tính cộng phí giao hàng lạnh.
  2. Chọn phương thức thanh toán: Hiển thị bảng **Quét Mã VietQR (MB Bank)** (`showQrPaymentDialog()`) hoặc Thanh toán tiền mặt khi nhận hàng (COD).
  3. Tạo đơn hàng mới, tự động chuyển sang tab **✅ Đã thanh toán** (`renderPaidOrders()`) và hiển thị màn hình **Lịch sử đơn hàng** của khách.
* **API & Hàm dữ liệu phụ trách (`FoodDataManager.java` & `backend-api/server.js`):**
  * Endpoint: `GET /api/orders`, `POST /api/orders`
  * Hàm Java: `getCartItems()`, `addToCart()`, `saveCartItems()`, `getCartSubtotal()`, `placeOrder()`, `getOrders()`

---

### 👤 Thành viên 4: Phân hệ Quản trị Admin (Đơn hàng, CRUD Sản phẩm, Cửa hàng) & REST API

* **Các file Giao diện (XML) phụ trách:**
  * `app/src/main/res/layout/activity_admin.xml`
  * `app/src/main/res/layout/activity_add_food.xml`
* **Các file Xử lý (Java) & Backend Server phụ trách:**
  * `app/src/main/java/com/example/foodappproject/admin_activity.java`
  * `app/src/main/java/com/example/foodappproject/add_food_activity.java`
  * `app/src/main/java/com/example/foodappproject/ApiClient.java`
  * `backend-api/server.js` & `backend-api/db.json`
* **Chức năng chính cần nắm khi vấn đáp:**
  1. Bảng thống kê Dashboard Admin: Tự động tính **Tổng doanh thu**, **Tổng số đơn hàng** và **Tổng số sản phẩm**.
  2. **Tab 1 — Quản lý đơn hàng (`renderOrdersManagement()`):** Duyệt đơn giao lạnh, xác nhận hoàn thành đơn, hủy đơn hoặc xóa đơn.
  3. **Tab 2 — Quản lý sản phẩm CRUD (`renderProductsManagement()`):** Thêm hộp trái cây mới, chỉnh sửa tên/giá/mô tả/icon sản phẩm (`showEditProductDialog()`), xóa sản phẩm khỏi quầy.
  4. **Tab 3 — Cấu hình cửa hàng (`renderStoreSettings()`):** Cập nhật tên quán, địa chỉ, hotline, phí ship lạnh và trạng thái đóng/mở cửa.
* **API & Hàm dữ liệu phụ trách (`FoodDataManager.java` & `backend-api/server.js`):**
  * Endpoint: `POST /api/fruits`, `PUT /api/fruits/:id`, `DELETE /api/fruits/:id`, `PUT /api/orders/:id/status`, `DELETE /api/orders/:id`, `GET /api/store`, `PUT /api/store`
  * Hàm Java: `addCustomFood()`, `updateFood()`, `deleteFood()`, `updateOrderStatus()`, `deleteOrder()`, `saveStoreSettings()`, `getTotalRevenue()`
