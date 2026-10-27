import java.sql.*;
import java.util.Scanner;

public class StudentEnrollmentSystem {

    // Database connection details
    static final String URL = "jdbc:mysql://localhost:3306/college_db";
    static final String USER = "root";
    static final String PASSWORD = "your_password";

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Add a new student
    static void addStudent(Scanner sc) {
        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        System.out.print("Enter student email: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO students(name, email) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);

            ps.executeUpdate();
            System.out.println("Student added successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Display all students
    static void viewStudents() {
        String sql = "SELECT * FROM students";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- Students ---");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("student_id") +
                    " | Name: " + rs.getString("name") +
                    " | Email: " + rs.getString("email")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    static void viewCourses() {
        String sql = "SELECT * FROM courses";

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- Courses ---");

            while (rs.next()) {
                System.out.println(
                    "ID: " + rs.getInt("course_id") +
                    " | Course: " + rs.getString("course_name") +
                    " | Credits: " + rs.getInt("credits")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Enroll student in a course
    static void enrollStudent(Scanner sc) {

        System.out.print("Enter student ID: ");
        int studentId = sc.nextInt();

        System.out.print("Enter course ID: ");
        int courseId = sc.nextInt();

        String sql = """
                INSERT INTO enrollments
                (student_id, course_id, enrollment_date)
                VALUES (?, ?, ?)
                """;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, Date.valueOf(java.time.LocalDate.now()));

            ps.executeUpdate();

            System.out.println("Student enrolled successfully!");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Student is already enrolled in this course.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View enrollments
    static void viewEnrollments() {

        String sql = """
                SELECT e.enrollment_id,
                       s.student_id,
                       s.name,
                       c.course_id,
                       c.course_name,
                       e.enrollment_date
                FROM enrollments e
                JOIN students s ON e.student_id = s.student_id
                JOIN courses c ON e.course_id = c.course_id
                """;

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n--- Enrollment Details ---");

            while (rs.next()) {
                System.out.println(
                    "Enrollment ID: " + rs.getInt("enrollment_id") +
                    " | Student ID: " + rs.getInt("student_id") +
                    " | Student: " + rs.getString("name") +
                    " | Course: " + rs.getString("course_name") +
                    " | Date: " + rs.getDate("enrollment_date")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Remove enrollment
    static void removeEnrollment(Scanner sc) {

        System.out.print("Enter enrollment ID: ");
        int enrollmentId = sc.nextInt();

        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Enrollment removed successfully!");
            } else {
                System.out.println("Enrollment not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n================================");
            System.out.println(" STUDENT ENROLLMENT SYSTEM");
            System.out.println("================================");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. View Courses");
            System.out.println("4. Enroll Student");
            System.out.println("5. View Enrollments");
            System.out.println("6. Remove Enrollment");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addStudent(sc);
                    break;

                case 2:
                    viewStudents();
                    break;

                case 3:
                    viewCourses();
                    break;

                case 4:
                    enrollStudent(sc);
                    break;

                case 5:
                    viewEnrollments();
                    break;

                case 6:
                    removeEnrollment(sc);
                    break;

                case 7:
                    System.out.println("Thank you!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}