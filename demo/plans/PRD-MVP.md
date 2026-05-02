PRD MVP - Ứng dụng quản lý phòng khám răng
=========================================

**TRẠNG THÁI CẬP NHẬT**: Tài liệu này mô tả PRD cho MVP và **trạng thái hiện tại của implementation** tính đến ngày 08/04/2026.

Tổng quan
---------
Tài liệu này mô tả PRD cho MVP của ứng dụng phòng khám răng. Mục tiêu: cung cấp chức năng cốt lõi để quản lý bệnh nhân, đặt lịch, check-in, ghi chú điều trị và nhắc lịch, nhằm giảm thủ công, tăng tỉ lệ đến khám và cải thiện trải nghiệm bệnh nhân.

**THAY ĐỔI THIẾT KẾ**:
- Backend chỉ cung cấp REST API endpoints, không phục vụ giao diện web (UI)
- Tất cả logic giao diện cần được xây dựng với frontend framework riêng (React, Vue, Angular, etc.)

1. Mục tiêu & Phạm vi MVP
-------------------------
Mục tiêu:
- bỏ chức năng đặt lịch thay vào đó chức năng đặt lịch sẽ thực hiện đơn giản hơn (cụ thể là khi người dùng bấm vào nút đặt lịch sẽ hiện lên popup để người dùng nhập thông tin như tên số điện thoại và nội dung cần khám "việc này lặp lại ở mục tư vấn")
- Thông báo nhắc lịch qua Email (SMS/Push sẽ được mở rộng sau)
- Lưu trữ hồ sơ cơ bản và tệp đính kèm (ảnh X-quang, hình trị liệu)

Phạm vi (loại trừ cho Phase 1):
- tạo tài khoản mới cho bác sĩ
- OTP verification
- Dashboard UI
- Frontend application

2. Yêu cầu chức năng (chi tiết theo mục) - TRẠNG THÁI IMPLEMENTATION
--------------------------------------------------------------------

2.1 Auth & User
✅ **HOÀN THÀNH**
- Chức năng: Đăng nhập, quản lý vai trò (receptionist, doctor, admin)
- Endpoints:
  - `POST /auth/login` → JWT token
  - Roles: RECEPTIONIST, DOCTOR, ADMIN
  - Authentication: JWT Bearer token (24h expiry)
  - Password hashing: BCrypt

*Bắt buộc hoàn thành
- Đăng ký (POST /auth/register) - cần backend support thêm
- OTP verification
- Reset mật khẩu

2.2 Hồ sơ bệnh nhân(khi tạo và sửa có lưu lại ngày giờ).
⚠️ **HOÀN THÀNH CÓ ĐIỀU KIỆN**
- Chức năng: Xem hồ sơ, cập nhật thông tin (dob, address, medicalHistory, allergies)
- Endpoints:
  - `GET /patients/{id}` → Lấy hồ sơ (all role)
  - `PUT /patients/{id}` → Cập nhật hồ sơ (RECEPTIONIST, ADMIN only)
- Permissions:
  - **RECEPTIONIST**: Cập nhật thông tin bệnh nhân
  - **DOCTOR**: Xem thông tin bệnh nhân
  - **ADMIN**: Quản lý tất cả

*bắt buộc hoàn thành
- CRUD đầy đủ (create/read/delete)

2.3 Quản lý bác sĩ 
⚠️ **HOÀN THÀNH CÓ ĐIỀU KIỆN**
- Chức năng: Admin tạo/xem bác sĩ, (thực hiện update chức năng là tạo mới bác sĩ lại với thông tin bác sĩ gồm: họ tên, số điện thoại, chuyên khoa, ảnh đại diện)
- Endpoints:
  - `POST /doctors` → Tạo bác sĩ (ADMIN only)đây là chức năng tạo mới 1 bác sĩ
  - `GET /doctors/{id}` → Xem bác sĩ (RECEPTIONIST, ADMIN)
- Permissions:
  - **ADMIN**: Tạo, xem bác sĩ
  - **RECEPTIONIST**: Xem danh sách bác sĩ
