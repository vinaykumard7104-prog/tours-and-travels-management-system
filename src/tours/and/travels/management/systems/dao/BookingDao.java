package tours.and.travels.management.systems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Customer;
import tours.and.travels.management.systems.model.Models.HotelBooking;
import tours.and.travels.management.systems.model.Models.PackageBooking;
import tours.and.travels.management.systems.model.Models.Stats;
import tours.and.travels.management.systems.util.Money;

/** Package and hotel bookings, plus the summary numbers shown on the overview page. */
public final class BookingDao {

    private BookingDao() { }

    public static void bookPackage(String username, Customer cu, String packageName, int persons, int total)
            throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("INSERT INTO bookpackage "
                     + "(username, `package`, persons, id, number, phone, price) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, packageName);
            ps.setString(3, String.valueOf(persons));
            ps.setString(4, cu.idType());
            ps.setString(5, cu.idNumber());
            ps.setString(6, cu.phone());
            ps.setString(7, String.valueOf(total));
            ps.executeUpdate();
        }
    }

    public static void bookHotel(String username, Customer cu, String hotel, int persons, int days, boolean ac,
                                 boolean food, int total) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("INSERT INTO bookhotels "
                     + "(username, name, persons, days, ac, food, id, number, phone, price) VALUES (?,?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, username);
            ps.setString(2, hotel);
            ps.setString(3, String.valueOf(persons));
            ps.setString(4, String.valueOf(days));
            ps.setString(5, ac ? "AC" : "Non-AC");
            ps.setString(6, food ? "Yes" : "No");
            ps.setString(7, cu.idType());
            ps.setString(8, cu.idNumber());
            ps.setString(9, cu.phone());
            ps.setString(10, String.valueOf(total));
            ps.executeUpdate();
        }
    }

    public static List<PackageBooking> packageBookings(String username) throws SQLException {
        List<PackageBooking> out = new ArrayList<>();
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT booking_id, `package`, persons, price, booked_on "
                     + "FROM bookpackage WHERE username = ? ORDER BY booking_id DESC")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new PackageBooking(rs.getInt(1), rs.getString(2), Money.parse(rs.getString(3)),
                            Money.parse(rs.getString(4)), rs.getTimestamp(5)));
                }
            }
        }
        return out;
    }

    public static List<HotelBooking> hotelBookings(String username) throws SQLException {
        List<HotelBooking> out = new ArrayList<>();
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT booking_id, name, persons, days, ac, food, price, booked_on "
                     + "FROM bookhotels WHERE username = ? ORDER BY booking_id DESC")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new HotelBooking(rs.getInt(1), rs.getString(2), Money.parse(rs.getString(3)),
                            Money.parse(rs.getString(4)), rs.getString(5), rs.getString(6),
                            Money.parse(rs.getString(7)), rs.getTimestamp(8)));
                }
            }
        }
        return out;
    }

    public static void cancelPackage(String username, int bookingId) throws SQLException {
        cancel("bookpackage", username, bookingId);
    }

    public static void cancelHotel(String username, int bookingId) throws SQLException {
        cancel("bookhotels", username, bookingId);
    }

    private static void cancel(String table, String username, int bookingId) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("DELETE FROM " + table + " WHERE booking_id = ? AND username = ?")) {
            ps.setInt(1, bookingId);
            ps.setString(2, username);
            ps.executeUpdate();
        }
    }

    public static Stats stats(String username) throws SQLException {
        int trips = 0, stays = 0, booked = 0, paid = 0;
        try (Connection c = Database.open()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT price FROM bookpackage WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) { trips++; booked += Money.parse(rs.getString(1)); }
                }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT price FROM bookhotels WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) { stays++; booked += Money.parse(rs.getString(1)); }
                }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT COALESCE(SUM(amount),0) FROM payment WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) paid = rs.getInt(1);
                }
            }
        }
        return new Stats(trips, stays, booked, paid);
    }
}
