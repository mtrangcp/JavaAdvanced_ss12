package ex6;

import java.sql.*;

public class PharmacyManager {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_pharmacy";
    private static final String USER = "root";
    private static final String PASS = "123456";

    public static void main(String[] args) {
        updateMedicineStock(1, 50);
        findMedicinesByPriceRange(10.0, 500.0);
        getPrescriptionTotal(1);
        getDailyRevenue("2026-03-24");
    }

    public static Connection openConnection() {

        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            System.err.println("Chua cai dat mysql driver");
        } catch (SQLException e) {
            System.err.println("Loi SQL : ket noi that bai");
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật
    public static void updateMedicineStock(int id, int addedQuantity) {
        String sql = "UPDATE medicines SET stock = stock + ? WHERE id = ?";
        try (Connection conn = PharmacyManager.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, addedQuantity);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Cập nhật kho thành công!");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Tìm theo khoảng giá
    public static void findMedicinesByPriceRange(double minPrice, double maxPrice) {
        String sql = "SELECT * FROM medicines WHERE price BETWEEN ? AND ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, minPrice);
            pstmt.setDouble(2, maxPrice);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("Danh sách thuốc trong khoảng giá:");
            while (rs.next()) {
                System.out.printf("ID: %d | Tên: %s | Giá: %.2f | Kho: %d%n",
                        rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("stock"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // tổng tiền đơn thuốc
    public static void getPrescriptionTotal(int prescriptionId) {
        String sql = "{call CalculatePrescriptionTotal(?, ?)}";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, prescriptionId);
            cstmt.registerOutParameter(2, Types.DECIMAL);

            cstmt.execute();
            double total = cstmt.getDouble(2);
            System.out.println("Tổng tiền đơn thuốc ID " + prescriptionId + " là: " + total);

        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Doanh thu theo ngày
    public static void getDailyRevenue(String dateStr) {
        String sql = "{call GetDailyRevenue(?, ?)}";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setDate(1, Date.valueOf(dateStr));
            cstmt.registerOutParameter(2, Types.DECIMAL);

            cstmt.execute();
            double revenue = cstmt.getDouble(2);
            System.out.printf("Doanh thu ngày %s là: %,.2f VNĐ%n", dateStr, revenue);

        } catch (SQLException e) { e.printStackTrace(); }
    }

}