*loại bỏ chức năng 2.4 này thay vào đó chức năng đặt lịch hẹn sẽ update đơn giản hơn là chỉ nhận thông tin do người dùng nhập vào gồm họ tên, số điện thoại, ngày giờ có thể thăm khám và vấn đề cần thăm khám
2.4 Lịch hẹn (Appointment)
✅ **HOÀN THÀNH**
- Chức năng: Tạo lịch hẹn, xem chi tiết, check-in, chống double-booking
- Endpoints:
  - `POST /appointments` → Tạo lịch hẹn
  - `GET /appointments/{id}` → Lấy chi tiết appointment
  - `POST /appointments/{id}/checkin` → Check-in tại quầy
- Statuses: PENDING, CONFIRMED, CANCELED, COMPLETED, NO_SHOW, CHECKED_IN
- Permissions:
  - **POST**: PATIENT, RECEPTIONIST, ADMIN
  - **GET**: PATIENT, DOCTOR, RECEPTIONIST, ADMIN
  - **CHECKIN**: RECEPTIONIST, DOCTOR (assigned), ADMIN
- Features:
  - ✅ Double-booking prevention (DB query check)
  - ✅ Automatic status → PENDING khi tạo
  - ✅ Status → CHECKED_IN khi check-in

❌ **KHÔNG HOÀN THÀNH**:
- Hủy lịch (PUT /appointments/{id}/cancel)
- Reschedule
- Lấy danh sách lịch của bệnh nhân/bác sĩ
- Cập nhật status CONFIRMED/COMPLETED/NO_SHOW
- Available slots API
2.5 Visit record
✅ **HOÀN THÀNH**
- Chức năng:  tạo visit record với ghi chú, thủ thuật, chi phí
- Endpoints:
  - `POST /visits` → Tạo visit record (lễ tân, admin )
  - `GET /visits/{id}` → Xem visit record (lễ tân, ADMIN)
- Permissions:
  - **POST /visits**: lễ tân, ADMIN
  - **GET /visits**: lễ tân, ADMIN
 * với chức năng 2.5 này tôi muốn dựa vào hồ sơ bệnh nhân đã tạo phía trên sau khi thăm khám xong thì cập nhật thêm ghi chú, thủ thuật, chi phí vào form đã có sẵn.Sau đó update lại ngày giờ sửa gần nhất


❌ **KHÔNG HOÀN THÀNH**:
- Update visit record
- Liên kết diagnosis/treatments
* update chức năng 2.6 gửi thông báo tới bệnh nhân qua sms trước ngày đến khám 1 ngày. Lấy dữ liệu khách hàng từ chức năng đặt lịch 
2.6 Thông báo & Reminders
⚠️ **HOÀN THÀNH CÓ ĐIỀU KIỆN**
- Chức năng: Gửi thông báo nhắc nhở lịch khám về sms cho bệnh nhân
- Endpoints:
  - `POST /notifications/send` → Gửi thông báo tự động(RECEPTIONIST, ADMIN)
- Channels: Email, SMS, Push (cấu trúc hỗ trợ nhưng chưa implement provider)
- Permissions:
  - **POST /notifications/send**: RECEPTIONIST, ADMIN
- Features:
  - ✅ Lưu notification records
  - ✅ Template support
  - ✅ Channel selection

* bắt buộc HOÀN THÀNH**:
- SMS provider integration

5. Personas & Permissions
--------------------------
**PATIENT** (Bệnh nhân)
-  chỉ Có thể: xem được các thông tin public trên trang web, không cần đăng nhập. Khi bấm nút đặt lịch hoặc nút tư vấn miễn phí thì nhập thông tin vào popup. 

**RECEPTIONIST** (Lễ tân)
- Có thể: Tạo/xem xem bài viết, tạo và cập nhật hồ sơ bác sĩ, gửi thông báo, xem bác sĩ

**DOCTOR** (Bác sĩ)
- Có thể: 

**ADMIN** (Quản trị)
- Có thể: Tất cả + tạo bác sĩ, quản lý người dùng

