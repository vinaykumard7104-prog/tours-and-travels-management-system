package tours.and.travels.management.systems.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Account;
import tours.and.travels.management.systems.util.Passwords;

/** Sign-up, sign-in, password recovery and account deletion. All queries are parameterised. */
public final class AccountDao {

    private AccountDao() { }

    public record Recovery(String name, String question) { }

    public static Account register(String username, String name, String password, String question, String answer)
            throws SQLException {
        try (Connection c = Database.open()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM account WHERE username = ?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) throw new DaoException("That username is already taken. Try another one.");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO account (username, name, password, security, answer) VALUES (?,?,?,?,?)")) {
                ps.setString(1, username);
                ps.setString(2, name);
                ps.setString(3, Passwords.hash(password));
                ps.setString(4, question);
                ps.setString(5, Passwords.hash(Passwords.normalizeAnswer(answer)));
                ps.executeUpdate();
            }
        }
        return new Account(username, name);
    }

    public static Optional<Account> login(String username, String password) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT username, name, password FROM account WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                String stored = rs.getString("password");
                if (!Passwords.verify(password, stored)) return Optional.empty();
                String canonical = rs.getString("username");
                String name = rs.getString("name");
                if (!Passwords.isHashed(stored)) {            // upgrade legacy plain-text password
                    try (PreparedStatement up = c.prepareStatement("UPDATE account SET password = ? WHERE username = ?")) {
                        up.setString(1, Passwords.hash(password));
                        up.setString(2, canonical);
                        up.executeUpdate();
                    }
                }
                return Optional.of(new Account(canonical, name == null || name.isBlank() ? canonical : name));
            }
        }
    }

    public static Optional<Recovery> findRecovery(String username) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT name, security FROM account WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new Recovery(rs.getString(1), rs.getString(2))) : Optional.empty();
            }
        }
    }

    /** @return true when the answer was right and the password was changed. */
    public static boolean resetPassword(String username, String answer, String newPassword) throws SQLException {
        try (Connection c = Database.open();
             PreparedStatement ps = c.prepareStatement("SELECT answer FROM account WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !Passwords.verifyAnswer(answer, rs.getString(1))) return false;
            }
            try (PreparedStatement up = c.prepareStatement(
                    "UPDATE account SET password = ?, answer = ? WHERE username = ?")) {
                up.setString(1, Passwords.hash(newPassword));
                up.setString(2, Passwords.hash(Passwords.normalizeAnswer(answer)));   // also upgrades legacy answers
                up.setString(3, username);
                up.executeUpdate();
            }
            return true;
        }
    }

    /** Removes the account and everything that belongs to it, atomically. */
    public static void deleteEverything(String username) throws SQLException {
        String[] tables = {"payment", "bookhotels", "bookpackage", "customer", "account"};
        try (Connection c = Database.open()) {
            c.setAutoCommit(false);
            try {
                for (String t : tables) {
                    try (PreparedStatement ps = c.prepareStatement("DELETE FROM " + t + " WHERE username = ?")) {
                        ps.setString(1, username);
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }
}
