-- ============================================================
-- Smart Employee Management System — Sample Data
-- Run AFTER schema.sql
-- ============================================================

USE employee_management_db;

-- -----------------------------------------------
-- Roles
-- -----------------------------------------------
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN'), (2, 'EMPLOYEE');

-- -----------------------------------------------
-- Admin User
-- Password: Admin@123 (BCrypt encoded)
-- -----------------------------------------------
INSERT IGNORE INTO users (id, username, password, email, enabled, role_id) VALUES
(1, 'admin@ems.com',
 '$2a$12$Z5oOSIi.FZ0t.GECMqNdL.GVN6qBvDuTQshAKSMpK5JDkqvM7wUOO',
 'admin@ems.com', 1, 1);

-- -----------------------------------------------
-- Departments
-- -----------------------------------------------
INSERT IGNORE INTO departments (id, name, head, description) VALUES
(1, 'Engineering',       'Rajesh Kumar',   'Software development and infrastructure team'),
(2, 'Human Resources',  'Priya Sharma',   'HR operations, recruitment and employee relations'),
(3, 'Finance',          'Anil Mehta',     'Finance, accounting and payroll management'),
(4, 'Marketing',        'Sneha Patel',    'Brand management, campaigns and digital marketing'),
(5, 'Operations',       'Vikram Singh',   'Day-to-day business operations and logistics');

-- -----------------------------------------------
-- Employee Users
-- Password: Employee@123 (BCrypt encoded)
-- -----------------------------------------------
INSERT IGNORE INTO users (id, username, password, email, enabled, role_id) VALUES
(2, 'john.doe@ems.com',      '$2a$12$6lHPFjCIbF5o3.kjnkHNMecx9tiyEr/rpmfPkCimxQU0nHmpIcwMm', 'john.doe@ems.com',      1, 2),
(3, 'priya.sharma@ems.com',  '$2a$12$6lHPFjCIbF5o3.kjnkHNMecx9tiyEr/rpmfPkCimxQU0nHmpIcwMm', 'priya.sharma@ems.com',  1, 2),
(4, 'anil.mehta@ems.com',    '$2a$12$6lHPFjCIbF5o3.kjnkHNMecx9tiyEr/rpmfPkCimxQU0nHmpIcwMm', 'anil.mehta@ems.com',    1, 2),
(5, 'sneha.patel@ems.com',   '$2a$12$6lHPFjCIbF5o3.kjnkHNMecx9tiyEr/rpmfPkCimxQU0nHmpIcwMm', 'sneha.patel@ems.com',   1, 2),
(6, 'vikram.singh@ems.com',  '$2a$12$6lHPFjCIbF5o3.kjnkHNMecx9tiyEr/rpmfPkCimxQU0nHmpIcwMm', 'vikram.singh@ems.com',  1, 2);

-- -----------------------------------------------
-- Employees
-- -----------------------------------------------
INSERT IGNORE INTO employees
  (id, emp_id, first_name, last_name, email, phone, gender, date_of_birth, address, designation, joining_date, status, department_id, user_id)
VALUES
(1, 'EMP0001', 'John',   'Doe',    'john.doe@ems.com',     '+919876543210', 'MALE',   '1995-04-15',
 '123 MG Road, Bengaluru, Karnataka', 'Senior Software Engineer', '2022-01-10', 'ACTIVE', 1, 2),

(2, 'EMP0002', 'Priya',  'Sharma', 'priya.sharma@ems.com', '+919845678901', 'FEMALE', '1993-08-22',
 '45 Linking Road, Mumbai, Maharashtra', 'HR Manager', '2021-06-01', 'ACTIVE', 2, 3),

(3, 'EMP0003', 'Anil',   'Mehta',  'anil.mehta@ems.com',   '+919734567890', 'MALE',   '1990-12-05',
 '78 Park Street, Kolkata, West Bengal', 'Finance Analyst', '2020-03-15', 'ACTIVE', 3, 4),

(4, 'EMP0004', 'Sneha',  'Patel',  'sneha.patel@ems.com',  '+919623456789', 'FEMALE', '1997-03-18',
 '22 CG Road, Ahmedabad, Gujarat', 'Marketing Executive', '2023-02-20', 'ACTIVE', 4, 5),

(5, 'EMP0005', 'Vikram', 'Singh',  'vikram.singh@ems.com', '+919512345678', 'MALE',   '1988-11-30',
 '56 Connaught Place, New Delhi', 'Operations Manager', '2019-07-01', 'ACTIVE', 5, 6);

-- -----------------------------------------------
-- Salary Records
-- -----------------------------------------------
INSERT IGNORE INTO salary (id, employee_id, basic_salary, allowance, bonus, deductions, net_salary, month, year) VALUES
(1, 1, 85000.00, 15000.00, 5000.00, 8500.00,  96500.00, 7, 2026),
(2, 2, 75000.00, 12000.00, 3000.00, 7500.00,  82500.00, 7, 2026),
(3, 3, 70000.00, 10000.00, 2500.00, 7000.00,  75500.00, 7, 2026),
(4, 4, 55000.00, 8000.00,  2000.00, 5500.00,  59500.00, 7, 2026),
(5, 5, 90000.00, 18000.00, 7000.00, 9000.00, 106000.00, 7, 2026);

-- -----------------------------------------------
-- Leave Requests (Sample)
-- -----------------------------------------------
INSERT IGNORE INTO leave_requests (id, employee_id, leave_type, start_date, end_date, reason, status, applied_date) VALUES
(1, 1, 'SICK',   '2026-07-10', '2026-07-11', 'Fever and cold, doctor advised rest.',    'PENDING',  '2026-07-04 09:00:00'),
(2, 2, 'CASUAL', '2026-07-08', '2026-07-08', 'Personal family function.',               'APPROVED', '2026-07-02 10:30:00'),
(3, 3, 'ANNUAL', '2026-07-15', '2026-07-19', 'Family vacation planned.',                'PENDING',  '2026-07-03 11:00:00'),
(4, 4, 'CASUAL', '2026-06-25', '2026-06-25', 'Personal work.',                          'REJECTED', '2026-06-20 14:00:00'),
(5, 5, 'SICK',   '2026-07-05', '2026-07-06', 'Viral infection.',                        'PENDING',  '2026-07-04 08:00:00');

-- -----------------------------------------------
-- Attendance (Sample — Today and recent days)
-- -----------------------------------------------
INSERT IGNORE INTO attendance (employee_id, date, check_in, check_out, working_hours, status) VALUES
(1, CURDATE() - INTERVAL 1 DAY, '09:02:00', '18:05:00', 9.05, 'PRESENT'),
(2, CURDATE() - INTERVAL 1 DAY, '08:55:00', '17:58:00', 9.05, 'PRESENT'),
(3, CURDATE() - INTERVAL 1 DAY, '09:15:00', '18:10:00', 8.92, 'PRESENT'),
(4, CURDATE() - INTERVAL 1 DAY, '10:00:00', '14:00:00', 4.00, 'HALF_DAY'),
(5, CURDATE() - INTERVAL 1 DAY, '08:45:00', '17:45:00', 9.00, 'PRESENT'),
(1, CURDATE() - INTERVAL 2 DAY, '09:00:00', '18:00:00', 9.00, 'PRESENT'),
(2, CURDATE() - INTERVAL 2 DAY, '09:10:00', '18:05:00', 8.92, 'PRESENT'),
(3, CURDATE() - INTERVAL 2 DAY, '09:05:00', '18:00:00', 8.92, 'PRESENT');