7. Data Model (Current Implementation)
--------------------------------------
```
User {
  id: Long
  name: String
  email: String (unique)
  phone: String (unique)
  passwordHash: String (BCrypt)
  role: Role (PATIENT, RECEPTIONIST, DOCTOR, ADMIN)
}

PatientProfile {
  id: Long
  user: User (OneToOne)
  dob: LocalDate
  address: String
  medicalHistory: String (2000 chars)
  allergies: String (1000 chars)
}

Doctor {
  id: Long
  user: User (OneToOne)
  specialty: String
  workingHours: String (JSON or text, 1000 chars)
}

Appointment {
  id: Long
  doctor: Doctor (ManyToOne)
  patient: PatientProfile (ManyToOne)
  startTime: LocalDateTime
  endTime: LocalDateTime
  status: AppointmentStatus
  source: String
}

AppointmentStatus: PENDING, CONFIRMED, CANCELED, COMPLETED, NO_SHOW, CHECKED_IN

VisitRecord {
  id: Long
  appointment: Appointment (OneToOne)
  notes: String (2000 chars)
  procedures: String (2000 chars)
  cost: BigDecimal
  attachments: List<Attachment>
}

Notification {
  id: Long
  appointment: Appointment (ManyToOne)
  channel: String (email, sms, push)
  template: String
  sentAt: LocalDateTime
  status: String
}

Attachment {
  id: Long
  visitRecord: VisitRecord (ManyToOne)
  url: String
  type: String (MIME type)
  size: Long
}
```

8. Security Configuration
--------------------------
✅ **Implemented Features**:
- JWT authentication (24h expiry)
- Role-based access control (@PreAuthorize)
- BCrypt password encoding
- Stateless session (no cookies)
- CORS+CSRF disabled (API only)

❌ **TODOs**:
- HTTPS/TLS enforcing
- Rate limiting
- Authentication audit logging
- CORS configuration for specific origins

9. Testing Status
-----------------
- Unit Tests: ✅ Basic model/service tests available
- Integration Tests: ⚠️ Partial (PatientServiceTests, VisitAttachmentTests, AppIntegrationTests)
- E2E Tests: ❌ Not implemented
- Coverage: ⚠️ Need improvement

10. Priority Features for Next Phase
------------------------------------
**HIGH**:
1. POST /auth/register (bệnh nhân tự đăng ký)
2. PUT /appointments/{id}/cancel, /reschedule
3. List endpoints (GET /appointments, /visits)
4. Available slots API
5. Email provider integration (SendGrid, AWS SES)

**MEDIUM**:
1. Audit logging
2. SMS provider integration
3. Update VisitRecord endpoint
4. Dashboard APIs (appointments by day, stats)
5. Scheduled notifications

**LOW**:
1. File size validation
2. Image preview
3. Payment integration
4. Advanced reporting

11. Database Configuration
---------------------------
- Type: SQL Server (local dev)
- Connection String: `jdbc:sqlserver://localhost:1433;databaseName=master;...`
- Credentials: sa/123 (DEV ONLY - CHANGE FOR PRODUCTION!)
- ORM: Hibernate with Spring Data JPA
- DDL: `spring.jpa.hibernate.ddl-auto=update`

⚠️ **PRODUCTION CHANGES NEEDED**:
- Change from SQL Server to PostgreSQL/MySQL
- Update datasource username/password
- Enable encryption
- Setup connection pooling
- Enable SSL/TLS

12. Environment Variables & Configuration
-------------------------------------------
```properties
# JWT
jwt.secret=change-me-for-prod

# File uploads
file.upload-dir=uploads

# Database
spring.datasource.url=jdbc:sqlserver://...
spring.datasource.username=sa
spring.datasource.password=123
```

13. Deployment Notes
---------------------
- Framework: Spring Boot 3.x
- Java: JDK 17+
- Build: Maven (mvn clean package)
- Run: `java -jar demo-0.0.1-SNAPSHOT.jar`
- Package: maven-shade plugin (runnable JAR)

14. Version History
-------------------
**v0.1 (Current - 08/04/2026)**
- Removed UI from backend (no static files served)
- Added @PreAuthorize for method-level security
- Implemented all core endpoints
- Clear role-based permissions
- JWT authentication working
- File upload/download for visit records

15. Next Steps for Team
-----------------------
1. **Frontend Development**: Build React/Vue/Angular SPA with these endpoints
2. **Registration Feature**: Implement POST /auth/register with email verification
3. **Email Integration**: Setup SendGrid/AWS SES for notifications
4. **List Endpoints**: Add GET endpoints for appointments/visits lists with filtering
5. **Available Slots**: Implement doctor availability calculation
6. **Testing**: Write comprehensive E2E tests
7. **Deployment**: Setup Docker, CI/CD pipeline
8. **Documentation**: Generate OpenAPI/Swagger documentation

---

**Last Updated**: 08/04/2026
**Document Status**: ACTIVE - Implementation in progress
