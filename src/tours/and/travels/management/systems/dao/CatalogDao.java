package tours.and.travels.management.systems.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Hotel;
import tours.and.travels.management.systems.model.Models.TravelPackage;
import tours.and.travels.management.systems.util.Money;

/** Read-only catalogue of packages and hotels. */
public final class CatalogDao {

    private CatalogDao() { }

    public static List<TravelPackage> packages() throws SQLException {
        List<TravelPackage> out = new ArrayList<>();
        try (Connection c = Database.open(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, tagline, duration, price, season, features, image "
                     + "FROM travelpackage ORDER BY price")) {
            while (rs.next()) {
                String f = rs.getString("features");
                List<String> features = f == null || f.isBlank() ? List.of() : Arrays.asList(f.split("\\|"));
                out.add(new TravelPackage(rs.getString("name"), rs.getString("tagline"), rs.getString("duration"),
                        rs.getInt("price"), rs.getString("season"), features, rs.getString("image")));
            }
        }
        return out;
    }

    public static List<Hotel> hotels() throws SQLException {
        List<Hotel> out = new ArrayList<>();
        try (Connection c = Database.open(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, costperperson, foodincluded, acroom FROM hotel")) {
            int i = 0;
            while (rs.next()) {
                String name = rs.getString("name");
                out.add(new Hotel(name, Money.parse(rs.getString("costperperson")),
                        Money.parse(rs.getString("foodincluded")), Money.parse(rs.getString("acroom")),
                        imageFor(name, i++)));
            }
        }
        return out;
    }

    /** Maps a hotel name (even the misspelt ones from the old data) to one of the bundled photos. */
    static String imageFor(String name, int index) {
        String n = name == null ? "" : name.toLowerCase(Locale.ROOT);
        int k;
        if (n.contains("marriott")) k = 1;
        else if (n.contains("mandarin") || n.contains("madarin")) k = 2;
        else if (n.contains("four seasons")) k = 3;
        else if (n.contains("radisson") || n.contains("raddisson")) k = 4;
        else if (n.contains("classico")) k = 5;
        else if (n.contains("bay club")) k = 6;
        else if (n.contains("breeze")) k = 7;
        else if (n.contains("taj")) k = 8;
        else if (n.contains("happy morning")) k = 9;
        else if (n.contains("river view")) k = 10;
        else k = (index % 10) + 1;
        return "hotel" + k + ".jpg";
    }
}
