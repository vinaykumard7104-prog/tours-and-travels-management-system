package tours.and.travels.management.systems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Customer;

/** Traveller profile (one per account). */
public final class CustomerDao {

    private CustomerDao() { }

    public static Optional<Customer> find(String username) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT username, id, number, name, gender, country, address, phone, email "
                     + "FROM customer WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Customer(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
            }
        }
    }

    /** Insert or update - always limited to this one user (the old version updated every row). */
    public static void save(Customer cu) throws SQLException {
        try (Connection c = Database.open()) {
            boolean exists;
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM customer WHERE username = ?")) {
                ps.setString(1, cu.username());
                try (ResultSet rs = ps.executeQuery()) { exists = rs.next(); }
            }
            String sql = exists
                    ? "UPDATE customer SET id=?, number=?, name=?, gender=?, country=?, address=?, phone=?, email=? WHERE username=?"
                    : "INSERT INTO customer (id, number, name, gender, country, address, phone, email, username) "
                      + "VALUES (?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, cu.idType());
                ps.setString(2, cu.idNumber());
                ps.setString(3, cu.name());
                ps.setString(4, cu.gender());
                ps.setString(5, cu.country());
                ps.setString(6, cu.address());
                ps.setString(7, cu.phone());
                ps.setString(8, cu.email());
                ps.setString(9, cu.username());
                ps.executeUpdate();
            }
        }
    }
}
