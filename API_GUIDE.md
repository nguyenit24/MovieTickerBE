# API GUIDE - Movie Ticket Booking System

## Overview
Hướng dẫn sử dụng API cho hệ thống đặt vé xem phim. API được thiết kế để xử lý toàn bộ quy trình booking từ A-Z với cơ chế lock ghế 10 phút và tự động hết hạn.

## Base URL
```
http://localhost:8080
```

## Authentication
Hiện tại API chưa yêu cầu authentication. Có thể thêm JWT token trong tương lai.

## Response Format
Tất cả API responses đều có format chuẩn:
```json
{
    "code": 200,
    "message": "Success message",
    "data": {...}
}
```

---

## 🎬 BOOKING FLOW COMPLETE GUIDE

### Phase 1: Lấy thông tin cơ bản

#### 1.1 Lấy danh sách phim đang chiếu
```http
GET /api/phim
```

**Response:**
```json
{
    "code": 200,
    "message": "Lấy danh sách phim thành công",
    "data": [
        {
            "maPhim": "P001",
            "tenPhim": "Avatar 3",
            "thoiLuong": 180,
            "moTa": "Phim khoa học viễn tưởng...",
            "ngayKhoiChieu": "2024-01-15",
            "ngayKetThuc": "2024-03-15",
            "trangThai": "ACTIVE"
        }
    ]
}
```

#### 1.2 Lấy danh sách suất chiếu theo phim
```http
GET /api/suat-chieu/phim/{maPhim}
```

**Response:**
```json
{
    "code": 200,
    "message": "Success",
    "data": [
        {
            "maSuatChieu": "SC001",
            "ngayChieu": "2024-01-20",
            "gioChieu": "19:30:00",
            "phongChieu": {
                "maPhong": "PC001",
                "tenPhong": "Phòng VIP 1",
                "soLuongGhe": 50
            }
        }
    ]
}
```

#### 1.3 Lấy sơ đồ ghế theo suất chiếu
```http
GET /api/ghe/suat-chieu/{maSuatChieu}
```

**Response:**
```json
{
    "code": 200,
    "message": "Success",
    "data": [
        {
            "maGhe": "A01",
            "hang": "A",
            "so": 1,
            "loaiGhe": {
                "maLoaiGhe": "LG001",
                "tenLoaiGhe": "VIP",
                "gia": 120000
            },
            "trangThai": "AVAILABLE"  // AVAILABLE, BOOKED, PROCESSING
        }
    ]
}
```

### Phase 2: Booking Process

#### 2.1 Đặt vé (Tạo booking)
```http
POST /api/ve/booking
Content-Type: application/json
```

**Request Body:**
```json
{
    "maSuatChieu": "SC001",
    "maGheList": ["A01", "A02", "A03"],
    "dichVuDiKemList": [
        {
            "maDichVu": "DV001",
            "soLuong": 2
        }
    ]
}
```

**Response Success:**
```json
{
    "code": 201,
    "message": "Đặt vé thành công",
    "data": {
        "hoaDon": {
            "maHD": "HD20241011001",
            "tongTien": 480000,
            "ngayLap": "2024-10-11T14:30:00",
            "trangThai": "PROCESSING"
        },
        "danhSachVe": [
            {
                "maVe": "VE001",
                "ghe": {
                    "maGhe": "A01",
                    "hang": "A",
                    "so": 1
                },
                "giaVe": 120000,
                "trangThai": "PROCESSING"
            }
        ],
        "timeoutMinutes": 10,
        "expiredAt": "2024-10-11T14:40:00"
    }
}
```

**Response Error - Ghế đã được đặt:**
```json
{
    "code": 400,
    "message": "Một hoặc nhiều ghế đã được đặt và thanh toán cho suất chiếu này",
    "data": null
}
```

**Response Error - Ghế đang được giữ:**
```json
{
    "code": 400,
    "message": "Ghế đang được giữ bởi người dùng khác. Vui lòng chọn ghế khác",
    "data": null
}
```

#### 2.2 Kiểm tra trạng thái hóa đơn
```http
GET /api/payment/status/{maHD}
```

**Response:**
```json
{
    "code": 200,
    "message": "Lấy trạng thái hóa đơn thành công",
    "data": {
        "maHD": "HD20241011001",
        "trangThai": "PROCESSING",  // PROCESSING, PAID, EXPIRED, CANCELLED
        "timestamp": "2024-10-11T14:35:00"
    }
}
```

