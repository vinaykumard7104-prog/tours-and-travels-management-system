package tours.and.travels.management.systems.ui.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.widget.Avatar;
import tours.and.travels.management.systems.ui.widget.IconButton;
import tours.and.travels.management.systems.ui.widget.Logo;

/** Dark gradient navigation rail. */
final class Sidebar extends JPanel {

    static final int WIDTH = 268;

    private final Map<String, NavItem> items = new LinkedHashMap<>();

    Sidebar(MainFrame app) {
        setOpaque(false);
        setPreferredSize(new Dimension(WIDTH, 100));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(26, 18, 20, 18));

        Logo logo = new Logo(46, true, Color.WHITE, "Tours & Travels", "Management System");
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);
        add(Ui.vgap(34));

        add(caption("MENU"));
        add(Ui.vgap(8));
        add(item(app, "home", "Overview", Ico.HOME));
        add(item(app, "packages", "Tour packages", Ico.SUITCASE));
        add(item(app, "hotels", "Hotels", Ico.HOTEL));
        add(item(app, "destinations", "Destinations", Ico.GLOBE));
        add(item(app, "bookings", "My bookings", Ico.TICKET));
        add(item(app, "payments", "Payments", Ico.WALLET));
        add(Ui.vgap(22));
        add(caption("ACCOUNT"));
        add(Ui.vgap(8));
        add(item(app, "profile", "My profile", Ico.USER));
        add(item(app, "tools", "Tools", Ico.GRID));
        add(Box_glue());

        // user chip
        JPanel chip = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Ui.g2(g0);
                g.setColor(new Color(255, 255, 255, 20));
                g.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 36, 36));
                g.dispose();
            }
        };
        chip.setOpaque(false);
        chip.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 8));
        chip.add(new Avatar(app.account().name(), 40), BorderLayout.WEST);
        JPanel names = new JPanel();
        names.setOpaque(false);
        names.setLayout(new BoxLayout(names, BoxLayout.Y_AXIS));
        names.add(Ui.label(app.account().name(), 14, true, null).color(Color.WHITE));
        names.add(Ui.label("@" + app.account().username(), 12, false, null).color(new Color(255, 255, 255, 150)));
        chip.add(names, BorderLayout.CENTER);
        IconButton out = new IconButton(Ico.LOGOUT, 38).tint(Theme.C.MUTED);
        out.setToolTipText("Sign out");
        out.addActionListener(e -> app.logout());
        chip.add(out, BorderLayout.EAST);
        chip.setAlignmentX(Component.LEFT_ALIGNMENT);
        chip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        add(chip);
    }

    private static Component Box_glue() { return javax.swing.Box.createVerticalGlue(); }

    private static JComponent caption(String s) {
        JComponent l = Ui.label(s, 11, true, null).color(new Color(255, 255, 255, 110));
        l.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JComponent item(MainFrame app, String key, String label, Ico icon) {
        NavItem n = new NavItem(label, icon, () -> app.navigate(key));
        items.put(key, n);
        return n;
    }

    void select(String key) {
        items.forEach((k, v) -> v.setActive(k.equals(key)));
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        g.setPaint(new GradientPaint(0, 0, Theme.C.SIDE_TOP.get(), 0, getHeight(), Theme.C.SIDE_BOT.get()));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(6, 182, 212, 22));
        g.fillOval(-120, getHeight() - 220, 320, 320);
        g.setColor(new Color(59, 130, 246, 26));
        g.fillOval(getWidth() - 140, -90, 240, 240);
        g.dispose();
    }

    /** One navigation row. */
    private static final class NavItem extends JComponent {
        private final String label;
        private final Ico icon;
        private double active;
        private boolean isActive;
        private final Hover hover = new Hover(this);

        NavItem(String label, Ico icon, Runnable action) {
            this.label = label;
            this.icon = icon;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { action.run(); }
            });
        }

        void setActive(boolean a) {
            if (a == isActive) return;
            isActive = a;
            final double from = active, to = a ? 1 : 0;
            Anim.run(240, p -> { active = from + (to - from) * p; repaint(); }, null);
        }

        @Override public Dimension getPreferredSize() { return new Dimension(100, 48); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 48); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            int w = getWidth(), h = getHeight();
            double hv = hover.get();
            RoundRectangle2D pill = new RoundRectangle2D.Float(0, 2, w, h - 4, 28, 28);
            if (active > 0.01) {
                g.setPaint(new GradientPaint(0, 0, new Color(59, 130, 246, (int) (200 * active)),
                        w, 0, new Color(6, 182, 212, (int) (150 * active))));
                g.fill(pill);
            }
            g.setColor(new Color(255, 255, 255, (int) (22 * hv * (1 - active))));
            g.fill(pill);
            Color fg = Theme.mix(new Color(255, 255, 255, 165), Color.WHITE, Math.max(hv * 0.7, active));
            icon.paint(g, 16, (h - 22) / 2.0, 22, fg, 1.9);
            g.setFont(Theme.font(14.5f, active > 0.5 ? java.awt.Font.BOLD : java.awt.Font.PLAIN));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(fg);
            g.drawString(label, 52, (h - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }
}
