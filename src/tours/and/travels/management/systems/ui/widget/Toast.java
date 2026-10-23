package tours.and.travels.management.systems.ui.widget;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Small slide-in notification shown inside the window. */
public final class Toast extends JComponent {

    public enum Kind { SUCCESS, ERROR, INFO }

    private static final String KEY = "tt.toast";

    private final String message;
    private final Kind kind;
    private float alpha = 0f;

    private Toast(String message, Kind kind) {
        this.message = message;
        this.kind = kind;
        setOpaque(false);
        FontMetrics fm = getFontMetrics(Theme.bold(14));
        setSize(Math.min(520, fm.stringWidth(message) + 98), 52);
    }

    public static void show(Component anchor, String message, Kind kind) {
        JRootPane root = anchor == null ? null : SwingUtilities.getRootPane(anchor);
        if (root == null) return;
        JLayeredPane lp = root.getLayeredPane();
        Object old = lp.getClientProperty(KEY);
        if (old instanceof Component c) lp.remove(c);
        Toast t = new Toast(message, kind);
        lp.putClientProperty(KEY, t);
        lp.add(t, JLayeredPane.POPUP_LAYER);
        int x = (lp.getWidth() - t.getWidth()) / 2;
        Anim.run(320, p -> {
            t.alpha = (float) p;
            t.setLocation(x, (int) (-t.getHeight() + (t.getHeight() + 24) * p));
            t.repaint();
        }, () -> {
            Timer hold = new Timer(2800, e -> Anim.run(300, p -> {
                t.alpha = (float) (1 - p);
                t.repaint();
            }, () -> {
                lp.remove(t);
                lp.repaint();
            }));
            hold.setRepeats(false);
            hold.start();
        });
    }

    public static void success(Component a, String m) { show(a, m, Kind.SUCCESS); }
    public static void error(Component a, String m) { show(a, m, Kind.ERROR); }
    public static void info(Component a, String m) { show(a, m, Kind.INFO); }

    @Override public Dimension getPreferredSize() { return getSize(); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alpha))));
        int w = getWidth(), h = getHeight();
        Ui.shadow(g, 6, 6, w - 12, h - 18, 16, 6, 1.4f);
        g.setColor(new Color(0x0F172A));
        g.fill(new RoundRectangle2D.Float(6, 6, w - 12, h - 18, 32, 32));
        Color c = switch (kind) {
            case SUCCESS -> Theme.C.SUCCESS.get();
            case ERROR -> Theme.C.DANGER.get();
            default -> Theme.C.PRIMARY2.get();
        };
        g.setColor(c);
        g.fill(new Ellipse2D.Float(16, (h - 12) / 2f - 12, 24, 24));
        (kind == Kind.SUCCESS ? Ico.CHECK : kind == Kind.ERROR ? Ico.CLOSE : Ico.PLANE)
                .paint(g, 20, (h - 12) / 2.0 - 8, 16, Color.WHITE, 2.6);
        g.setFont(Theme.bold(14));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        String m = message;
        while (fm.stringWidth(m) > w - 90 && m.length() > 4) m = m.substring(0, m.length() - 2);
        if (!m.equals(message)) m += "\u2026";
        g.drawString(m, 52, (h - 12 - fm.getHeight()) / 2 + fm.getAscent() + 6);
        g.dispose();
    }
}
