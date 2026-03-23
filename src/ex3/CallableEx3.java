package ex3;

// Mặc định, JDBC coi các dấu ? là tham số đầu vào (IN). phải gọi registerOutParameter() để thông báo
// cho JDBC Driver biết vị trí nào là tham số đầu ra (OUT) và kiểu dữ liệu dự kiến trả về.
// Nếu không đăng ký, Driver sẽ không dành sẵn bộ nhớ để hứng giá trị trả về từ Stored Procedure,
// dẫn đến lỗi index hoặc không lấy được dữ liệu.

// Kiểu DECIMAL trong SQL tương ứng với java.sql.Types.DECIMAL (hoặc java.sql.Types.NUMERIC)
import ex1.PrepareStatementEx1;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CallableEx3 {
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
        int surgeryId = 505;
        String sql = "{call GET_SURGERY_FEE(?, ?)}";

        try (
            Connection conn = PrepareStatementEx1.openConnection();
            CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, surgeryId);

            cstmt.registerOutParameter(2, java.sql.Types.DECIMAL);
            cstmt.execute();
            double cost = cstmt.getDouble(2);

            System.out.printf("Chi phí phẫu thuật cho ca %d là: %.2f VNĐ%n", surgeryId, cost);

        } catch (SQLException e) {
            System.err.println("Lỗi thực thi thủ tục: " + e.getMessage());
        }
    }

}