### Phase 3: Payment Process

#### 3.1 Tạo thanh toán VNPay
```http
POST /api/payment/vn_pay/create
Content-Type: application/json
```

**Request Body:**
```json
{
    "orderId": "HD20241011001",
    "amount": 480000,
    "orderInfo": "Thanh toán vé xem phim"
}
```

**Response Success:**
```json
{
    "code": 201,
    "message": "Tạo thanh toán thành công",
    "data": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=..."
}
```

**Response Error - Hóa đơn hết hạn:**
```json
{
    "code": 400,
    "message": "Tạo thanh toán thất bại: Hóa đơn đã hết hạn thanh toán (quá 10 phút). Vui lòng tạo đơn hàng mới.",
    "data": null
}
```

#### 3.2 Tạo thanh toán MoMo
```http
POST /api/payment/momo/create
Content-Type: application/json
```

**Request Body:**
```json
{
    "orderId": "HD20241011001",
    "amount": 480000,
    "orderInfo": "Thanh toán vé xem phim"
}
```

**Response:**
```json
{
    "code": 201,
    "message": "Tạo thanh toán thành công",
    "data": {
        "partnerCode": "MOMO",
        "orderId": "HD20241011001",
        "amount": 480000,
        "payUrl": "https://payment.momo.vn/...",
        "qrCodeUrl": "https://api.qrserver.com/..."
    }
}
```

#### 3.3 Callback xử lý kết quả thanh toán

**VNPay Callback:**
```http
GET /api/payment/vn_pay/payment_info?vnp_OrderInfo=...&vnp_ResponseCode=00
```

**MoMo Callback:**
```http
GET /api/payment/momo/payment_info?orderId=HD20241011001&resultCode=0
```

### Phase 4: Cancel/Timeout Handling

#### 4.1 Hủy hóa đơn manual (khi user thoát)
```http
POST /api/payment/cancel/{maHD}
```

**Response Success:**
```json
{
    "code": 200,
    "message": "Hủy hóa đơn thành công",
    "data": null
}
```

**Response Error:**
```json
{
    "code": 400,
    "message": "Hủy hóa đơn thất bại: Không thể hủy hóa đơn. Trạng thái hiện tại: PAID",
    "data": null
}
```

#### 4.2 Polling trạng thái thanh toán
```http
GET /api/payment/check-status/{maHD}
```

**Response:**
```json
{
    "code": 200,
    "message": "Kiểm tra trạng thái thành công",
    "data": {
        "maHD": "HD20241011001",
        "trangThai": "PAID",
        "paidAt": "2024-10-11T14:38:00"
    }
}
```

---

## 🕒 TIMEOUT & EXPIRY MECHANISM

### Automatic Expiry (Background Process)
- **Scheduled Task**: Chạy mỗi 2 phút
- **Timeout**: 10 phút kể từ khi tạo hóa đơn
- **Auto Update**: PROCESSING → EXPIRED
- **No Data Deletion**: Chỉ cập nhật trạng thái, không xóa data

### Status Flow
```
PROCESSING (0-10 min) → PAID (success)
PROCESSING (>10 min) → EXPIRED (auto)
PROCESSING (manual)   → CANCELLED (user action)
```

---

## 📋 REFERENCE APIS

### Phim APIs
```http
GET /api/phim                    # Danh sách phim
GET /api/phim/{maPhim}          # Chi tiết phim
GET /api/the-loai               # Danh sách thể loại
```

### Suất chiếu APIs
```http
GET /api/suat-chieu                      # Tất cả suất chiếu
GET /api/suat-chieu/phim/{maPhim}       # Suất chiếu theo phim
GET /api/suat-chieu/{maSuatChieu}       # Chi tiết suất chiếu
```

### Ghế APIs
```http
GET /api/ghe                           # Tất cả ghế
GET /api/ghe/suat-chieu/{maSuatChieu} # Ghế theo suất chiếu
GET /api/loai-ghe                      # Loại ghế và giá
```

### Dịch vụ đi kèm APIs
```http
GET /api/dich-vu-di-kem              # Danh sách dịch vụ
GET /api/dich-vu-di-kem/{maDichVu}   # Chi tiết dịch vụ
```

---

## 🚨 ERROR HANDLING

