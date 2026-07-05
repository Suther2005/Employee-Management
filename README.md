# 🏢 Smart Employee Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)](https://getbootstrap.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A **production-ready**, full-stack HR management system built with Java 21, Spring Boot 3.x, MySQL 8, and Thymeleaf. Designed as a **final-year Computer Science project** showcasing enterprise-level architecture, clean coding, and professional UI.

---

## 🚀 Features

### Admin Dashboard
- 📊 **Analytics** — stat cards + Chart.js charts (department-wise, monthly trend, leave breakdown, attendance %)
- 👥 **Employee Management** — full CRUD, pagination, sorting, search, photo upload
- 🏢 **Department Management** — create, update, delete with employee count protection
- ✅ **Attendance Tracking** — view all attendance, filter by employee/date
- 📋 **Leave Management** — approve/reject with email notifications
- 💰 **Salary Management** — manage salary records, auto net salary calculation
- 📄 **Reports** — export Employee, Salary, Leave data to **Excel** (.xlsx) and **PDF**

### Employee Portal
- 🖥️ **Personal Dashboard** — quick action cards and attendance status
- 📸 **Profile Management** — view/edit details, upload photo, change password
- ⏰ **Attendance** — check in/check out with real-time clock, view history
- 📝 **Leave** — apply, track status, cancel pending requests
- 💳 **Salary** — view personal salary slip

### Security
- 🔐 **BCrypt** password encryption (strength 12)
- 🛡️ **Role-based access control** (ADMIN / EMPLOYEE)
- 🔒 **CSRF** protection
- 🕐 **Session** management (30-min timeout)
- 🚫 **Custom error pages** (404, 500, Access Denied)

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security 6 + BCrypt |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Frontend | Thymeleaf + Bootstrap 5.3 + Chart.js |
| Build | Apache Maven |
| Excel Export | Apache POI 5.x |
| PDF Export | OpenPDF (iText fork) |
| Email | Spring Boot Mail (JavaMail) |

---

## 📁 Project Structure

```
employee-management/
├── src/main/java/com/ems/
│   ├── EmsApplication.java           ← Main entry point
│   ├── config/                        ← Security, Web MVC, DataInitializer, AppConfig
│   ├── controller/                    ← 9 MVC controllers
│   ├── service/                       ← Service interfaces
│   │   └── impl/                      ← Service implementations
│   ├── repository/                    ← 7 Spring Data JPA repositories
│   ├── entity/                        ← 7 JPA entities
│   ├── dto/                           ← 6 Data Transfer Objects
│   ├── security/                      ← CustomUserDetailsService
│   ├── exception/                     ← GlobalExceptionHandler + 3 custom exceptions
│   └── util/                          ← FileUploadUtil, ExcelExportUtil, PdfExportUtil
├── src/main/resources/
│   ├── application.properties         ← All configuration
│   ├── schema.sql                     ← DDL (optional, Hibernate auto-creates)
│   ├── data.sql                       ← Sample data
│   ├── static/
│   │   ├── css/main.css               ← Custom styles
│   │   └── js/main.js                 ← Custom JavaScript
│   └── templates/                     ← 25+ Thymeleaf templates
└── pom.xml                            ← Maven dependencies
```

---

## ⚙️ Prerequisites

