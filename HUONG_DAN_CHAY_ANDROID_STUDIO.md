# HƯỚNG DẪN CHẠY PROJECT TRÊN ANDROID STUDIO & VS CODE

Dự án: **Ứng Dụng Quán Trái Cây Cắt Sẵn (Android Java + Node.js REST API)**  
Link GitHub: https://github.com/Thanhphongpham123/lltmobile-app-trai-cay

---

## PHẦN 1: CÁCH CHẠY TRÊN PHẦN MỀM ANDROID STUDIO

Khi mở project bên phần mềm **Android Studio** (trên máy cá nhân hoặc máy phòng thực hành ở trường), làm theo **4 bước** sau:

### Bước 1: Mở Project vào Android Studio
Bạn có thể chọn 1 trong 2 cách:

* **Cách A — Mở từ thư mục có sẵn trên máy:**
  1. Mở **Android Studio** -> Bấm **Open** (hoặc vào menu **File** -> **Open...**).
  2. Tìm đến đúng thư mục **`C:\mb cuoi ky\FoodAppProject`** (thư mục có biểu tượng con robot Android màu xanh lá) -> Bấm **OK**.

* **Cách B — Tải trực tiếp từ GitHub trên máy khác / máy ở trường:**
  1. Mở **Android Studio** -> Bấm **Get from VCS** (hoặc **File** -> **New** -> **Project from Version Control...**).
  2. Ở ô **URL**, dán đường dẫn GitHub:
     ```text
     https://github.com/Thanhphongpham123/lltmobile-app-trai-cay.git
     ```
  3. Bấm **Clone**.

---

### Bước 2: Đợi Android Studio đồng bộ Gradle (Gradle Sync)
* Ngay khi mở project, nhìn xuống góc dưới bên phải của Android Studio sẽ thấy thanh trạng thái **`Gradle Build Running...`** hoặc **`Syncing...`**.
* Chờ khoảng **30 giây – 1 phút** để Android Studio tải thư viện và đồng bộ xong.
* **Lưu ý nếu mở trên máy mới báo lỗi phiên bản Java (JDK):**
  1. Vào menu **File** -> **Settings** (phím tắt `Ctrl + Alt + S`).
  2. Chọn **Build, Execution, Deployment** -> **Build Tools** -> **Gradle**.
  3. Tại mục **Gradle JDK**, chọn **jbr-17** (JDK 17) hoặc **jbr-21** (JDK 21) có sẵn của Android Studio.
  4. Bấm **Apply** -> **OK**, sau đó bấm biểu tượng **Con Voi (`Sync Project with Gradle Files`)** ở góc trên bên phải.

---

### Bước 3: Chọn Máy ảo (Device Manager)
* Nhìn lên thanh công cụ phía trên cùng của Android Studio:
  * Ô cấu hình chạy chọn là **`app`**.
  * Ô bên cạnh chọn thiết bị máy ảo (ví dụ: **`Medium Phone API 36.1`**).
* **Nếu máy ở trường chưa có sẵn máy ảo:**
  1. Bấm vào biểu tượng **Device Manager** (hình chiếc điện thoại ở thanh công cụ bên phải).
  2. Bấm nút **`+` (Create Virtual Device)** -> Chọn **Phone (Pixel 6 / Medium Phone)** -> **Next** -> Chọn **System Image** đã tải sẵn trên máy -> **Finish**.

---

### Bước 4: Bấm nút Chạy (`▶ Run 'app'`)
* Bấm vào **nút hình tam giác màu xanh lá `▶` (Run 'app')** ở thanh công cụ trên cùng (hoặc nhấn phím tắt **`Shift + F10`**).
* Android Studio sẽ tự động bật máy ảo, cài đặt ứng dụng **Trái Cây Cắt Sẵn** và mở màn hình **Đăng nhập**.

---

## PHẦN 2: CÁCH CHẠY NHANH TRÊN VS CODE / ANTIGRAVITY (DÙNG FILE `.BAT`)

Nếu chạy trên máy tính cá nhân bằng **VS Code / Antigravity**:
1. Mở **Terminal** (`Ctrl + ~`).
2. Gõ lệnh:
   ```powershell
   .\run_app.bat
   ```
   File `run_app.bat` sẽ tự động khởi động **REST API Server (`port 3000`)**, biên dịch APK, bật máy ảo, cài đặt và mở ứng dụng.

---

## PHẦN 3: HỆ THỐNG BACKEND REST API (`backend-api/server.js`)

Project đi kèm **REST API Server** tại thư mục `backend-api/` và lớp kết nối HTTP `ApiClient.java` (`http://10.0.2.2:3000/api`).

* **Khởi chạy riêng API Server (nếu cần):**
  ```powershell
  node backend-api/server.js
  ```
* **Danh sách các Endpoint RESTful (`http://localhost:3000/api`):**
  * `GET    /api/health` — Kiểm tra trạng thái hoạt động của API Server
  * `POST   /api/auth/login` — Đăng nhập & phân quyền (`ADMIN` / `CUSTOMER`)
  * `POST   /api/auth/register` — Đăng ký tài khoản mới
  * `PUT    /api/auth/password` — Đổi mật khẩu tài khoản
  * `GET    /api/fruits` — Lấy danh sách toàn bộ các món trái cây cắt sẵn
  * `POST   /api/fruits` — Thêm hộp trái cây mới (Admin)
  * `PUT    /api/fruits/:id` — Cập nhật giá / thông tin hộp trái cây (Admin)
  * `DELETE /api/fruits/:id` — Xóa hộp trái cây khỏi cửa hàng (Admin)
  * `GET    /api/orders` — Lấy danh sách toàn bộ đơn hàng
  * `POST   /api/orders` — Tạo đơn đặt hàng mới (Thanh toán QR / COD)
  * `PUT    /api/orders/:id/status` — Cập nhật trạng thái đơn hàng (`Đang giao lạnh`, `Hoàn thành`, `Đã hủy`)
  * `DELETE /api/orders/:id` — Xóa đơn hàng (Admin)
  * `GET    /api/store` & `PUT /api/store` — Xem và cập nhật cấu hình cửa hàng

---

## PHẦN 4: THÔNG TIN TÀI KHOẢN ĐĂNG NHẬP TRONG ỨNG DỤNG

1. **Tài khoản Khách mua hàng (Mặc định hoặc tự Đăng ký mới):**
   * **Email:** `buu@foodapp.vn`
   * **Mật khẩu:** `123456`
   * *(Chỉ vào được giao diện mua trái cây, giỏ hàng, thanh toán VietQR, lịch sử đơn và hồ sơ cá nhân — không vào được trang Admin).*

2. **Tài khoản Quản trị viên (Admin riêng biệt):**
   * **Email:** `admin@fruit.vn` *(hoặc `admin`)*
   * **Mật khẩu:** `admin123`
   * *(Vào thẳng trang Quản trị Admin để duyệt đơn hàng, thêm/sửa/xóa sản phẩm trái cây và cấu hình cửa hàng).*