### Common Error Codes
- **400**: Bad Request - Dữ liệu không hợp lệ
- **404**: Not Found - Không tìm thấy resource
- **409**: Conflict - Ghế đã được đặt
- **410**: Gone - Hóa đơn đã hết hạn
- **500**: Internal Server Error

### Error Response Format
```json
{
    "code": 400,
    "message": "Chi tiết lỗi cụ thể",
    "data": null
}
```

---

## 🔄 BOOKING WORKFLOW EXAMPLE

### Complete Booking Flow:
```bash
# 1. Lấy danh sách phim
GET /api/phim

# 2. Lấy suất chiếu
GET /api/suat-chieu/phim/P001

# 3. Xem sơ đồ ghế
GET /api/ghe/suat-chieu/SC001

# 4. Đặt vé (lock 10 phút)
POST /api/ve/booking
{
    "maSuatChieu": "SC001",
    "maGheList": ["A01", "A02"]
}

# 5. Tạo thanh toán
POST /api/payment/vn_pay/create
{
    "orderId": "HD20241011001",
    "amount": 240000
}

# 6. User thanh toán trên VNPay...

# 7. Callback tự động cập nhật trạng thái
GET /api/payment/vn_pay/payment_info?vnp_ResponseCode=00

# 8. (Optional) Polling check status
GET /api/payment/status/HD20241011001
```

### Cancel Flow:
```bash
# User thoát không thanh toán
POST /api/payment/cancel/HD20241011001
```

### Auto Expiry:
```bash
# Sau 10 phút tự động
Status: PROCESSING → EXPIRED
```

---

## 📊 STATUS DEFINITIONS

### Invoice Status (InvoiceStatus)
- **PROCESSING**: Đang chờ thanh toán (0-10 phút)
- **PAID**: Đã thanh toán thành công
- **EXPIRED**: Hết hạn thanh toán (>10 phút)
- **CANCELLED**: Đã hủy bởi user
- **REFUNDED**: Đã hoàn tiền

### Ticket Status (TicketStatus)
- **PROCESSING**: Đang được giữ chỗ (0-10 phút)
- **PAID**: Đã thanh toán, ghế đã được đặt
- **EXPIRED**: Hết hạn, ghế được giải phóng
- **CANCELLED**: Đã hủy, ghế được giải phóng

### Seat Status (Display only)
- **AVAILABLE**: Ghế trống, có thể đặt
- **BOOKED**: Ghế đã được đặt và thanh toán
- **PROCESSING**: Ghế đang được giữ bởi user khác

---

## 🎯 BEST PRACTICES

1. **Always check seat availability** trước khi booking
2. **Handle timeout gracefully** - show countdown timer
3. **Implement polling** để check payment status
4. **Call cancel API** khi user thoát trang
5. **Show clear error messages** cho từng trường hợp
6. **Cache data appropriately** để giảm API calls
7. **Validate input** trước khi gọi API

---

*Last updated: October 11, 2025*
*API Version: 1.0*
    "ngayLap": "2025-10-11T14:30:45",
    "tongTien": 250000.0,
    "phuongThucThanhToan": "VNPAY",
    "trangThai": "PROCESSING",
    "maGiaoDich": "GD1728567890123",
    "ghiChu": "Tổng tiền vé: 200000 VND\nPhụ thu dịch vụ: 50000 VND\nGiảm giá: 0 VND\nTổng cộng: 250000 VND",
    "danhSachVe": [
      {
        "maVe": "VE-1728567890123001",
        "tenPhim": "Spider-Man: No Way Home",
        "tenPhongChieu": "Phòng A1",
        "tenGhe": "A01",
        "thoiGianChieu": "2025-10-11T20:00:00",
        "ngayDat": "2025-10-11T14:30:45",
        "thanhTien": 125000.0,
        "trangThai": "PROCESSING",
        "maHoaDon": "hd-1728567890123"
      },
      {
        "maVe": "VE-1728567890123002",
        "tenPhim": "Spider-Man: No Way Home",
        "tenPhongChieu": "Phòng A1", 
        "tenGhe": "A02",
        "thoiGianChieu": "2025-10-11T20:00:00",
        "ngayDat": "2025-10-11T14:30:45",
        "thanhTien": 125000.0,
        "trangThai": "PROCESSING",
        "maHoaDon": "hd-1728567890123"
      }
    ],
    "tenNguoiDung": null
  }
}
```

**Response Error (400):**
```json
{
  "code": 400,
  "message": "Một hoặc nhiều ghế đã được đặt và thanh toán cho suất chiếu này",
  "data": null
}
```

---

## 2. Logic Kiểm Tra Ghế

### Các trường hợp kiểm tra:
1. **Ghế đã thanh toán:** Không cho đặt
2. **Ghế đang xử lý (PROCESSING):**
   - Nếu < 10 phút: Không cho đặt
   - Nếu > 10 phút: Tự động chuyển thành EXPIRED và cho đặt mới

### Cơ chế tự động:
- Hệ thống tự động kiểm tra và cập nhật trạng thái vé EXPIRED khi có yêu cầu đặt ghế mới
- Thời gian hết hạn: 10 phút kể từ lúc đặt vé (ngayDat)

---

## 3. Quy trình sử dụng cho Frontend

### Bước 1: Đặt vé
```javascript
const bookingRequest = {
  maSuatChieu: "sc-123",
  maGheList: ["ghe-001", "ghe-002"],
  maPhim: "phim-001",
  phuongThucThanhToan: "VNPAY"
};