- **Java 21** (JDK) — [Download](https://openjdk.org/)
- **Maven 3.8+** — [Download](https://maven.apache.org/)
- **MySQL 8.0** — [Download](https://dev.mysql.com/downloads/)
- **IntelliJ IDEA** (recommended) or any Java IDE

---

## 🔧 Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/smart-employee-management.git
cd smart-employee-management
```

### 2. Set Up MySQL Database

Open MySQL Workbench or terminal and run:

```sql
CREATE DATABASE IF NOT EXISTS employee_management_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

Or simply start the app — it will auto-create the DB thanks to `createDatabaseIfNotExist=true` in the JDBC URL.

### 3. Configure Database Credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/employee_management_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### 4. (Optional) Configure Email

To enable leave approval email notifications:

```properties
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password   # Gmail App Password (not regular password)
```

> **Note:** For Gmail, enable 2FA and generate an App Password from Google Account Settings.  
> If not configured, emails will be silently skipped (logged as warnings).

### 5. Build & Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The app will start at **http://localhost:8080**

### 6. (Optional) Load Sample Data

Run `src/main/resources/data.sql` in MySQL Workbench to populate sample employees, departments, and records.

---

## 🔑 Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| **Admin** | `admin@ems.com` | `Admin@123` |
| **Employee 1** | `john.doe@ems.com` | `Employee@123` |
| **Employee 2** | `priya.sharma@ems.com` | `Employee@123` |
| **Employee 3** | `anil.mehta@ems.com` | `Employee@123` |

> The default admin account is auto-created by `DataInitializer.java` on first startup.

---

## 📡 API Endpoints

### Authentication
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/login` | Login page |
| POST | `/login` | Process login |
| GET | `/logout` | Logout |

### Dashboard (ADMIN)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/dashboard` | Admin dashboard with analytics |

### Employees (ADMIN)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/employees` | List employees (paginated, sortable, searchable) |
| GET | `/employees/add` | Add employee form |
| POST | `/employees/save` | Create employee |
| GET | `/employees/edit/{id}` | Edit employee form |
| POST | `/employees/update/{id}` | Update employee |
| GET | `/employees/view/{id}` | View employee details |
| POST | `/employees/delete/{id}` | Delete employee |

### Departments (ADMIN)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/departments` | List departments |
| GET | `/departments/add` | Add form |
| POST | `/departments/save` | Create department |
| GET | `/departments/edit/{id}` | Edit form |
| POST | `/departments/update/{id}` | Update department |
| GET | `/departments/view/{id}` | View with employees |
| POST | `/departments/delete/{id}` | Delete department |

### Attendance
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/attendance/today` | ALL | Today's check-in/out |
| POST | `/attendance/checkin` | ALL | Record check-in |
| POST | `/attendance/checkout` | ALL | Record check-out |
| GET | `/attendance/my-history` | ALL | Employee's own history |
| GET | `/attendance` | ADMIN | All attendance records |

### Leave
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/leave/apply` | ALL | Apply leave form |
| POST | `/leave/apply` | ALL | Submit leave application |
| GET | `/leave/my-leaves` | ALL | Employee's leave history |
| POST | `/leave/cancel/{id}` | ALL | Cancel pending leave |
| GET | `/leave` | ADMIN | All leave requests |
| POST | `/leave/approve/{id}` | ADMIN | Approve leave |
| POST | `/leave/reject/{id}` | ADMIN | Reject leave |
| GET | `/leave/view/{id}` | ADMIN | View leave details |

### Salary
| Method | URL | Access | Description |
|--------|-----|--------|-------------|
| GET | `/salary` | ADMIN | List all salary records |
| GET | `/salary/add` | ADMIN | Add salary form |
| POST | `/salary/save` | ADMIN | Create salary record |
| GET | `/salary/edit/{id}` | ADMIN | Edit salary form |
| POST | `/salary/update/{id}` | ADMIN | Update salary |
| POST | `/salary/delete/{id}` | ADMIN | Delete salary |
| GET | `/salary/my-salary` | ALL | Employee's own salary |

### Reports (ADMIN)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/reports` | Reports page |
| GET | `/reports/employees/excel` | Export employees to Excel |
| GET | `/reports/employees/pdf` | Export employees to PDF |
| GET | `/reports/salary/excel` | Export salary to Excel |
| GET | `/reports/salary/pdf` | Export salary to PDF |
| GET | `/reports/leave/excel` | Export leaves to Excel |
| GET | `/reports/leave/pdf` | Export leaves to PDF |

### Profile
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/profile` | View profile |
| GET | `/profile/edit` | Edit profile form |
| POST | `/profile/update` | Update profile |
| GET | `/profile/change-password` | Change password form |
| POST | `/profile/change-password` | Update password |

---

## 🗄️ Database Design

```
roles (1) ──< users (1) ─── employees (1) ──< attendance
                                   │         ──< leave_requests
                                   │         ─── salary (1:1)
                                   └── departments (1) ──< employees
```

---

## 🎯 Features Showcase (Resume Points)

✅ Spring Boot 3.x + Java 21 (modern LTS)  
✅ Spring Security with BCrypt & Role-based Auth  
✅ Spring Data JPA with custom JPQL queries  
✅ Layered architecture (Controller → Service → Repository)  
✅ DTO pattern with Bean Validation  
✅ Global Exception Handling with @ControllerAdvice  
✅ Excel export with Apache POI  
✅ PDF export with OpenPDF  
✅ Async email notifications with JavaMail  
✅ File upload with UUID naming  
✅ Dashboard analytics with Chart.js  
✅ Pagination, Sorting, Search  
✅ Dark mode toggle  
✅ Responsive Bootstrap 5 UI  
✅ CSRF protection  
✅ Custom error pages  
✅ Auto database seeding on startup  

---

## 🤝 Contributing

This project is built as a final-year resume project. Feel free to fork and extend!

---

## 📝 License

MIT License — free to use for educational and portfolio purposes.

---

## 👤 Author

**Sutherson R**  
🐙 [GitHub](https://github.com/Suther2005)

---

*Built with ❤️ as a Final Year Computer Science Project*
