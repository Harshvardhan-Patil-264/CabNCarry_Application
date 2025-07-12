# 🚗 CabN - Ride Booking Android App

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

*A modern ride booking application built with Java and XML for Android platform*

[![GitHub stars](https://img.shields.io/github/stars/yourusername/cabn?style=social)](https://github.com/yourusername/cabn)
[![GitHub forks](https://img.shields.io/github/forks/yourusername/cabn?style=social)](https://github.com/yourusername/cabn)
[![GitHub issues](https://img.shields.io/github/issues/yourusername/cabn)](https://github.com/yourusername/cabn/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/yourusername/cabn)](https://github.com/yourusername/cabn/pulls)

</div>

---

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [✨ Features](#-features)
- [🛠️ Technologies Used](#️-technologies-used)
- [📱 Screenshots](#-screenshots)
- [🏗️ Architecture](#️-architecture)
- [🚀 Installation](#-installation)
- [⚙️ Configuration](#️-configuration)
- [📖 Usage](#-usage)
- [🔧 API Integration](#-api-integration)
- [🗄️ Database Schema](#️-database-schema)
- [🔒 Security Features](#-security-features)
- [📁 Project Structure](#-project-structure)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👨‍💻 Credits](#-credits)

---

## 🎯 Overview

CabN is a comprehensive ride booking Android application that connects users with drivers in real-time. Built with Java and XML, it features a modern Material Design interface, secure OTP-based authentication, real-time location tracking, and an intuitive booking system.

### 🎯 Key Objectives
- **User-Friendly Interface**: Modern Material Design with intuitive navigation
- **Secure Authentication**: OTP-based login system for both users and drivers
- **Real-Time Location**: GPS integration for accurate pickup and drop-off locations
- **Efficient Booking**: Streamlined ride booking process with multiple vehicle options
- **Driver Management**: Dedicated driver interface for ride acceptance and management

---

## ✨ Features

### 👤 User Features
- 🔐 **Secure OTP Authentication** - Email-based login with one-time password
- 📍 **Interactive Map Integration** - Select pickup and drop-off locations
- 🚗 **Multiple Vehicle Options** - Choose from cars, rickshaws, tempos, and trucks
- 💰 **Dynamic Pricing** - Real-time fare calculation based on distance
- 📱 **Real-Time Location Tracking** - GPS integration for accurate location services
- 📋 **Ride History** - View and manage past rides
- 👤 **Profile Management** - Update personal information and preferences

### 🚛 Driver Features
- 📊 **Ride Request Dashboard** - View and accept incoming ride requests
- 📍 **Route Navigation** - Integrated mapping for efficient route planning
- 💳 **Earnings Tracking** - Monitor ride earnings and performance
- 🔄 **Availability Toggle** - Control availability status
- 📱 **Real-Time Updates** - Live ride status updates

### 🔧 Technical Features
- 🌐 **WebView Integration** - Custom HTML/JavaScript maps with Leaflet.js
- 📧 **Email Integration** - Automated OTP delivery via JavaMail API
- 🗄️ **MySQL Database** - Robust backend data management
- 🔒 **Session Management** - Secure user session handling
- 📱 **Responsive Design** - Optimized for various screen sizes

---

## 🛠️ Technologies Used

### 🎯 Core Technologies
- **Java** - Primary programming language for Android development
- **XML** - Layout and resource management
- **Android SDK** - Native Android development framework
- **Gradle** - Build automation and dependency management

### 🗄️ Backend & Database
- **MySQL** - Relational database management
- **JDBC** - Database connectivity and operations
- **JavaMail API** - Email functionality for OTP delivery

### 🗺️ Location & Maps
- **Google Location Services** - GPS and location tracking
- **Leaflet.js** - Interactive mapping library
- **MapTiler API** - Geocoding and map tiles
- **WebView** - HTML/JavaScript integration

### 🎨 UI/UX
- **Material Design Components** - Modern Android UI components
- **RecyclerView** - Efficient list management
- **CardView** - Material design cards
- **SwipeRefreshLayout** - Pull-to-refresh functionality

### 🔧 Development Tools
- **Android Studio** - Integrated development environment
- **Git** - Version control system
- **Gradle** - Build system and dependency management

---

## 📱 Screenshots

<div align="center">

| ![Login Page](screenshots/login.png) | ![OTP Validation](screenshots/otp.png) | ![Sign Up Form](screenshots/signup.png) |
|:---:|:---:|:---:|
| **Login Page - Email Entry & OTP Send** | **OTP Validation** | **Sign Up Form Validation** |

---

| ![Map and Route Selection](screenshots/map_select.png) | ![Vehicle Types and Prices](screenshots/vehicle_prices.png) | ![Confirm Ride Selection](screenshots/confirm_ride.png) |
|:---:|:---:|:---:|
| **Map and Route Selection** | **Display Vehicle Types and Prices** | **Confirm Ride Selection** |

---

| ![Payment Method Selection](screenshots/payment.png) | ![Ride History](screenshots/ride_history.png) |
|:---:|:---:|
| **Payment Method Selection** | **Ride History - View Past** |

---

| ![Driver Login](screenshots/driver_login.png) | ![Driver Dashboard](screenshots/driver_dashboard.png) | ![New Ride Request](screenshots/ride_request.png) |
|:---:|:---:|:---:|
| **Driver Login with Valid Credentials** | **Driver Dashboard Overview** | **New Ride Request - Notification** |

---

| ![Accept Ride](screenshots/accept_ride.png) | ![Ongoing Ride](screenshots/ongoing_ride.png) | ![Complete Ride](screenshots/complete_ride.png) |
|:---:|:---:|:---:|
| **Accepting a Ride Request** | **Ongoing Ride Interface** | **Completing a Ride** |

---

| ![Ride History Access](screenshots/ride_history_access.png) |
|:---:|
| **Ride History Access** |

</div>

---

## 🏗️ Architecture

### 📱 Application Architecture
```
CabN Android App
├── Presentation Layer (Activities & Fragments)
│   ├── FirstActivity (Splash Screen)
│   ├── LoginActivity (Authentication)
│   ├── Registration (User Registration)
│   ├── MainActivity (User Dashboard)
│   ├── DriverMainActity (Driver Dashboard)
│   ├── Userprof (Profile Management)
│   ├── Userhist (Ride History)
│   └── SeeMap (Route Display)
├── Business Logic Layer
│   ├── DBConnection (Database Operations)
│   ├── EmailSender (OTP Delivery)
│   ├── LocationCheck (GPS Services)
│   └── LayoutShow (UI Logic)
├── Data Layer
│   ├── User (User Model)
│   ├── Rides (Ride Model)
│   ├── RideRequestsDriver (Request Model)
│   └── MySQL Database
└── UI Layer
    ├── XML Layouts
    ├── Material Components
    └── WebView Integration
```

### 🔄 Data Flow
1. **Authentication Flow**: Email → OTP → Login → Session Management
2. **Booking Flow**: Location Selection → Route Calculation → Driver Assignment → Ride Confirmation
3. **Driver Flow**: Request Reception → Route Planning → Ride Completion → Payment

---

## 🚀 Installation

### 📋 Prerequisites
- Android Studio (Latest Version)
- Java Development Kit (JDK 8 or higher)
- MySQL Server
- Android Device or Emulator (API Level 24+)

### 🔧 Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/Harshvardhan-Patil-264/CabNCarry_Application
cd CabNCarry
```

#### 2. Database Setup
```sql
-- Create database
CREATE DATABASE cabncarry;
USE cabncarry;

-- Create users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    mail VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    availability ENUM('avl', 'unavl') DEFAULT 'avl'
);

-- Create drivers table
CREATE TABLE drivers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    mail VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    license_number VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    availability_status ENUM('available', 'unavailable') DEFAULT 'available',
    rating DECIMAL(3,2) DEFAULT 0.00
);

-- Create OTP verification table
CREATE TABLE otp_verification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    otp INT NOT NULL,
    expires_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP + INTERVAL 10 MINUTE
);

-- Create rides table
CREATE TABLE rides (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    driver_id INT,
    start_location VARCHAR(255) NOT NULL,
    end_location VARCHAR(255) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    status ENUM('pending', 'accepted', 'completed', 'cancelled') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 3. Configure Database Connection
Update the database credentials in `DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://YOUR_SERVER_IP:3306/cabncarry?useSSL=false";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

#### 4. Configure Email Settings
Update email credentials in `EmailSender.java`:
```java
private static final String SENDER_EMAIL = "your_email@gmail.com";
private static final String APP_PASSWORD = "your_app_password";
```

#### 5. Build and Run
```bash
# Open in Android Studio
# Sync Gradle files
# Build the project
# Run on device/emulator
```

---

## ⚙️ Configuration

### 🔧 Environment Variables
```bash
# Database Configuration
DB_HOST=192.168.25.171
DB_PORT=3306
DB_NAME=cabncarry
DB_USER=root
DB_PASSWORD=****

# Email Configuration
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USER=********************
EMAIL_PASSWORD=********************

# Map Configuration
MAPTILER_KEY=EmsgGtSr2aDNx6zMJqRP
```

### 📱 Android Permissions
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 📖 Usage

### 👤 For Users
1. **Registration**: Create account with email and personal details
2. **Login**: Enter email and verify with OTP
3. **Book Ride**: Select pickup and drop-off locations on map
4. **Choose Vehicle**: Select from available vehicle types
5. **Confirm Booking**: Review fare and confirm ride
6. **Track Ride**: Monitor ride progress in real-time

### 🚛 For Drivers
1. **Login**: Authenticate with email and OTP
2. **View Requests**: See incoming ride requests
3. **Accept Rides**: Review and accept suitable rides
4. **Navigate**: Use integrated maps for route planning
5. **Complete Rides**: Mark rides as completed
6. **Manage Profile**: Update availability and personal info

---

## 🔧 API Integration

### 🗺️ MapTiler API
- **Purpose**: Geocoding and map tiles
- **Integration**: WebView with Leaflet.js
- **Features**: Location search, route planning, address geocoding

### 📧 Email API
- **Service**: JavaMail API with Gmail SMTP
- **Purpose**: OTP delivery for authentication
- **Security**: App-specific passwords for enhanced security

### 📍 Location Services
- **Provider**: Google Play Services Location API
- **Features**: Real-time GPS tracking, location permissions
- **Accuracy**: High-accuracy location updates

---

## 🗄️ Database Schema

### 📊 Core Tables

#### Users Table
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    mail VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    availability ENUM('avl', 'unavl') DEFAULT 'avl'
);
```

#### Drivers Table
```sql
CREATE TABLE drivers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    mail VARCHAR(100) UNIQUE NOT NULL,
    address TEXT NOT NULL,
    license_number VARCHAR(50) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    availability_status ENUM('available', 'unavailable') DEFAULT 'available',
    rating DECIMAL(3,2) DEFAULT 0.00
);
```

#### Rides Table
```sql
CREATE TABLE rides (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    driver_id INT,
    start_location VARCHAR(255) NOT NULL,
    end_location VARCHAR(255) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    fare DECIMAL(10,2) NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    status ENUM('pending', 'accepted', 'completed', 'cancelled') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔒 Security Features

### 🔐 Authentication Security
- **OTP-based Login**: Time-limited one-time passwords
- **Email Verification**: Secure email delivery system
- **Session Management**: Secure session handling with SharedPreferences
- **Input Validation**: Comprehensive input sanitization

### 🛡️ Data Security
- **SQL Injection Prevention**: Prepared statements for all database queries
- **Password Security**: App-specific passwords for email services
- **Permission Management**: Granular Android permissions
- **Data Encryption**: Secure data transmission

### 🔒 Privacy Protection
- **Location Privacy**: User consent for location access
- **Data Minimization**: Only necessary data collection
- **Secure Storage**: Encrypted SharedPreferences for sensitive data

---

## 📁 Project Structure

```
CabN/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cabn/
│   │   │   │   ├── Activities/
│   │   │   │   │   ├── FirstActivity.java
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── Registration.java
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   ├── DriverMainActity.java
│   │   │   │   │   ├── Userprof.java
│   │   │   │   │   ├── Userhist.java
│   │   │   │   │   └── SeeMap.java
│   │   │   │   ├── Models/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Rides.java
│   │   │   │   │   └── RideRequestsDriver.java
│   │   │   │   ├── Services/
│   │   │   │   │   ├── DBConnection.java
│   │   │   │   │   ├── EmailSender.java
│   │   │   │   │   └── LocationCheck.java
│   │   │   │   ├── Adapters/
│   │   │   │   │   ├── RideAdapterRecycling.java
│   │   │   │   │   └── MyRideAdapter.java
│   │   │   │   └── Utils/
│   │   │   │       └── LayoutShow.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_login.xml
│   │   │   │   │   ├── activity_registration.xml
│   │   │   │   │   ├── activity_userprof.xml
│   │   │   │   │   ├── activity_userhist.xml
│   │   │   │   │   ├── activity_driver_main_acitity.xml
│   │   │   │   │   └── toolbar.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   ├── menu/
│   │   │   │   │   └── usermenu.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_car_4.xml
│   │   │   │   │   ├── ic_car_6.xml
│   │   │   │   │   ├── ic_rickshaw.xml
│   │   │   │   │   ├── ic_tempo.xml
│   │   │   │   │   ├── ic_truck.xml
│   │   │   │   │   └── ic_logout.xml
│   │   │   │   └── xml/
│   │   │   │       ├── data_extraction_rules.xml
│   │   │   │       └── backup_rules.xml
│   │   │   ├── assets/
│   │   │   │   ├── map.html
│   │   │   │   └── mapp1.html
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── libs/
│   │   └── mysql-connector-java-5.1.49.jar
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

---

## 🤝 Contributing

We welcome contributions to improve CabN! Here's how you can contribute:

### 🔧 Development Setup
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 📝 Contribution Guidelines
- Follow Java coding conventions
- Add comments for complex logic
- Update documentation for new features
- Test thoroughly before submitting
- Ensure backward compatibility

### 🐛 Reporting Issues
- Use the GitHub issue tracker
- Provide detailed bug descriptions
- Include device and Android version information
- Attach relevant logs and screenshots

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Harshvardhan Vilas Patil

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Credits

<div align="center">

### 🚀 **Developer**

**Harshvardhan Vilas Patil**  
📧 **Email**: [harsh264patil@gmail.com](mailto:harsh264patil@gmail.com)  
🔗 **GitHub**: [@harsh264patil](https://github.com/Harshvardhan-Patil-264)

---

### 🛠️ **Technologies & Libraries**

| Technology | Purpose | Version |
|------------|---------|---------|
| **Android SDK** | Native Android Development | API 34 |
| **Java** | Programming Language | JDK 8+ |
| **MySQL** | Database Management | 8.0+ |
| **JavaMail API** | Email Services | 1.6.7 |
| **Google Location Services** | GPS & Location | 21.0.1 |
| **Material Design Components** | UI Framework | 1.12.0 |
| **Leaflet.js** | Interactive Maps | 1.9.4 |
| **MapTiler API** | Geocoding & Map Tiles | Latest |

---

### 🙏 **Special Thanks**

- **Android Developer Community** - For continuous support and resources
- **Material Design Team** - For the amazing UI components
- **Open Source Contributors** - For the libraries that made this possible
- **MapTiler** - For providing excellent mapping services
- **MySQL Community** - For robust database solutions

---

### 📞 **Contact & Support**

<div align="center">

[![Email](https://img.shields.io/badge/Email-harsh264patil%40gmail.com-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:harsh264patil@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/harsh264patil)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/harshvardhan-patil)

</div>

---

**⭐ If you find this project helpful, please give it a star!**

**🤝 Feel free to contribute and make this project even better!**

</div>

---

<div align="center">

**Made with ❤️ by Harshvardhan Vilas Patil**

*Building the future of ride booking, one line of code at a time.*

</div> 