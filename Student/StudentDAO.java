package Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // 1. INSERT a new student into the database
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (name, age, course, image) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getAge());
            pstmt.setString(3, student.getCourse());
            pstmt.setBytes(4, student.getImage()); // Saves the image bytes into the LONGBLOB column

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. SELECT (Retrieve) all students from the database
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String sql = "SELECT * FROM students";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setName(rs.getString("name"));
                student.setAge(rs.getInt("age"));
                student.setCourse(rs.getString("course"));
                student.setImage(rs.getBytes("image")); // Retrieves the image bytes from LONGBLOB

                studentList.add(student);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentList;
    }

    // 3. UPDATE an existing student
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, age = ?, course = ?, image = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setInt(2, student.getAge());
            pstmt.setString(3, student.getCourse());
            pstmt.setBytes(4, student.getImage());
            pstmt.setInt(5, student.getId());

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. DELETE a student by ID
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //5. Search students by name or course
    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE name LIKE ? OR course LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                students.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("course"),
                        rs.getBytes("image")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}