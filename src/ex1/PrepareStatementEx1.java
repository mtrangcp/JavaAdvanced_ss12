package ex1;

import java.sql.*;

// 1. Cơ chế "Pre-compiled" (Biên dịch trước)
// sử dụng Statement thông thường, SQL engine phải biên dịch (phân tích cú pháp) câu lệnh mỗi khi nó được thực thi.
// PreparedStatement, cấu trúc của câu lệnh SQL được gửi đến database và biên dịch trước với các tham số giữ chỗ (?)

// 2. Tách biệt Dữ liệu và Lệnh -> an toàn
// Trong lỗi SQL Injection, kẻ tấn công nhập ' OR '1'='1 để thay đổi logic của câu lệnh.
// Với nối chuỗi: Câu lệnh trở thành ... WHERE pass = '' OR '1'='1', biến điều kiện luôn đúng.
// Với PreparedStatement: Hệ thống sẽ tìm kiếm một mật khẩu có giá trị đúng bằng chuỗi ' OR '1'='1.
// Dấu nháy đơn sẽ được tự động "escape" (vô hiệu hóa), khiến nó không thể phá vỡ cấu trúc câu lệnh ban đầu.
public class PrepareStatementEx1 {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/btvn_ss12?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    public static Connection openConnection(){

        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL,USERNAME,PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Chua cai dat mysql driver");
        } catch (SQLException e) {
            System.err.println("Loi SQL : ket noi that bai");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String code = "BS01";
        String pass = "123456";
        String sql = "SELECT * FROM Doctors WHERE code = ? AND pass = ?";

        try (
                Connection conn = PrepareStatementEx1.openConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setString(2, pass);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Đăng nhập thành công!");
                } else {
                    System.out.println("Sai mã bác sĩ hoặc mật khẩu.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
