package ex5.model;

import java.sql.*;
import java.util.Scanner;

public class RHMSApp {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/rhms_db";
    private static final String USER = "root";
    private static final String PASS = "123456"; // Thay bằng pass của bạn

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== HỆ THỐNG QUẢN LÝ NỘI TRÚ RIKKEI-HOSPITAL ===");
            System.out.println("1. Danh sách bệnh nhân");
            System.out.println("2. Tiếp nhận bệnh nhân mới (Chống Injection)");
            System.out.println("3. Cập nhật bệnh án");
            System.out.println("4. Xuất viện & Tính phí (Stored Procedure)");
            System.out.println("5. Thoát");
            System.out.print("Chọn chức năng: ");
            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    showPatients();
                    break;
                case 2:
                    addPatient(sc);
                    break;
                case 3:
                    updatePatient(sc);
                    break;
                case 4:
                    dischargePatient(sc);
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }

    private static void showPatients() {
        String sql = "SELECT * FROM Patients";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("----------------------------------------------------------");
            System.out.println("| ID    | Tên Bệnh Nhân        | Tuổi  | Khoa Điều Trị   |");
            System.out.println("----------------------------------------------------------");
            while (rs.next()) {
                System.out.println(new Patient(rs.getInt("p_id"), rs.getString("p_name"),
                        rs.getInt("p_age"), rs.getString("department")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Thêm mới - Xử lý dấu nháy (L'Oréal)
    private static void addPatient(Scanner sc) {
        System.out.print("Nhập tên bệnh nhân: ");
        String name = sc.nextLine();
        System.out.print("Nhập tuổi: ");
        int age = Integer.parseInt(sc.nextLine());
        System.out.print("Khoa điều trị: ");
        String dept = sc.nextLine();

        String sql = "INSERT INTO Patients(p_name, p_age, department) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, dept);
            pstmt.executeUpdate();
            System.out.println("Tiếp nhận bệnh nhân thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void dischargePatient(Scanner sc) {
        System.out.print("Nhập mã ID bệnh nhân xuất viện: ");
        int id = Integer.parseInt(sc.nextLine());

        String sql = "{call CALCULATE_DISCHARGE_FEE(?, ?)}";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, id);
            cstmt.registerOutParameter(2, Types.DECIMAL);

            cstmt.execute();
            double totalFee = cstmt.getDouble(2);

            System.out.println("--------------------------------------");
            System.out.printf("Tổng viện phí phải thanh toán: %,.0f VNĐ%n", totalFee);
            System.out.println("--------------------------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void updatePatient(Scanner sc) {
        System.out.print("Nhập mã ID bệnh nhân cần cập nhật: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Nhập tên mới (để trống nếu không đổi): ");
        String newName = sc.nextLine();
        System.out.print("Nhập tuổi mới (nhập 0 nếu không đổi): ");
        int newAge = Integer.parseInt(sc.nextLine());
        System.out.print("Nhập khoa mới (để trống nếu không đổi): ");
        String newDept = sc.nextLine();

        String sql = "UPDATE Patients SET p_name = IF(? = '', p_name, ?), " +
                "p_age = IF(? = 0, p_age, ?), " +
                "department = IF(? = '', department, ?) " +
                "WHERE p_id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newName);
            pstmt.setString(2, newName);
            pstmt.setInt(3, newAge);
            pstmt.setInt(4, newAge);
            pstmt.setString(5, newDept);
            pstmt.setString(6, newDept);
            pstmt.setInt(7, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Cập nhật bệnh án thành công cho ID: " + id);
            } else {
                System.out.println("Không tìm thấy bệnh nhân có ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật: " + e.getMessage());
        }
    }
}
