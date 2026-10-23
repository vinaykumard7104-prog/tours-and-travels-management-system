package tours.and.travels.management.systems.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Database connection settings.
 * Resolution order (later wins): built-in defaults, ./db.properties (project folder),
 * ~/.tours-travels/db.properties (written by the in-app "Database settings" dialog).
 */
public final class DbConfig {

    public String host = "localhost";
    public String port = "3306";
    public String database = "travelmanagementsystem";
    public String user = "root";
    public String password = "";

    public static Path userFile() {
        return Paths.get(System.getProperty("user.home"), ".tours-travels", "db.properties");
    }

    public static DbConfig load() {
        DbConfig c = new DbConfig();
        c.merge(Paths.get("db.properties"));                       // project folder (NetBeans "Run")
        Path jarDir = jarDirectory();
        if (jarDir != null) {                                      // dist/xyz.jar launched from anywhere
            c.merge(jarDir.resolve("db.properties"));
            if (jarDir.getParent() != null) c.merge(jarDir.getParent().resolve("db.properties"));
        }
        c.merge(userFile());
        return c;
    }

    private static Path jarDirectory() {
        try {
            Path p = Paths.get(DbConfig.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return Files.isRegularFile(p) ? p.getParent() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void merge(Path file) {
        if (!Files.isRegularFile(file)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("[DbConfig] cannot read " + file + ": " + e.getMessage());
            return;
        }
        host = p.getProperty("db.host", host).trim();
        port = p.getProperty("db.port", port).trim();
        database = p.getProperty("db.name", database).trim();
        user = p.getProperty("db.user", user).trim();
        password = p.getProperty("db.password", password);
    }

    public void save() throws IOException {
        Path file = userFile();
        Files.createDirectories(file.getParent());
        Properties p = new Properties();
        p.setProperty("db.host", host);
        p.setProperty("db.port", port);
        p.setProperty("db.name", database);
        p.setProperty("db.user", user);
        p.setProperty("db.password", password);
        try (OutputStream out = Files.newOutputStream(file)) {
            p.store(out, "Tours & Travels - database settings");
        }
    }

    private static final String OPTIONS =
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
            + "&useUnicode=true&characterEncoding=UTF-8&connectTimeout=5000";

    /** URL without a schema - used to create the database when it does not exist yet. */
    public String serverUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/?" + OPTIONS;
    }

    public String databaseUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + OPTIONS;
    }
}
