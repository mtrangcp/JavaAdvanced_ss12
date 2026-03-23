package ex4;

// lãng phí tài nguyên
// ùng Statement trong vòng lặp, Database Server phải thực hiện quy trình sau 1.000 lần:
// Sytax Check & Parsing: Kiểm tra cú pháp và phân tích câu lệnh chuỗi.
// Hashing & Searching: Tìm xem câu lệnh này đã có trong vùng nhớ đệm (Plan Cache) chưa.
// Vì giá trị dữ liệu (tr.getData()) thay đổi liên tục, Database coi mỗi câu lệnh là duy nhất và không thể dùng lại kế hoạch cũ.
// Execution Plan Creation: Phải tính toán lại đường đi tối ưu để ghi dữ liệu vào bảng (tốn CPU và RAM).
// --> lãng phí kết nối: Tạo mới đối tượng Statement liên tục gây áp lực lên bộ nhớ của Java (Heap space) và băng thông mạng.

// --> PreparedStatement giúp Database chỉ cần thực hiện bước 1-3 duy nhất một lần (Prepare), sau đó chỉ việc "đổ" dữ liệu vào các vị trí ? để chạy (Execute).
import ex1.PrepareStatementEx1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreStateEx4 {
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
        List<TestResult> list = new ArrayList<>();
        list.add(new TestResult("nGuyen van A"));
        list.add(new TestResult("Nguyen thi b"));
        list.add(new TestResult("Le thi c"));
        String sql = "INSERT INTO Results(data) VALUES (?)";

            Connection conn = PrepareStatementEx1.openConnection();
        try (
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);

            for (TestResult tr : list) {
                pstmt.setString(1, tr.getData());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();

            System.out.println("Thành công");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            e.printStackTrace();
        }
    }
}
