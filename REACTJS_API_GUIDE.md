# 🎬 API Guide - Hệ thống Đặt Vé Xem Phim (ReactJS)

## 📋 Quy trình đặt vé hoàn chỉnh từ A-Z

### 🎯 Tổng quan Flow
1. **Đặt vé** → Nhận thông tin booking với trạng thái `PROCESSING`
2. **Tạo thanh toán** → Nhận URL/QR code thanh toán 
3. **Thanh toán** → Hệ thống tự động cập nhật trạng thái
4. **Kiểm tra kết quả** → Polling API để xác nhận trạng thái

---

## 🛠️ API Endpoints

### 1. Đặt vé (Book Tickets)
**Endpoint:** `POST /api/ve`

**Request:**
```json
{
  "maSuatChieu": "sc-123",
  "maGheList": ["ghe-001", "ghe-002"],
  "maPhim": "phim-001",
  "maKhuyenMai": "km-001",
  "phuongThucThanhToan": "VNPAY",
  "dichVuList": [
    {
      "maDv": "dv-001", 
      "soLuong": 2
    }
  ]
}
```

**Response Success (200):**
```json
{
  "code": 200,
  "message": "Đặt vé thành công - Đang xử lý",
  "data": {
    "maHD": "HD1760164690255697",
    "ngayLap": "2025-10-11T13:38:10.2556321",
    "tongTien": 184000.0,
    "phuongThucThanhToan": "VNPAY",
    "trangThai": "PROCESSING",
    "maGiaoDich": "GD1760164690249",
    "ghiChu": "Tổng tiền vé: 180000 VND\\nPhụ thu dịch vụ: 50000 VND\\nGiảm giá: 46000 VND\\nTổng cộng: 184000 VND",
    "danhSachVe": [
      {
        "maVe": "VE-1760164690266586",
        "tenPhim": "The Hangover",
        "tenPhongChieu": "Phòng 3",
        "tenGhe": "A01",
        "thoiGianChieu": "2025-10-17T20:14:00",
        "ngayDat": "2025-10-11T13:38:10.2667541",
        "thanhTien": 90000.0,
        "trangThai": "PROCESSING",
        "maHoaDon": "HD1760164690255697"
      }
    ],
    "tenNguoiDung": null
  }
}
```

### 2. Tạo thanh toán VNPAY
**Endpoint:** `POST /api/payment/vn_pay/create`

**Request:**
```json
{
  "amount": 184000,
  "orderId": "HD1760164690255697"
}
```

**Response (201):**
```json
{
  "code": 201,
  "message": "Tạo thanh toán thành công",
  "data": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=18400000&..."
}
```

### 3. Tạo thanh toán MOMO
**Endpoint:** `POST /api/payment/momo/create`

**Request:**
```json
{
  "amount": 184000,
  "orderId": "HD1760164690255697"
}
```

**Response (201):**
```json
{
  "code": 201,
  "message": "Tạo thanh toán thành công",
  "data": {
    "payUrl": "https://payment.momo.vn/...",
    "qrCodeUrl": "data:image/png;base64,..."
  }
}
```

### 4. Callback thanh toán (Auto từ gateway)
**VNPAY Callback:** `GET /api/payment/vn_pay/payment_info`
**MOMO Callback:** `GET /api/payment/momo/payment_info`

**VNPAY Response Success:**
```json
{
  "code": 200,
  "message": "Thanh toán thành công",
  "data": {
    "transactionNo": "15198513",
    "transactionDate": "20251010215344",
    "responseCode": "00",
    "orderId": "HD1760164690255697",
    "status": "SUCCESS"
  }
}
```

### 5. Kiểm tra trạng thái thanh toán
**Endpoint:** `GET /api/payment/status/{orderId}`

**Response Success (PAID):**
```json
{
  "code": 200,
  "message": "Lấy trạng thái thành công",
  "data": {
    "orderId": "HD1760164690255697",
    "status": "PAID",
    "transactionNo": "15198513",
    "transactionDate": "20251010215344",
    "responseCode": "00",
    "paymentStatus": "SUCCESS"
  }
}
```

**Response Processing:**
```json
{
  "code": 200,
  "message": "Lấy trạng thái thành công",
  "data": {
    "orderId": "HD1760164690255697",
    "status": "PROCESSING",
    "transactionNo": null,
    "transactionDate": null,
    "responseCode": null,
    "paymentStatus": "PENDING"
  }
}
```

---

## ⚛️ ReactJS Implementation

