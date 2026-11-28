const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = 3000;
const DB_FILE = path.join(__dirname, 'db.json');

// Bộ nhớ lưu các giao dịch QR đang chờ Ngân hàng MB Bank xác nhận
const pendingPayments = {};

const DEFAULT_DB = {
  store: {
    name: "Quán Trái Cây Cắt Sẵn FreshCut",
    address: "123 Nguyễn Văn Cừ, Quận 5, TP.HCM",
    phone: "0352847621",
    deliveryFee: 15000,
    isOpen: true
  },
  users: [
    {
      name: "Admin Quản Trị",
      email: "admin@fruit.vn",
      phone: "0352847621",
      password: "admin123",
      role: "ADMIN"
    },
    {
      name: "Pham Ngoc Thanh Phong",
      email: "buu@foodapp.vn",
      phone: "0352847621",
      password: "123456",
      role: "CUSTOMER"
    }
  ],
  fruits: [
    {
      id: "fr1",
      name: "Hộp Xoài Cát Hòa Lộc Cắt Sẵn (400g)",
      category: "Hộp cắt sẵn",
      price: 45000,
      rating: "4.9",
      description: "Xoài chín tới dẻo thơm hoặc xoài keo giòn chua ngọt, gọt vỏ cắt miếng sẵn, tặng kèm hũ muối tôm Tây Ninh & mắm đường.",
      emoji: "🥭"
    },
    {
      id: "fr2",
      name: "Hộp Dưa Hấu Không Hạt Ướp Lạnh (450g)",
      category: "Hộp cắt sẵn",
      price: 35000,
      rating: "4.9",
      description: "Dưa hấu đỏ mọng ngọt lịm, không hạt, cắt miếng vuông vừa ăn, ướp lạnh mát sảng khoái kèm nĩa và muối ớt.",
      emoji: "🍉"
    },
    {
      id: "fr3",
      name: "Khay Trái Cây Nhiệt Đới Mix 5 Vị (600g)",
      category: "Mix thập cẩm",
      price: 75000,
      rating: "5.0",
      description: "Kết hợp 5 loại trái cây tươi: Xoài keo, Dưa hấu, Dứa mật, Ổi nữ hoàng và Thanh long đỏ, kèm 2 loại muối chấm.",
      emoji: "🍍"
    },
    {
      id: "fr4",
      name: "Hộp Dâu Tây Đà Lạt & Nho Xanh (350g)",
      category: "Mix thập cẩm",
      price: 69000,
      rating: "4.9",
      description: "Dâu tây Đà Lạt mọng đỏ chua ngọt dịu nhẹ kết hợp nho xanh không hạt giòn tan, rửa sạch bằng nước lọc RO.",
      emoji: "🍓"
    },
    {
      id: "fr5",
      name: "Hộp Kiwi Vàng & Táo Envy Cắt Sẵn (400g)",
      category: "Mix thập cẩm",
      price: 79000,
      rating: "4.8",
      description: "Kiwi vàng New Zealand giàu Vitamin C mix cùng táo Envy Mỹ giòn ngọt, gọt sẵn đóng hộp tiện lợi.",
      emoji: "🥝"
    },
    {
      id: "fr6",
      name: "Hộp Ổi Hồng & Mận An Phước Giòn Ngọt",
      category: "Hộp cắt sẵn",
      price: 35000,
      rating: "4.8",
      description: "Ổi hồng nữ hoàng bỏ ruột giòn rụm cùng mận An Phước ngọt thanh, tặng kèm muối ớt xanh chấm cực cuốn.",
      emoji: "🍑"
    },
    {
      id: "fr7",
      name: "Tô Trái Cây Dầm Sữa Chua Hy Lạp",
      category: "Trái cây dầm",
      price: 55000,
      rating: "4.9",
      description: "Bơ sáp, dâu tây, xoài chín, chuối và thanh long dầm cùng sữa chua Hy Lạp sánh mịn, rắc hạt granola & hạnh nhân.",
      emoji: "🥑"
    },
    {
      id: "fr8",
      name: "Trái Cây Tô Dầm Sữa Đặc Đá Tuyết",
      category: "Trái cây dầm",
      price: 45000,
      rating: "4.8",
      description: "Đầy ắp 7 loại trái cây tươi theo mùa dầm sữa đặc, cốt dừa béo ngậy và trân châu trắng giòn dai.",
      emoji: "🍧"
    },
    {
      id: "fr9",
      name: "Nước Ép Cam Tươi & Dứa Mật Nguyên Chất",
      category: "Nước ép tươi",
      price: 38000,
      rating: "4.9",
      description: "Ép lạnh 100% từ cam vàng tươi và dứa mật nguyên chất, không đường hóa học, giữ trọn dưỡng chất.",
      emoji: "🍊"
    }
  ],
  orders: [
    {
      orderId: "#FR-2041",
      dateTime: "17/11/2026 - 15:20",
      itemsSummary: "1x Khay Trái Cây Nhiệt Đới Mix 5 Vị (600g)\n1x Tô Trái Cây Dầm Sữa Chua Hy Lạp",
      totalPrice: 145000,
      address: "TP. Hồ Chí Minh",
      status: "Đã thanh toán (QR)"
    },
    {
      orderId: "#FR-2035",
      dateTime: "16/11/2026 - 10:05",
      itemsSummary: "1x Hộp Dâu Tây Đà Lạt & Nho Xanh (350g)\n1x Hộp Xoài Cát Hòa Lộc Cắt Sẵn (400g)",
      totalPrice: 129000,
      address: "TP. Hồ Chí Minh",
      status: "Hoàn thành"
    }
  ]
};

