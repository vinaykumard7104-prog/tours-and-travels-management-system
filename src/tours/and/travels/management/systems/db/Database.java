package tours.and.travels.management.systems.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;

/** Connection factory + automatic schema creation / migration. */
public final class Database {

    private static volatile DbConfig config = DbConfig.load();

    private Database() { }

    public static DbConfig config() { return config; }

    public static void reloadConfig() { config = DbConfig.load(); }

    /** Opens a new connection. Callers must close it (try-with-resources). */
    public static Connection open() throws SQLException {
        DbConfig c = config;
        return DriverManager.getConnection(c.databaseUrl(), c.user, c.password);
    }

    /** Creates the database + tables if necessary and seeds the catalogue. Safe to call on every start. */
    public static void initialize() throws SQLException {
        DbConfig c = config;
        if (!safeName(c.database)) throw new SQLException("Invalid database name: " + c.database);
        try (Connection server = DriverManager.getConnection(c.serverUrl(), c.user, c.password);
             Statement st = server.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + c.database
                    + "` DEFAULT CHARACTER SET utf8mb4");
        }
        try (Connection con = open()) {
            Schema.apply(con);
        }
    }

    private static boolean safeName(String n) { return n != null && n.matches("[A-Za-z0-9_]{1,64}"); }

    /** Converts low-level exceptions into a sentence a human can act on. */
    public static String explain(Throwable t) {
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) root = root.getCause();
        String msg = String.valueOf(t.getMessage());
        DbConfig c = config;
        if (t instanceof SQLException se) {
            if ("28000".equals(se.getSQLState()) || msg.contains("Access denied")) {
                return "MySQL rejected the user name or password (user '" + c.user + "'). "
                        + "Open Database settings and enter the password you use in MySQL Workbench.";
            }
            if (msg.contains("No suitable driver")) {
                return "The MySQL JDBC driver was not found. In NetBeans: right-click the project > "
                        + "Properties > Libraries and make sure lib/mysql-connector-java-8.0.28.jar is listed.";
            }
            if (se instanceof SQLNonTransientConnectionException || "08S01".equals(se.getSQLState())
                    || root instanceof java.net.ConnectException || msg.contains("Communications link failure")) {
                return "Cannot reach MySQL at " + c.host + ":" + c.port + ". Start the MySQL service "
                        + "(Windows: Services > MySQL80 > Start) and try again.";
            }
        }
        return msg;
    }

    // ------------------------------------------------------------------------------------------
    /** Table definitions. Names/columns match the original project so existing data keeps working. */
    static final class Schema {

        private Schema() { }

        static void apply(Connection con) throws SQLException {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS account ("
                        + "username VARCHAR(50) NOT NULL PRIMARY KEY, name VARCHAR(100), "
                        + "password VARCHAR(255), security VARCHAR(150), answer VARCHAR(255)"
                        + ") DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS customer ("
                        + "username VARCHAR(50), id VARCHAR(50), number VARCHAR(60), name VARCHAR(100), "
                        + "gender VARCHAR(20), country VARCHAR(60), address VARCHAR(255), "
                        + "phone VARCHAR(25), email VARCHAR(120)) DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS bookpackage ("
                        + "booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), "
                        + "`package` VARCHAR(60), persons VARCHAR(10), id VARCHAR(50), number VARCHAR(60), "
                        + "phone VARCHAR(25), price VARCHAR(30), "
                        + "booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP) DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS bookhotels ("
                        + "booking_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), "
                        + "name VARCHAR(100), persons VARCHAR(10), days VARCHAR(10), ac VARCHAR(10), "
                        + "food VARCHAR(10), id VARCHAR(50), number VARCHAR(60), phone VARCHAR(25), "
                        + "price VARCHAR(30), booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ") DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS hotel ("
                        + "name VARCHAR(100), costperperson VARCHAR(20), foodincluded VARCHAR(20), "
                        + "acroom VARCHAR(20)) DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS travelpackage ("
                        + "name VARCHAR(60) NOT NULL PRIMARY KEY, tagline VARCHAR(120), duration VARCHAR(60), "
                        + "price INT NOT NULL, season VARCHAR(40), features TEXT, image VARCHAR(120)"
                        + ") DEFAULT CHARSET=utf8mb4");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS payment ("
                        + "payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) NOT NULL, "
                        + "amount INT NOT NULL, method VARCHAR(30), reference VARCHAR(40), "
                        + "paid_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP) DEFAULT CHARSET=utf8mb4");
            }

            // --- migrations for databases created by the 3-year-old version ---
            widen(con, "account", "password", "VARCHAR(255)");
            widen(con, "account", "security", "VARCHAR(150)");
            widen(con, "account", "answer", "VARCHAR(255)");
            addColumn(con, "bookpackage", "booking_id",
                    "ADD COLUMN booking_id INT NOT NULL AUTO_INCREMENT, ADD UNIQUE KEY uq_bookpackage_id (booking_id)");
            addColumn(con, "bookpackage", "booked_on",
                    "ADD COLUMN booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
            addColumn(con, "bookhotels", "booking_id",
                    "ADD COLUMN booking_id INT NOT NULL AUTO_INCREMENT, ADD UNIQUE KEY uq_bookhotels_id (booking_id)");
            addColumn(con, "bookhotels", "booked_on",
                    "ADD COLUMN booked_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");

            seedPackages(con);
            seedHotels(con);
        }

        private static boolean columnExists(Connection con, String table, String column) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM information_schema.columns "
                    + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?")) {
                ps.setString(1, table);
                ps.setString(2, column);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }

        private static void addColumn(Connection con, String table, String column, String alter) {
            try {
                if (!columnExists(con, table, column)) {
                    try (Statement st = con.createStatement()) {
                        st.executeUpdate("ALTER TABLE " + table + " " + alter);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[Schema] could not add " + table + "." + column + ": " + e.getMessage());
            }
        }

        private static void widen(Connection con, String table, String column, String type) {
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE " + table + " MODIFY " + column + " " + type);
            } catch (SQLException e) {
                System.err.println("[Schema] could not widen " + table + "." + column + ": " + e.getMessage());
            }
        }

        private static boolean isEmpty(Connection con, String table) throws SQLException {
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table)) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }

        private static void seedPackages(Connection con) throws SQLException {
            if (!isEmpty(con, "travelpackage")) return;
            String sql = "INSERT INTO travelpackage (name, tagline, duration, price, season, features, image) "
                    + "VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                pkg(ps, "Gold Package", "Beach & island escape", "6 Days and 7 Nights", 12000, "SUMMER SPECIAL",
                        "Airport Assistance|Half Day City Tour|Daily Buffet|Soft Drinks|Full Day 3 Island Cruise|English Speaking Guide",
                        "package1.jpg");
                pkg(ps, "Silver Package", "Mountains & night safari", "5 Days and 6 Nights", 24000, "WINTER SPECIAL",
                        "Toll Free|Entrance Free Tickets|Meet And Greet at Airport|Welcome Drinks on Arrival|Night Safari|Cruise with Dinner",
                        "package2.jpg");
                pkg(ps, "Bronze Package", "Backwaters & adventure", "6 Days and 5 Nights", 32000, "WINTER SPECIAL",
                        "Return Airfare|Free Clubbing, Horse Riding & Games|River Rafting|Hard Drinks Free|Daily Buffet|BBQ Dinner",
                        "package3.jpg");
                ps.executeBatch();
            }
        }

        private static void pkg(PreparedStatement ps, String name, String tag, String dur, int price, String season,
                                String features, String image) throws SQLException {
            ps.setString(1, name);
            ps.setString(2, tag);
            ps.setString(3, dur);
            ps.setInt(4, price);
            ps.setString(5, season);
            ps.setString(6, features);
            ps.setString(7, image);
            ps.addBatch();
        }

        private static void seedHotels(Connection con) throws SQLException {
            if (!isEmpty(con, "hotel")) return;
            Object[][] rows = {
                {"JW Marriott Hotel", 4500, 800, 700},
                {"Mandarin Oriental Hotel", 5200, 900, 800},
                {"Four Seasons Hotel", 5800, 1000, 900},
                {"Radisson Blue Hotel", 3200, 600, 500},
                {"Classico Hotel", 1800, 400, 350},
                {"The Bay Club Hotel", 2600, 500, 450},
                {"Breeze Blow Hotel", 1500, 350, 300},
                {"The Taj Hotel", 6500, 1100, 1000},
                {"Happy Morning Motel", 900, 250, 200},
                {"River View Hotel", 2100, 450, 400},
            };
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO hotel (name, costperperson, foodincluded, acroom) VALUES (?,?,?,?)")) {
                for (Object[] r : rows) {
                    ps.setString(1, (String) r[0]);
                    ps.setString(2, String.valueOf(r[1]));
                    ps.setString(3, String.valueOf(r[2]));
                    ps.setString(4, String.valueOf(r[3]));
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }
}
