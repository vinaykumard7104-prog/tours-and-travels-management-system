package tours.and.travels.management.systems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Payment;

/** Payment history (payments in this app are simulated - no real money moves). */
public final class PaymentDao {

    private PaymentDao() { }

    public static void add(String username, int amount, String method, String reference) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO payment (username, amount, method, reference) VALUES (?,?,?,?)")) {
            ps.setString(1, username);
            ps.setInt(2, amount);
            ps.setString(3, method);
            ps.setString(4, reference);
            ps.executeUpdate();
        }
    }

    public static List<Payment> list(String username) throws SQLException {
        List<Payment> out = new ArrayList<>();
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT payment_id, amount, method, reference, paid_on "
                     + "FROM payment WHERE username = ? ORDER BY payment_id DESC")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Payment(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getTimestamp(5)));
                }
            }
        }
        return out;
    }
}