function loadDb() {
  if (!fs.existsSync(DB_FILE)) {
    fs.writeFileSync(DB_FILE, JSON.stringify(DEFAULT_DB, null, 2), 'utf8');
    return JSON.parse(JSON.stringify(DEFAULT_DB));
  }
  try {
    return JSON.parse(fs.readFileSync(DB_FILE, 'utf8'));
  } catch (e) {
    return JSON.parse(JSON.stringify(DEFAULT_DB));
  }
}

function saveDb(data) {
  fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf8');
}

function sendJson(res, statusCode, payload) {
  res.writeHead(statusCode, {
    'Content-Type': 'application/json; charset=utf-8',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type'
  });
  res.end(JSON.stringify(payload, null, 2));
}

function sendHtml(res, html) {
  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(html);
}

function parseBody(req) {
  return new Promise((resolve) => {
    let body = '';
    req.on('data', chunk => { body += chunk.toString(); });
    req.on('end', () => {
      try {
        resolve(body ? JSON.parse(body) : {});
      } catch (e) {
        resolve({});
      }
    });
  });
}

// Trang Web Cổng Ngân Hàng MB Bank (http://localhost:3000/mbbank)
function renderMbBankPortalHtml() {
  return `<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>MB Bank - Xác Nhận Biến Động Số Dư VietQR</title>
  <style>
    body { font-family: 'Segoe UI', Arial, sans-serif; background: #eef2f7; margin: 0; padding: 24px; color: #1e293b; }
    .container { max-width: 720px; margin: 0 auto; }
    .header { background: linear-gradient(135deg, #1e3a8a, #2563eb); color: white; padding: 22px 26px; border-radius: 16px; box-shadow: 0 4px 14px rgba(37,99,235,0.25); }
    .header h1 { margin: 0 0 6px 0; font-size: 22px; }
    .header p { margin: 0; opacity: 0.9; font-size: 14px; }
    .account-box { background: white; border-radius: 14px; padding: 16px 20px; margin-top: 16px; display: flex; justify-content: space-between; align-items: center; border-left: 5px solid #2563eb; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
    .section-title { margin: 22px 0 12px 0; font-size: 16px; font-weight: 700; color: #1e3a8a; }
    .tx-card { background: white; border-radius: 14px; padding: 18px 20px; margin-bottom: 14px; box-shadow: 0 2px 10px rgba(0,0,0,0.07); border: 1.5px solid #cbd5e1; }
    .tx-card.paid { border-color: #22c55e; background: #f0fdf4; }
    .tx-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; }
    .tx-code { font-size: 18px; font-weight: 700; color: #1e3a8a; }
    .badge { padding: 5px 12px; border-radius: 20px; font-size: 12.5px; font-weight: 700; }
    .badge.pending { background: #fef3c7; color: #d97706; }
    .badge.paid { background: #22c55e; color: white; }
    .amount { font-size: 21px; font-weight: 800; color: #ea580c; margin: 6px 0; }
    .btn-confirm { background: #16a34a; color: white; border: none; padding: 13px 20px; font-size: 15.5px; font-weight: 700; border-radius: 10px; cursor: pointer; width: 100%; margin-top: 10px; transition: 0.2s; }
    .btn-confirm:hover { background: #15803d; }
    .empty { background: white; border-radius: 14px; padding: 36px 20px; text-align: center; color: #64748b; border: 1px dashed #cbd5e1; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>🏦 MB BANK - CỔNG XÁC NHẬN THANH TOÁN VIETQR 24/7</h1>
      <p>Khi bạn xác nhận nhận tiền tại đây, màn hình App Android của khách sẽ tự động báo "Thanh toán thành công"</p>
    </div>

    <div class="account-box">
      <div>
        <div style="font-size:13px;color:#64748b;">Tài khoản thụ hưởng VietQR</div>
        <div style="font-size:17px;font-weight:700;color:#0f172a;margin-top:2px;">PHAM NGOC THANH PHONG</div>
      </div>
      <div style="text-align:right;">
        <div style="font-size:13px;color:#64748b;">Số tài khoản MB Bank</div>
        <div style="font-size:18px;font-weight:800;color:#2563eb;margin-top:2px;">0352847621</div>
      </div>
    </div>

    <div class="section-title">📡 GIAO DỊCH QR ĐANG CHỜ XÁC NHẬN TRÊN APP ANDROID (Tự động cập nhật)</div>
    <div id="txList">Đang tải danh sách giao dịch...</div>
  </div>

  <script>
    function formatVnd(n) {
      return Number(n || 0).toLocaleString('vi-VN') + 'đ';
    }

    async function loadTransactions() {
      try {
        const res = await fetch('/api/payments');
        const json = await res.json();
        const list = json.data || [];
        const container = document.getElementById('txList');

        if (list.length === 0) {
          container.innerHTML = '<div class="empty">⏳ Hiện chưa có khách nào mở mã QR thanh toán.<br><br>Hãy bấm <b>"📱 Hiện Mã QR Để Thanh Toán"</b> trên máy ảo Android, giao dịch sẽ hiện lên tại đây ngay lập tức!</div>';
          return;
        }

        container.innerHTML = list.map(tx => {
          const isPaid = tx.status === 'PAID';
          return \`
            <div class="tx-card \${isPaid ? 'paid' : ''}">
              <div class="tx-top">
                <span class="tx-code">Nội dung CK: \${tx.txCode}</span>
                <span class="badge \${isPaid ? 'paid' : 'pending'}">\${isPaid ? '✅ ĐÃ BÁO THÀNH CÔNG VỀ APP' : '⏳ App đang chờ ngân hàng xác nhận...'}</span>
              </div>
              <div class="amount">Số tiền: +\${formatVnd(tx.amount)}</div>
              <div style="font-size:13.5px;color:#475569;margin-top:4px;">🕒 Thời gian tạo: \${tx.createdAt}</div>
              \${!isPaid ? \`
                <button class="btn-confirm" onclick="confirmPayment('\${tx.txCode}')">
                  ✅ MB BANK BÁO CÓ: XÁC NHẬN ĐÃ NHẬN +\${formatVnd(tx.amount)}
                </button>
              \` : '<div style="margin-top:8px;color:#15803d;font-weight:700;font-size:14px;">✔ Màn hình máy ảo Android đã nhận tín hiệu thành công!</div>'}
            </div>
          \`;
        }).join('');
      } catch (e) {}
    }

    async function confirmPayment(txCode) {
      await fetch('/api/payments/' + encodeURIComponent(txCode) + '/confirm', { method: 'POST' });
      loadTransactions();
    }

    loadTransactions();
    setInterval(loadTransactions, 1500);
  </script>
</body>
</html>`;
}

