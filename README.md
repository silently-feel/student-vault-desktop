# student-vault-desktop

A clean, lightweight, and native Java Swing desktop application built from scratch to manage student records with a direct MySQL database connection via JDBC.

## 🚀 Features

- **Full CRUD Operations:** Add, update, delete, and view student records seamlessly.
- **Binary Image Storage:** Save and load student profile pictures as `LONGBLOB` data directly inside MySQL.
- **Live Search & Counter:** Filter records instantly by name/course and track total student counts in real-time.
- **CSV Export Utility:** Export active table data directly to a spreadsheet-ready `.csv` file.

## 🛠️ Tech Stack

- **Language:** Java
- **UI Framework:** Java Swing
- **Database Connectivity:** JDBC (`PreparedStatement` for security)
- **Database:** MySQL

## 📂 Project Structure

```text
├── Student/
│   ├── DBConnection.java   # MySQL connection manager
│   ├── Student.java        # Student domain model / POJO
│   ├── StudentDAO.java     # Database CRUD logic & secure queries
│   └── MainForm.java       # Swing UI, layout, and event handling
├── lib/
│   └── mysql-connector...jar # MySQL JDBC Driver
└── README.md
⚙️ Setup and Installation Instructions
1. Database Configuration
Before running the application, you must set up your MySQL database and table structure. Open your MySQL client (like MySQL Workbench or terminal) and run the following script:

SQL
-- Create the database
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

-- Create the student table with binary image support
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    course VARCHAR(100) NOT NULL,
    image LONGBLOB
);
2. Configure Database Credentials
Open your DBConnection.java file inside the Student package and update your local MySQL username and password so it matches your environment:

Java
package Student;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";             // Your MySQL username
    private static final String PASSWORD = "your_password"; // Your local MySQL password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
3. How to Run Locally
Clone or download this repository.

Open the project folder in IntelliJ IDEA or any standard Java IDE.

Ensure the MySQL JDBC Driver (.jar) is added to your project's libraries/build path.

Run the main application file: MainForm.java.