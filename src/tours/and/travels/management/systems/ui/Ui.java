package tours.and.travels.management.systems.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;

/** Assorted painting / layout helpers. */
public final class Ui {

    private Ui() { }

    /** A graphics copy with quality rendering hints enabled. */
    public static Graphics2D g2(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        return g2;
    }

    /** Soft layered drop-shadow under a rounded rectangle. */
    public static void shadow(Graphics2D g, double x, double y, double w, double h, double r, int size, float strength) {
        boolean dark = Theme.isDark();
        float base = (dark ? 0.55f : 0.30f) * strength / size;
        for (int i = size; i >= 1; i--) {
            float a = base * (1f - (i - 1f) / size);
            g.setColor(new Color(dark ? 0 : 15, dark ? 0 : 25, dark ? 0 : 60, Math.min(255, Math.round(a * 255))));
            g.fill(new RoundRectangle2D.Double(x - i, y - i + size * 0.35, w + 2 * i, h + 2 * i, r * 2 + i, r * 2 + i));
        }
    }

    public static ThemedLabel label(String text, float size, boolean bold, Theme.C role) {
        return new ThemedLabel(text, bold ? Theme.bold(size) : Theme.regular(size), role);
    }

    public static ThemedLabel title(String text, float size) { return label(text, size, true, Theme.C.TEXT); }

    public static ThemedLabel muted(String text, float size) { return label(text, size, false, Theme.C.MUTED); }

    public static Component vgap(int h) { return Box.createRigidArea(new Dimension(0, h)); }

    public static Component hgap(int w) { return Box.createRigidArea(new Dimension(w, 0)); }

    public static Window windowOf(Component c) { return c == null ? null : SwingUtilities.getWindowAncestor(c); }

    /** True when undecorated windows can have transparent corners on this system. */
    public static boolean translucencySupported() {
        try {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .isWindowTranslucencySupported(java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
        } catch (Throwable t) {
            return false;
        }
    }

    public static String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        String s = parts.length == 1 ? parts[0].substring(0, 1) : parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1);
        return s.toUpperCase();
    }

    public static void alignLeft(JComponent c) { c.setAlignmentX(Component.LEFT_ALIGNMENT); }
}
