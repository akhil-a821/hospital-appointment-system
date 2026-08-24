# CarePulse — Hospital Appointment Scheduling System (Spring Boot Web App)

A modern, full-featured **Hospital Appointment Scheduling Web Application** built with **Spring Boot 3**, **Spring MVC**, **Spring Data JPA**, and a **Mobile-Responsive UI** that runs on **Android Phones, iPhones, Tablets, and Desktop Browsers**.

---

## 📱 Mobile & Desktop Access

### 1. Run on Your Computer:
Double-click **`run_web.bat`** (or in terminal run `tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run`).

### 2. Open on Desktop Browser:
Navigate to:
👉 **`http://localhost:8080`**

### 3. Open on Mobile Phone (Android / iPhone / iPad):
1. Make sure your phone is connected to the **same Wi-Fi network** as your computer.
2. Find your computer's local IP address (open PowerShell and type `ipconfig`, look for `IPv4 Address`, e.g. `192.168.1.15`).
3. On your phone's browser (Chrome / Safari), navigate to:
👉 **`http://192.168.1.15:8080`** *(replace with your computer's actual IP)*
4. You can now book appointments, view doctors, and manage the hospital right from your phone!

---

## 🔑 Demo Login Credentials

You can click the **1-Click Demo Buttons** on the login screen or enter:

| Role | Email | Password | Features |
| :--- | :--- | :--- | :--- |
| **Patient** | `patient@hospital.com` | `patient123` | Doctor Directory & Search, Interactive Time Slot Picker, My Appointments, Cancellation |
| **Admin** | `admin@hospital.com` | `admin123` | Hospital KPIs, Doctor Management (Add/Edit/Delete), Appointment Confirmation & Cancellation |

---

## 🏗️ Architecture Overview

- **Framework**: Spring Boot 3.2.x, Spring MVC, Spring Data JPA
- **Database**: Runs out-of-the-box on automatic local embedded database or connects to MySQL
- **Frontend**: Responsive HTML5, CSS3 (Mobile-first Flexbox & Grid), JavaScript, and Thymeleaf
- **APIs**: RESTful dynamic slot availability calculation (`/api/doctors/{id}/slots`)

---

## 🚀 Free Cloud Deployment (e.g. Render.com / Railway)

1. Upload this folder to a GitHub repository or deploy directly via Render.
2. Select **New Web Service** $\rightarrow$ Connect repo.
3. Build Command: `mvn clean package -DskipTests`
4. Start Command: `java -jar target/hospital-appointment-system-2.0.0.jar`
5. You will get a free live URL (e.g. `https://hospital-carepulse.onrender.com`) accessible worldwide on any phone or PC!
