package tours.and.travels.management.systems.util;

import java.util.regex.Pattern;

/** Small input-validation helpers shared by the forms. */
public final class Validate {

    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_.]{3,30}");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9 \\-]{7,16}$");

    private Validate() { }

    public static boolean blank(String s) { return s == null || s.trim().isEmpty(); }
    public static boolean username(String s) { return s != null && USERNAME.matcher(s).matches(); }
    public static boolean email(String s) { return s != null && EMAIL.matcher(s.trim()).matches(); }
    public static boolean phone(String s) { return s != null && PHONE.matcher(s.trim()).matches(); }
    public static boolean password(String s) { return s != null && s.length() >= 6; }
}