const server = http.createServer(async (req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;
  const method = req.method;

  if (method === 'OPTIONS') {
    return sendJson(res, 200, { status: 'ok' });
  }

  // Giao diện Web Cổng Ngân Hàng MB Bank: http://localhost:3000/mbbank
  if (method === 'GET' && (pathname === '/mbbank' || pathname === '/')) {
    return sendHtml(res, renderMbBankPortalHtml());
  }

  // API Quản lý Giao dịch Thanh toán QR thời gian thực
  if (method === 'GET' && pathname === '/api/payments') {
    const list = Object.values(pendingPayments).reverse();
    return sendJson(res, 200, { success: true, data: list });
  }

  if (method === 'POST' && pathname === '/api/payments/create') {
    const body = await parseBody(req);
    const txCode = body.txCode || ('FC' + Math.floor(1000 + Math.random() * 8999));
    const tx = {
      txCode,
      amount: Number(body.amount) || 0,
      status: 'PENDING',
      createdAt: new Date().toLocaleTimeString('vi-VN') + ' - ' + new Date().toLocaleDateString('vi-VN')
    };
    pendingPayments[txCode] = tx;
    console.log(`[MB-BANK QR] Khởi tạo chờ thanh toán: ${txCode} - ${tx.amount}đ`);
    return sendJson(res, 201, { success: true, data: tx });
  }

  if (pathname.startsWith('/api/payments/')) {
    const parts = pathname.replace('/api/payments/', '').split('/');
    const txCode = decodeURIComponent(parts[0]);
    const action = parts[1];

    // App Android gọi liên tục mỗi 1.5s để kiểm tra ngân hàng đã báo thành công chưa
    if (method === 'GET' && !action) {
      const tx = pendingPayments[txCode];
      if (!tx) return sendJson(res, 404, { success: false, status: 'NOT_FOUND' });
      return sendJson(res, 200, { success: true, status: tx.status, data: tx });
    }

    // Cổng MB Bank xác nhận đã nhận tiền
    if ((method === 'POST' || method === 'PUT') && action === 'confirm') {
      if (!pendingPayments[txCode]) {
        pendingPayments[txCode] = {
          txCode,
          amount: 0,
          status: 'PAID',
          createdAt: new Date().toLocaleTimeString('vi-VN')
        };
      } else {
        pendingPayments[txCode].status = 'PAID';
      }
      console.log(`[MB-BANK QR] ✅ ĐÃ XÁC NHẬN NHẬN TIỀN GIAO DỊCH: ${txCode}`);
      return sendJson(res, 200, { success: true, status: 'PAID', data: pendingPayments[txCode] });
    }
  }

  const db = loadDb();

  if (method === 'GET' && (pathname === '/api/health' || pathname === '/api')) {
    return sendJson(res, 200, {
      success: true,
      message: "FreshCut Fruit REST API + MB Bank VietQR Webhook đang hoạt động!",
      mbbankPortal: "http://localhost:3000/mbbank"
    });
  }

  if (method === 'POST' && pathname === '/api/auth/login') {
    const body = await parseBody(req);
    const { email, password } = body;
    if (email && (email.toLowerCase() === 'admin@fruit.vn' || email.toLowerCase() === 'admin')) {
      if (password === 'admin123') {
        return sendJson(res, 200, { success: true, role: 'ADMIN', name: 'Admin Quản Trị', email });
      }
      return sendJson(res, 401, { success: false, message: 'Sai mật khẩu Admin!' });
    }
    const user = db.users.find(u => u.email.toLowerCase() === (email || '').toLowerCase());
    if (user && user.password === password) {
      return sendJson(res, 200, { success: true, role: 'CUSTOMER', user });
    }
    return sendJson(res, 401, { success: false, message: 'Sai email hoặc mật khẩu!' });
  }

  if (method === 'POST' && pathname === '/api/auth/register') {
    const body = await parseBody(req);
    const { name, email, phone, password } = body;
    if (!email || !password) {
      return sendJson(res, 400, { success: false, message: 'Thiếu email hoặc mật khẩu' });
    }
    db.users = db.users.filter(u => u.email.toLowerCase() !== email.toLowerCase());
    const newUser = { name: name || 'Khách hàng', email, phone: phone || '0352847621', password, role: 'CUSTOMER' };
    db.users.push(newUser);
    saveDb(db);
    return sendJson(res, 201, { success: true, user: newUser });
  }

  if (method === 'PUT' && pathname === '/api/auth/password') {
    const body = await parseBody(req);
    const { email, newPassword } = body;
    db.users.forEach(u => {
      if (!email || u.email.toLowerCase() === email.toLowerCase()) {
        if (u.role !== 'ADMIN') u.password = newPassword;
      }
    });
    saveDb(db);
    return sendJson(res, 200, { success: true, message: 'Đã cập nhật mật khẩu' });
  }

  if (method === 'GET' && pathname === '/api/fruits') {
    return sendJson(res, 200, { success: true, count: db.fruits.length, data: db.fruits });
  }

  if (method === 'POST' && pathname === '/api/fruits') {
    const body = await parseBody(req);
    const newFruit = {
      id: body.id || ('custom_' + Date.now()),
      name: body.name || 'Hộp trái cây mới',
      category: body.category || 'Hộp cắt sẵn',
      price: Number(body.price) || 45000,
      rating: body.rating || '5.0',
      description: body.description || 'Trái cây tươi cắt sẵn trong ngày.',
      emoji: body.emoji || '🍇'
    };
    db.fruits.unshift(newFruit);
    saveDb(db);
    return sendJson(res, 201, { success: true, data: newFruit });
  }

  if (pathname.startsWith('/api/fruits/')) {
    const id = decodeURIComponent(pathname.replace('/api/fruits/', ''));
    if (method === 'PUT') {
      const body = await parseBody(req);
      const item = db.fruits.find(f => f.id === id);
      if (!item) return sendJson(res, 404, { success: false, message: 'Không tìm thấy sản phẩm' });
      if (body.name) item.name = body.name;
      if (body.category) item.category = body.category;
      if (body.price !== undefined) item.price = Number(body.price);
      if (body.description !== undefined) item.description = body.description;
      if (body.emoji) item.emoji = body.emoji;
      saveDb(db);
      return sendJson(res, 200, { success: true, data: item });
    }
    if (method === 'DELETE') {
      db.fruits = db.fruits.filter(f => f.id !== id);
      saveDb(db);
      return sendJson(res, 200, { success: true, message: 'Đã xóa sản phẩm ' + id });
    }
  }

  if (method === 'GET' && pathname === '/api/orders') {
    return sendJson(res, 200, { success: true, count: db.orders.length, data: db.orders });
  }

  if (method === 'POST' && pathname === '/api/orders') {
    const body = await parseBody(req);
    const newOrder = {
      orderId: body.orderId || ('#FR-' + Math.floor(2000 + Math.random() * 7900)),
      dateTime: body.dateTime || new Date().toLocaleString('vi-VN'),
      itemsSummary: body.itemsSummary || '',
      totalPrice: Number(body.totalPrice) || 0,
      address: body.address || 'TP. Hồ Chí Minh',
      status: body.status || 'Đã thanh toán (QR)'
    };
    db.orders.unshift(newOrder);
    saveDb(db);
    return sendJson(res, 201, { success: true, data: newOrder });
  }

  if (pathname.startsWith('/api/orders/')) {
    const parts = pathname.replace('/api/orders/', '').split('/');
    const orderId = decodeURIComponent(parts[0]);
    if (method === 'PUT') {
      const body = await parseBody(req);
      const ord = db.orders.find(o => o.orderId === orderId);
      if (!ord) return sendJson(res, 404, { success: false, message: 'Không tìm thấy đơn hàng' });
      if (body.status) ord.status = body.status;
      saveDb(db);
      return sendJson(res, 200, { success: true, data: ord });
    }
    if (method === 'DELETE') {
      db.orders = db.orders.filter(o => o.orderId !== orderId);
      saveDb(db);
      return sendJson(res, 200, { success: true, message: 'Đã xóa đơn hàng ' + orderId });
    }
  }

  if (pathname === '/api/store') {
    if (method === 'GET') {
      return sendJson(res, 200, { success: true, data: db.store });
    }
    if (method === 'PUT') {
      const body = await parseBody(req);
      db.store = {
        name: body.name || db.store.name,
        address: body.address || db.store.address,
        phone: body.phone || db.store.phone,
        deliveryFee: body.deliveryFee !== undefined ? Number(body.deliveryFee) : db.store.deliveryFee,
        isOpen: body.isOpen !== undefined ? Boolean(body.isOpen) : db.store.isOpen
      };
      saveDb(db);
      return sendJson(res, 200, { success: true, data: db.store });
    }
  }

  return sendJson(res, 404, { success: false, message: 'Endpoint không tồn tại: ' + pathname });
});

loadDb();
server.listen(PORT, '0.0.0.0', () => {
  console.log('====================================================');
  console.log(`  FRESHCUT FRUIT REST API SERVER RUNNING ON PORT ${PORT}`);
  console.log(`  Cong Xac Nhan MB Bank: http://localhost:${PORT}/mbbank`);
  console.log(`  Android Emulator URL:  http://10.0.2.2:${PORT}/api`);
  console.log('====================================================');
});
