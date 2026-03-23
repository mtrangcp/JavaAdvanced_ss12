package ex2;

import ex1.PrepareStatementEx1;

import java.sql.*;

// dùng nối chuỗi (+ temp +), Java sẽ gọi hàm toString() của biến temp. Hàm này phụ thuộc vào cấu hình
// hệ điều hành (Locale). Nếu hệ thống dùng Locale tiếng Việt, 37.5 sẽ bị biến thành "37,5",
// khiến câu lệnh SQL trở thành SET temperature = 37,5 (sai cú pháp SQL vì dấu phẩy được hiểu là ngăn cách các cột)

// Truyền tham số kiểu Binary: Các phương thức setDouble() hoặc setInt() không biến số thành chuỗi văn bản.
// Thay vào đó, chúng truyền giá trị dưới dạng dữ liệu nhị phân trực tiếp vào Driver.
public class PreStatementEx2 {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/btvn_ss12?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static Connection openConnection() {

        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Chua cai dat mysql driver");
        } catch (SQLException e) {
            System.err.println("Loi SQL : ket noi that bai");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        double temp = 37.5;
        int heartRate = 85;
        int patientId = 101;

        String sql = "UPDATE Vitals SET temperature = ?, heart_rate = ? WHERE p_id = ?";

        try (
                Connection conn = PrepareStatementEx1.openConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Truyền dữ liệu trực tiếp, không lo lắng về Locale
            pstmt.setDouble(1, temp);
            pstmt.setInt(2, heartRate);
            pstmt.setInt(3, patientId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Cập nhật thành công cho " + rowsAffected + " bệnh nhân.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
