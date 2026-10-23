package tours.and.travels.management.systems.ui.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.Logo;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.WrapText;
import tours.and.travels.management.systems.util.Async;

/** Start-up screen: connects to MySQL, creates the schema if needed, then opens the sign-in window. */
public class SplashFrame extends JFrame {

    private final ThemedLabel status = Ui.label("Starting\u2026", 14, false, null).color(new Color(255, 255, 255, 190));
    private final Bar bar = new Bar();
    private final JPanel bottom = new JPanel(new BorderLayout());
    private Timer creep;
    private long started;

    private static final class Bar extends JComponent {
        double value;
        @Override public Dimension getPreferredSize() { return new Dimension(100, 6); }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            g.setColor(new Color(255, 255, 255, 55));
            g.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
            g.setPaint(new GradientPaint(0, 0, new Color(0x3B82F6), getWidth(), 0, new Color(0x22D3EE)));
            g.fillRoundRect(0, 0, (int) (getWidth() * value), 6, 6, 6);
            g.dispose();
        }
    }

    public SplashFrame() {
        super("Tours & Travels");
        boolean round = Ui.translucencySupported();
        setUndecorated(true);
        if (round) setBackground(new Color(0, 0, 0, 0));
        setSize(880, 500);
        setLocationRelativeTo(null);

        ImagePanel hero = new ImagePanel(round ? 28 : 0).bottomShade(0.55f).tint(new Color(8, 18, 48, 120));
        hero.setResource("images/hero/splash.jpg");
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(48, 56, 40, 56));
        setContentPane(hero);

        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        Logo logo = new Logo(74, true, Color.WHITE, "Tours & Travels", "Management System");
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(Box_glue());
        centre.add(logo);
        centre.add(Ui.vgap(18));
        ThemedLabel tag = Ui.label("Plan. Book. Explore.", 20, false, null).color(new Color(255, 255, 255, 220));
        tag.setAlignmentX(Component.LEFT_ALIGNMENT);
        centre.add(tag);
        hero.add(centre, BorderLayout.CENTER);

        bottom.setOpaque(false);
        JPanel stat = new JPanel();
        stat.setOpaque(false);
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        stat.add(status);
        stat.add(Ui.vgap(12));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        stat.add(bar);
        bottom.add(stat, BorderLayout.CENTER);
        hero.add(bottom, BorderLayout.SOUTH);
    }

    private static Component Box_glue() { return javax.swing.Box.createVerticalGlue(); }

    @Override public void setVisible(boolean b) {
        super.setVisible(b);
        if (b && started == 0) {
            started = System.currentTimeMillis();
            connect();
        }
    }

    private void connect() {
        status.setText("Connecting to MySQL\u2026");
        if (bottom.getComponentCount() == 0) return;
        bar.value = 0.05;
        if (creep != null) creep.stop();
        creep = new Timer(40, e -> { bar.value += (0.85 - bar.value) * 0.03; bar.repaint(); });
        creep.start();
        Async.run(() -> { Database.initialize(); return Boolean.TRUE; }, ok -> {
            status.setText("Preparing your travel desk\u2026");
            long wait = Math.max(300, 1800 - (System.currentTimeMillis() - started));
            Timer t = new Timer((int) wait, e -> openApp());
            t.setRepeats(false);
            t.start();
        }, this::showError);
    }

    private void openApp() {
        if (creep != null) creep.stop();
        Anim.run(300, p -> { bar.value = bar.value + (1 - bar.value) * p; bar.repaint(); }, () -> {
            new AuthFrame().setVisible(true);
            dispose();
        });
    }

    private void showError(Throwable t) {
        if (creep != null) creep.stop();
        t.printStackTrace();
        status.setText("Could not start");
        bottom.removeAll();
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(26, 0, 0, 0));
        ThemedLabel head = Ui.label("Can\u2019t reach the database", 17, true, null).color(Color.WHITE);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        WrapText msg = new WrapText(Database.explain(t), Theme.regular(13.5f), null) {
            @Override public Color getForeground() { return new Color(255, 255, 255, 215); }
        };
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        PillButton retry = new PillButton("Retry", PillButton.Style.PRIMARY).compact();
        PillButton settings = new PillButton("Database settings", PillButton.Style.GLASS).compact();
        PillButton quit = new PillButton("Exit", PillButton.Style.GLASS).compact();
        retry.addActionListener(e -> { restoreProgress(); connect(); });
        settings.addActionListener(e -> DbSettingsDialog.show(this, () -> { restoreProgress(); connect(); }));
        quit.addActionListener(e -> System.exit(0));
        buttons.add(retry);
        buttons.add(Ui.hgap(10));
        buttons.add(settings);
        buttons.add(Ui.hgap(10));
        buttons.add(quit);
        p.add(head);
        p.add(Ui.vgap(6));
        p.add(msg);
        p.add(Ui.vgap(14));
        p.add(buttons);
        bottom.add(p, BorderLayout.CENTER);
        bottom.revalidate();
        bottom.repaint();
    }

    private void restoreProgress() {
        bottom.removeAll();
        JPanel stat = new JPanel();
        stat.setOpaque(false);
        stat.setLayout(new BoxLayout(stat, BoxLayout.Y_AXIS));
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        stat.add(status);
        stat.add(Ui.vgap(12));
        stat.add(bar);
        bottom.add(stat, BorderLayout.CENTER);
        bottom.revalidate();
    }
}
