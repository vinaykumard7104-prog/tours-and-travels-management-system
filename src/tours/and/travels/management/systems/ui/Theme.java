package tours.and.travels.management.systems.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GradientPaint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Design tokens: colours (light + dark), typography and brand gradient. */
public final class Theme {

    private static boolean dark;
    private static final List<Runnable> LISTENERS = new ArrayList<>();

    /** Every colour has a light and a dark value; callers always go through {@link #get()} at paint time. */
    public enum C {
        BG(0xF1F4FA, 0x0A1020),
        SURFACE(0xFFFFFF, 0x131B31),
        SURFACE_ALT(0xF3F6FB, 0x1B2745),
        INPUT(0xF3F6FB, 0x0E1529),
        BORDER(0xE2E8F2, 0x27345C),
        TEXT(0x0F172A, 0xE9EEFA),
        MUTED(0x66758D, 0x93A2C0),
        PRIMARY(0x2563EB, 0x4F8DF7),
        PRIMARY2(0x06B6D4, 0x22D3EE),
        ACCENT(0xF59E0B, 0xFBBF24),
        SUCCESS(0x10B981, 0x34D399),
        DANGER(0xEF4444, 0xF87171),
        SIDE_TOP(0x0A1430, 0x070B1A),
        SIDE_BOT(0x173272, 0x0C1736);

        private final Color light, darkC;

        C(int l, int d) {
            light = new Color(l);
            darkC = new Color(d);
        }

        public Color get() { return dark ? darkC : light; }
        public Color alpha(int a) { return Theme.alpha(get(), a); }
    }

    private Theme() { }

    public static boolean isDark() { return dark; }

    public static void setDark(boolean d) {
        dark = d;
        for (Runnable r : new ArrayList<>(LISTENERS)) r.run();
    }

    public static void toggle() { setDark(!dark); }

    public static void onChange(Runnable r) { LISTENERS.add(r); }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
    }

    public static Color mix(Color a, Color b, double t) {
        t = Math.max(0, Math.min(1, t));
        return new Color(
                (int) Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t),
                (int) Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    /** The signature blue-to-cyan brand gradient. */
    public static GradientPaint brand(float x1, float y1, float x2, float y2) {
        return new GradientPaint(x1, y1, C.PRIMARY.get(), x2, y2, C.PRIMARY2.get());
    }

    // ---------------------------------------------------------------- typography
    private static final String FAMILY = pickFamily();
    private static final Map<String, Font> FONTS = new HashMap<>();

    private static String pickFamily() {
        Set<String> have = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[]{"Segoe UI", "Inter", "SF Pro Text", "Helvetica Neue", "Roboto", "Noto Sans",
                "Ubuntu", "DejaVu Sans", "Arial"}) {
            if (have.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    public static Font font(float size, int style) {
        String key = size + ":" + style;
        return FONTS.computeIfAbsent(key, k -> new Font(FAMILY, style, 12).deriveFont(style, size));
    }

    public static Font regular(float size) { return font(size, Font.PLAIN); }
    public static Font bold(float size) { return font(size, Font.BOLD); }
}
