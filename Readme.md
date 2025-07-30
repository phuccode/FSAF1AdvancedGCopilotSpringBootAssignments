## Câu Hỏi và Trả Lời

### 1. Explain the trade-offs of caching. When might caching the findProductById result be a bad idea (e.g., if product prices change very frequently)?

**Trả Lời:**

**Ưu điểm Của Caching:**
- **Performance**: Giảm đáng kể số lượng truy vấn database queries và thời gian phản hồi.
- **Khả Năng Mở Rộng**: Giảm tải database, cho phép nhiều người dùng đồng thời hơn.
- **Hiệu Quả Chi Phí**: Ít sử dụng CPU/memory database hơn dẫn đến chi phí hạ tầng thấp hơn.
- **Trải Nghiệm Người Dùng**: Tải trang nhanh hơn cải thiện sự hài lòng của khách hàng.

**Trade-offs và Vấn Đề Tiềm Ẩn:**

1. **Độ Mới Dữ Liệu**: Dữ liệu cached có thể bị cũ, hiển thị thông tin lỗi thời nếu không cache đúng cách.
2. **Sử Dụng Memory**: Cache tiêu thụ thêm tài nguyên memory.
3. **Độ Phức Tạp**: Logic cache invalidation tăng độ phức tạp hệ thống.

**Khi cache `findProductById` có thể có vấn đề:**

- **Thay Đổi Giá Thường Xuyên**: Nếu giá sản phẩm thay đổi rất thường xuyên (ví dụ: định giá động, flash sales, định giá kiểu đấu giá), người dùng có thể thấy giá sai, dẫn đến khiếu nại khách hàng hoặc mất doanh số.
- **Inventory Thời Gian Thực**: Đối với sản phẩm có mức tồn kho thay đổi nhanh (mặt hàng phổ biến, số lượng hạn chế), dữ liệu cached có thể hiển thị mặt hàng còn hàng khi thực tế đã hết.
- **Định Giá Cá Nhân Hóa**: Nếu sản phẩm có giá riêng cho từng người dùng (giảm giá thành viên, định giá theo vùng), caching có thể hiển thị giá sai cho người dùng khác nhau.

### 2. What is the difference between a full table scan and an index scan in a database? Why did adding an @Index in Task 2 significantly improve query performance?

**Trả Lời:**

**Full Table Scan:**
- Database đọc **mọi row** trong table theo tuần tự.
- Kiểm tra từng record để xem có khớp với điều kiện WHERE không.
- Time Complexity: O(n) với n = số lượng rows.
- Tốn CPU và chậm cho các table có lượng data lớn.
- Ví dụ: `SELECT * FROM product WHERE name LIKE '%laptop%'` không có index.

**Index Scan:**
- Database sử dụng cấu trúc dữ liệu riêng biệt, đã sắp xếp (thường là B-tree).
- Nhanh chóng định vị các row liên quan sử dụng con trỏ index.
- Time Complexity: O(log n) cho exact matches, nhanh hơn nhiều cho range/LIKE queries.
- Yêu cầu ít thao tác I/O.
- Ví dụ: Cùng query với index có thể nhảy trực tiếp đến các row liên quan.

**Tại sao @Index trong Task 2 giúp cải thiện rõ rệt:**

- Phương thức searchProducts dùng LIKE hoặc WHERE name = ?, truy vấn theo name cột không index sẽ rất chậm.
- Khi thêm @Index(name = "idx_product_name", columnList = "name"), sẽ tạo chỉ mục SQL thì DB thực hiện index scan thay vì full scan.
- Giúp giảm thời gian truy vấn table product có nhiều dữ liệu.

**Ví Dụ Tác Động Performance:**
```sql
-- Không có index: Scan tất cả 1,000,000 sản phẩm.
-- Có index: Có thể chỉ cần kiểm tra 50 sản phẩm khớp với pattern.
```

### 3. Why are default health checks (like disk space, database connection) not always enough? Provide an example of a business-critical dependency for our e-commerce app that would require a custom health indicator.

**Trả Lời:**

**Hạn Chế Của Health Checks Mặc Định:**
Health checks mặc định của Spring Boot (dung lượng đĩa, kết nối database) chỉ xác minh hạ tầng cơ bản nhưng không validate **các dependency quan trọng cho business** hoặc **chức năng riêng của ứng dụng**.

**Vì sao cần thêm custom health check:**
Trong ứng dụng thương mại điện tử, ta thường phụ thuộc vào dịch vụ bên ngoài (third-party) để hoàn thành nghiệp vụ.
- Nếu những dịch vụ này không hoạt động, dù server vẫn "UP" theo actuator mặc định nhưng hệ thống thực chất vẫn đang gặp sự cố.

**Ví dụ điển hình: Cổng thanh toán (Payment Gateway)**
Ứng dụng phụ thuộc vào cổng thanh toán như Stripe, PayPal, Momo...
Nếu không thể kết nối tới API của PayPal:
- Người dùng không thể thanh toán.
- Đơn hàng không thể hoàn tất.
- Doanh thu bị ảnh hưởng trực tiếp.