const response = await fetch('/api/ve', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(bookingRequest)
});

const result = await response.json();
```

### Bước 2: Hiển thị thông tin booking
```javascript
if (result.code === 200) {
  const hoaDon = result.data;
  
  // Hiển thị thông tin đơn hàng
  console.log('Mã hóa đơn:', hoaDon.maHD);
  console.log('Tổng tiền:', hoaDon.tongTien);
  console.log('Trạng thái:', hoaDon.trangThai); // "PROCESSING"
  
  // Hiển thị danh sách vé
  hoaDon.danhSachVe.forEach(ve => {
    console.log(`Vé ${ve.maVe}: ${ve.tenGhe} - ${ve.thanhTien} VND`);
  });
  
  // Chuyển hướng đến trang thanh toán
  // (Tùy thuộc vào logic thanh toán của bạn)
  redirectToPayment(hoaDon.maHD);
}
```

### Bước 3: Xử lý lỗi
```javascript
if (result.code !== 200) {
  alert(result.message);
  // Ví dụ: "Một hoặc nhiều ghế đã được đặt và thanh toán cho suất chiếu này"
}
```

---

## 4. Lưu ý quan trọng

### Thời gian hết hạn:
- Vé có trạng thái `PROCESSING` sẽ tự động chuyển thành `EXPIRED` sau 10 phút
- Frontend nên hiển thị countdown timer cho user

### Refresh seat map:
- Nên refresh seat map thường xuyên để cập nhật trạng thái ghế
- Gọi API kiểm tra trạng thái ghế theo suất chiếu

### Error handling:
- Luôn kiểm tra response code trước khi xử lý data
- Hiển thị message phù hợp cho từng loại lỗi

---

## 5. Response Models

### HoaDonResponse
```typescript
interface HoaDonResponse {
  maHD: string;
  ngayLap: string; // ISO DateTime
  tongTien: number;
  phuongThucThanhToan: string;
  trangThai: string; // "PROCESSING" | "PAID" | "EXPIRED" | "CANCELLED" | "REFUNDED"
  maGiaoDich: string;
  ghiChu: string;
  danhSachVe: VeResponse[];
  tenNguoiDung: string | null;
}
```

### VeResponse  
```typescript
interface VeResponse {
  maVe: string;
  tenPhim: string;
  tenPhongChieu: string;
  tenGhe: string;
  thoiGianChieu: string; // ISO DateTime
  ngayDat: string; // ISO DateTime
  thanhTien: number;
  trangThai: string; // "PROCESSING" | "PAID" | "EXPIRED" | "CANCELLED"
  maHoaDon: string;
}
```

---

## 6. Tích hợp với Payment Flow

Sau khi đặt vé thành công và nhận được `HoaDonResponse`, frontend có thể:

1. Hiển thị thông tin đơn hàng với trạng thái "Đang xử lý"
2. Tích hợp với HoaDonController để tạo URL thanh toán
3. Sau khi thanh toán thành công, cập nhật trạng thái thành "PAID"

**Lưu ý:** API này chỉ xử lý việc booking và trả về thông tin đơn hàng. Việc tạo URL thanh toán sẽ được xử lý riêng thông qua HoaDonController.