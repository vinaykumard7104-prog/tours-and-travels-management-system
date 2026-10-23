package tours.and.travels.management.systems.util;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Date formatting. */
public final class Fmt {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private Fmt() { }

    public static String date(Timestamp t) {
        return t == null ? "\u2014" : t.toLocalDateTime().format(DATE);
    }
}
