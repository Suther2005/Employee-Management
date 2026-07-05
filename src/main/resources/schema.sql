-- ============================================================
-- Smart Employee Management System — Database Schema
-- MySQL 8.0+
-- ============================================================

CREATE DATABASE IF NOT EXISTS employee_management_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE employee_management_db;

-- -----------------------------------------------
-- Table: roles
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: users
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    enabled    TINYINT(1)  NOT NULL DEFAULT 1,
    role_id    BIGINT      NOT NULL,
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: departments
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS departments (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    head        VARCHAR(100),
    description TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: employees
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS employees (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    emp_id        VARCHAR(20)  NOT NULL UNIQUE,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    phone         VARCHAR(20),
    gender        ENUM('MALE', 'FEMALE', 'OTHER'),
    date_of_birth DATE,
    address       TEXT,
    designation   VARCHAR(100) NOT NULL,
    joining_date  DATE,
    status        ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE', 'TERMINATED') DEFAULT 'ACTIVE',
    profile_image VARCHAR(255),
    department_id BIGINT,
    user_id       BIGINT UNIQUE,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_emp_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL,
    CONSTRAINT fk_emp_user       FOREIGN KEY (user_id)       REFERENCES users(id)       ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: attendance
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id   BIGINT  NOT NULL,
    date          DATE    NOT NULL,
    check_in      TIME,
    check_out     TIME,
    working_hours DOUBLE,
    status        ENUM('PRESENT', 'ABSENT', 'HALF_DAY', 'ON_LEAVE') DEFAULT 'PRESENT',
    UNIQUE KEY uq_attendance (employee_id, date),
    CONSTRAINT fk_attendance_emp FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: leave_requests
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS leave_requests (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id      BIGINT   NOT NULL,
    leave_type       ENUM('CASUAL', 'SICK', 'ANNUAL', 'EMERGENCY', 'MATERNITY', 'PATERNITY', 'UNPAID') NOT NULL,
    start_date       DATE     NOT NULL,
    end_date         DATE     NOT NULL,
    reason           TEXT,
    status           ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    applied_date     DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewed_at      DATETIME,
    reviewed_by      VARCHAR(100),
    rejection_reason TEXT,
    CONSTRAINT fk_leave_emp FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------
-- Table: salary
-- -----------------------------------------------
CREATE TABLE IF NOT EXISTS salary (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id  BIGINT         NOT NULL UNIQUE,
    basic_salary DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    allowance    DECIMAL(12, 2)          DEFAULT 0.00,
    bonus        DECIMAL(12, 2)          DEFAULT 0.00,
    deductions   DECIMAL(12, 2)          DEFAULT 0.00,
    net_salary   DECIMAL(12, 2),
    month        INT,
    year         INT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_salary_emp FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
) ENGINE=InnoDB;
