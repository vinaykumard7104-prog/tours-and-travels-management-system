package tours.and.travels.management.systems.util;

import java.text.NumberFormat;
import java.util.Locale;

/** Indian-rupee formatting / parsing helpers. */
public final class Money {

    private static final NumberFormat FMT = NumberFormat.getIntegerInstance(new Locale.Builder().setLanguage("en").setRegion("IN").build());

    private Money() { }

    /** 12000 -> "₹12,000" */
    public static String format(long amount) {
        synchronized (FMT) {
            return "\u20B9" + FMT.format(amount);
        }
    }

    /** Accepts legacy values such as "Rs 12000", "Rs12000/-" or "12000". */
    public static int parse(String text) {
        if (text == null) return 0;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c == '.') break;
            if (Character.isDigit(c)) sb.append(c);
        }
        if (sb.length() == 0) return 0;
        try {
            return Integer.parseInt(sb.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