### 🎯 Component Structure
```
BookingFlow/
├── BookingForm.jsx          # Form đặt vé
├── PaymentSelection.jsx     # Chọn phương thức thanh toán
├── PaymentProcessing.jsx    # Xử lý thanh toán
├── PaymentSuccess.jsx       # Kết quả thành công
└── PaymentFailed.jsx        # Kết quả thất bại
```

### 📝 Main Booking Component

```javascript
import React, { useState, useEffect } from 'react';

const BookingFlow = () => {
  const [currentStep, setCurrentStep] = useState('booking');
  const [orderData, setOrderData] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('VNPAY');
  const [paymentStatus, setPaymentStatus] = useState('PENDING');

  // 1. Đặt vé
  const handleBooking = async (bookingData) => {
    try {
      const response = await fetch('/api/ve', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(bookingData)
      });

      const result = await response.json();

      if (result.code === 200) {
        setOrderData(result.data);
        localStorage.setItem('currentOrderId', result.data.maHD);
        setCurrentStep('payment');
      } else {
        alert(result.message);
      }
    } catch (error) {
      alert('Lỗi kết nối: ' + error.message);
    }
  };

  // 2. Tạo thanh toán
  const handlePayment = async () => {
    const endpoint = paymentMethod === 'VNPAY' 
      ? '/api/payment/vn_pay/create' 
      : '/api/payment/momo/create';

    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          amount: orderData.tongTien,
          orderId: orderData.maHD
        })
      });

      const result = await response.json();

      if (result.code === 201) {
        if (paymentMethod === 'VNPAY') {
          // Redirect tới VNPAY
          window.location.href = result.data;
        } else {
          // Hiển thị QR MOMO và start polling
          setCurrentStep('processing');
          startPaymentPolling(orderData.maHD);
        }
      } else {
        alert('Lỗi tạo thanh toán: ' + result.message);
      }
    } catch (error) {
      alert('Lỗi tạo thanh toán: ' + error.message);
    }
  };

  // 3. Polling kiểm tra trạng thái
  const checkPaymentStatus = async (orderId) => {
    try {
      const response = await fetch(\`/api/payment/status/\${orderId}\`);
      const result = await response.json();

      if (result.code === 200) {
        return result.data;
      }
      return { paymentStatus: 'ERROR' };
    } catch (error) {
      return { paymentStatus: 'ERROR' };
    }
  };

  const startPaymentPolling = (orderId) => {
    const pollInterval = setInterval(async () => {
      const status = await checkPaymentStatus(orderId);

      if (status.paymentStatus === 'SUCCESS') {
        clearInterval(pollInterval);
        setPaymentStatus('SUCCESS');
        setCurrentStep('success');
        localStorage.removeItem('currentOrderId');
      } else if (status.paymentStatus === 'FAILED') {
        clearInterval(pollInterval);
        setPaymentStatus('FAILED');
        setCurrentStep('failed');
      }
    }, 3000); // Poll mỗi 3 giây

    // Timeout sau 10 phút
    setTimeout(() => {
      clearInterval(pollInterval);
      if (paymentStatus === 'PENDING') {
        setPaymentStatus('TIMEOUT');
        setCurrentStep('failed');
      }
    }, 600000);
  };

  // 4. Handle return từ VNPAY
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const vnpResponseCode = urlParams.get('vnp_ResponseCode');
    const orderId = localStorage.getItem('currentOrderId');

    if (vnpResponseCode && orderId) {
      setCurrentStep('processing');
      startPaymentPolling(orderId);
    }
  }, []);

  return (
    <div className="booking-flow">
      {currentStep === 'booking' && (
        <BookingForm onSubmit={handleBooking} />
      )}
      
      {currentStep === 'payment' && (
        <PaymentSelection 
          orderData={orderData}
          selectedMethod={paymentMethod}
          onMethodChange={setPaymentMethod}
          onProceed={handlePayment}
        />
      )}
      
      {currentStep === 'processing' && (
        <PaymentProcessing 
          orderId={orderData?.maHD}
          paymentMethod={paymentMethod}
        />
      )}
      
      {currentStep === 'success' && (
        <PaymentSuccess orderData={orderData} />
      )}
      
      {currentStep === 'failed' && (
        <PaymentFailed 
          orderData={orderData}
          reason={paymentStatus}
          onRetry={() => setCurrentStep('payment')}
        />
      )}
    </div>
  );
};

export default BookingFlow;
```

### 💳 Payment Selection Component

