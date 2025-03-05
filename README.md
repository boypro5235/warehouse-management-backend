# WebQuanLiKho

## Giới thiệu
Đây là backend của dự án thiết kế website quản lý kho, được xây dựng bằng Spring Boot. Hệ thống giúp quản lý hàng tồn kho, nhập/xuất hàng hóa, theo dõi tình trạng sản phẩm và tạo báo cáo chi tiết.

## Công nghệ sử dụng
- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA** (Quản lý database)
- **Spring Security** (Xác thực & phân quyền)
- **JWT** (Xác thực người dùng)
- **PostgreSQL** (Hệ quản trị cơ sở dữ liệu)
- **Swagger** (Tài liệu API)
- **Lombok** (Giảm boilerplate code)

## Cài đặt
### Yêu cầu hệ thống
- **JDK 17**
- **Maven 3+**
- **PostgreSQL 14+**

### Cấu hình cơ sở dữ liệu
1. Tạo một database mới trong PostgreSQL:
   ```sql
   CREATE DATABASE warehouse_db;
   ```
2. Cấu hình file `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

### Cách chạy dự án
1. Clone repository:
   ```sh
   git clone https://github.com//boypro5235/WebQuanLiKho.git
   ```
2. Điều hướng vào thư mục dự án:
   ```sh
   cd WebQuanLiKho
   ```
3. Build và chạy ứng dụng:
   ```sh
   mvn spring-boot:run
   ```

## API Documentation
Sau khi khởi chạy, tài liệu API có thể được truy cập tại:
```
http://localhost:8080/swagger-ui/index.html
```

## Các module chính
- **Authentication**: Đăng ký, đăng nhập, xác thực JWT
- **User Management**: Quản lý người dùng, phân quyền
- **Product Management**: Thêm, sửa, xóa sản phẩm
- **Inventory Management**: Kiểm soát số lượng hàng tồn kho
- **Order Management**: Quản lý nhập/xuất hàng hóa
- **Reporting**: Xuất báo cáo tổng hợp

## Đóng góp
1. Fork repository
2. Tạo branch mới (`feature/your-feature`)
3. Commit thay đổi
4. Push lên branch của bạn
5. Tạo pull request

## Liên hệ
Nếu có bất kỳ thắc mắc hoặc đóng góp, vui lòng liên hệ qua email: `nguyenlong18022004@gmail.com`