```javascript
const PaymentSelection = ({ orderData, selectedMethod, onMethodChange, onProceed }) => {
  return (
    <div className="payment-selection">
      <h2>Thông tin đơn hàng</h2>
      <div className="order-summary">
        <p>Mã đơn hàng: {orderData.maHD}</p>
        <p>Tổng tiền: {orderData.tongTien.toLocaleString()} VND</p>
        <p>Trạng thái: {orderData.trangThai}</p>
      </div>

      <h3>Chọn phương thức thanh toán</h3>
      <div className="payment-methods">
        <label>
          <input 
            type="radio" 
            value="VNPAY"
            checked={selectedMethod === 'VNPAY'}
            onChange={(e) => onMethodChange(e.target.value)}
          />
          VNPAY
        </label>
        <label>
          <input 
            type="radio" 
            value="MOMO"
            checked={selectedMethod === 'MOMO'}
            onChange={(e) => onMethodChange(e.target.value)}
          />
          MOMO
        </label>
      </div>

      <button onClick={onProceed} className="proceed-btn">
        Thanh toán
      </button>
    </div>
  );
};
```

### ⏳ Payment Processing Component

```javascript
const PaymentProcessing = ({ orderId, paymentMethod }) => {
  const [timeLeft, setTimeLeft] = useState(600); // 10 phút

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft(prev => prev > 0 ? prev - 1 : 0);
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return \`\${mins}:\${secs.toString().padStart(2, '0')}\`;
  };

  return (
    <div className="payment-processing">
      <h2>Đang xử lý thanh toán...</h2>
      <p>Mã đơn hàng: {orderId}</p>
      <p>Phương thức: {paymentMethod}</p>
      <p>Thời gian còn lại: {formatTime(timeLeft)}</p>
      
      <div className="loading-spinner">
        <div className="spinner"></div>
      </div>
      
      <p>Vui lòng không đóng trang này</p>
    </div>
  );
};
```

---

## 🔄 Trạng thái & Error Handling

### 📊 Mapping Trạng thái
```javascript
const STATUS_MAPPING = {
  // Trạng thái đơn hàng
  'PROCESSING': 'Đang xử lý',
  'PAID': 'Đã thanh toán', 
  'FAILED': 'Thất bại',
  'CANCELLED': 'Đã hủy',
  'EXPIRED': 'Hết hạn',
  
  // Trạng thái thanh toán
  'SUCCESS': 'Thành công',
  'PENDING': 'Đang chờ',
  'TIMEOUT': 'Hết thời gian'
};
```

### 🚨 Error Handling Utils
```javascript
const handleApiError = (error, context) => {
  console.error(\`Error in \${context}:\`, error);
  
  // Show user-friendly message
  const message = error.message || 'Có lỗi xảy ra, vui lòng thử lại';
  showNotification(message, 'error');
  
  // Log for debugging
  if (process.env.NODE_ENV === 'development') {
    console.table(error);
  }
};

const showNotification = (message, type = 'info') => {
  // Implementation với toast/notification library
  toast(message, { type });
};
```

---

## 🎯 Tips & Best Practices

### ✅ Frontend Best Practices:
1. **localStorage backup** - Lưu orderId để recovery
2. **Countdown timer** - Hiển thị thời gian còn lại
3. **Polling strategy** - Check status mỗi 3-5 giây
4. **Error boundaries** - Wrap components với error handling
5. **Loading states** - Show spinner khi call API
6. **Back button handling** - Handle browser navigation
7. **Responsive design** - Mobile-friendly payment flow

### 🔒 Security:
1. **Validate inputs** - Client-side validation trước khi submit
2. **HTTPS only** - Tất cả payment calls qua HTTPS
3. **No sensitive data** - Không lưu payment info trong localStorage
4. **Timeout handling** - Clear timers khi unmount component

### 📱 UX Improvements:
1. **Progress indicator** - Show steps 1-2-3-4
2. **Confirmation modals** - Xác nhận trước khi proceed
3. **Retry mechanism** - Cho phép thử lại khi lỗi
4. **Success animation** - Celebrate khi thành công
5. **Clear error messages** - Specific error cho từng case

---

## 🧪 Testing Scenarios

### Test Cases:
1. **Happy Path** - Đặt vé → Thanh toán → Thành công
2. **Payment Failed** - Đặt vé → Thanh toán thất bại
3. **Timeout** - Đặt vé → Không thanh toán trong 10 phút
4. **Network Error** - Mất kết nối during polling
5. **Browser Refresh** - Refresh page during payment
6. **Back Button** - Browser back during flow

Với guide này, team FE ReactJS có thể implement hoàn chỉnh flow đặt vé với UX tốt và error handling đầy đủ! 🚀