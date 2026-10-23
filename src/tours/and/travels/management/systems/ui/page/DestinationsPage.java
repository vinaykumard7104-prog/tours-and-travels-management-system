package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.ModernDialog;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.ui.widget.WrapText;

/** Photo gallery of destinations with an auto-playing hero. */
public class DestinationsPage extends Page {

    /** A place worth visiting. */
    public record Dest(String name, String place, String blurb, String best, String image) { }

    public static final List<Dest> ALL = List.of(
            new Dest("Kalka\u2013Shimla Railway", "Himachal Pradesh", "A UNESCO-listed toy train that winds through pine forests and more than a hundred tunnels on its way up to the hills.", "Mar\u2013Jun, Dec\u2013Jan", "d1.jpg"),
            new Dest("Himalayan Hills", "The Himalayas", "Snow peaks, crisp air and quiet hill-top stays \u2013 the classic mountain escape.", "Mar\u2013Jun, Sep\u2013Nov", "d2.jpg"),
            new Dest("Varanasi", "Uttar Pradesh", "Sunrise boat rides along the ghats of the Ganges and the glow of the evening aarti.", "Oct\u2013Mar", "d3.jpg"),
            new Dest("Nilgiri Hills", "Tamil Nadu", "Tea-covered slopes and a little blue mountain train rolling through them.", "Oct\u2013Jun", "d4.jpg"),
            new Dest("Mysore Palace", "Karnataka", "Indo-Saracenic halls with carved pillars, stained glass \u2013 and thousands of lights on Sundays and festivals.", "Oct\u2013Mar", "d5.jpg"),
            new Dest("Humayun\u2019s Tomb", "Delhi", "A Mughal garden-tomb of red sandstone and marble that foreshadowed the Taj Mahal.", "Oct\u2013Mar", "d6.jpg"),
            new Dest("India Gate", "New Delhi", "The 42 m war memorial that glows golden at dusk, with lawns made for evening walks.", "Oct\u2013Mar", "d7.jpg"),
            new Dest("Dal Lake", "Srinagar, Kashmir", "Shikara rides, floating gardens and houseboats beneath snow-capped mountains.", "Apr\u2013Oct", "d8.jpg"));

    private final VerticalPanel content = body();
    private final ImagePanel hero = new ImagePanel(28).bottomShade(0.75f);
    private final ThemedLabel heroTitle = Ui.label("", 34, true, null).color(Color.WHITE);
    private final ThemedLabel heroSub = Ui.label("", 15, false, null).color(new Color(255, 255, 255, 210));
    private final Dots dots = new Dots(ALL.size());
    private Timer timer;
    private int index;

    public DestinationsPage(MainFrame app) {
        super(app);
        hero.setPreferredSize(new Dimension(800, 360));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(0, 40, 34, 40));
        JPanel cap = new JPanel(new BorderLayout());
        cap.setOpaque(false);
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(left(heroTitle));
        t.add(Ui.vgap(4));
        t.add(left(heroSub));
        cap.add(t, BorderLayout.CENTER);
        cap.add(dots, BorderLayout.EAST);
        hero.add(cap, BorderLayout.SOUTH);
        JPanel heroWrap = new JPanel(new BorderLayout());
        heroWrap.setOpaque(false);
        heroWrap.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        heroWrap.add(hero);
        content.add(left(heroWrap));
        content.add(gap(24));
        content.add(left(section("Explore India", "Tap any card to learn more", null)));
        content.add(gap(10));
        JPanel grid = grid(4, 2);
        for (Dest d : ALL) grid.add(buildCard(d, 240, () -> open(d)));
        content.add(left(grid));
        add(Scroll.page(content), BorderLayout.CENTER);
        show(0, false);
    }

    @Override public String title() { return "Destinations"; }

    @Override public String subtitle() { return "Places that deserve a spot on your list."; }

    @Override public void onShow() {
        if (timer == null) {
            timer = new Timer(4500, e -> show((index + 1) % ALL.size(), true));
            timer.start();
        }
    }

    @Override public void onHide() {
        if (timer != null) { timer.stop(); timer = null; }
    }

    private void show(int i, boolean animate) {
        index = i;
        Dest d = ALL.get(i);
        hero.setResource("images/destinations/" + d.image());
        heroTitle.setText(d.name());
        heroSub.setText(d.place() + "  \u00B7  Best time: " + d.best());
        dots.set(i);
    }

    /** Reusable photo card (also used on the overview page). */
    static Card buildCard(Dest d, int height, Runnable onClick) {
        Card c = new Card(new BorderLayout(), 8);
        ImagePanel img = new ImagePanel(16).bottomShade(0.8f);
        img.setResource("images/destinations/" + d.image());
        img.setPreferredSize(new Dimension(200, height));
        img.setLayout(new BorderLayout());
        JPanel cap = new JPanel();
        cap.setOpaque(false);
        cap.setLayout(new BoxLayout(cap, BoxLayout.Y_AXIS));
        cap.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        cap.add(left(Ui.label(d.name(), 16, true, null).color(Color.WHITE)));
        cap.add(left(Ui.label(d.place(), 12.5f, false, null).color(new Color(255, 255, 255, 200))));
        img.add(cap, BorderLayout.SOUTH);
        c.add(img, BorderLayout.CENTER);
        new Hover(c).onTick(v -> img.setZoom(1 + 0.07 * v)).attachTree(c);
        c.onClick(onClick);
        return c;
    }


    private void open(Dest d) {
        ModernDialog dlg = new ModernDialog(app, d.name(), d.place(), 640);
        ImagePanel pic = new ImagePanel(18).bottomShade(0.1f);
        pic.setResource("images/destinations/" + d.image());
        pic.setPreferredSize(new Dimension(584, 280));
        dlg.body().add(left(pic));
        dlg.body().add(Ui.vgap(16));
        dlg.body().add(left(new WrapText(d.blurb(), Theme.regular(15), Theme.C.TEXT)));
        dlg.body().add(Ui.vgap(12));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.add(Ui.label("Best time to visit:  ", 14, false, Theme.C.MUTED));
        row.add(Ui.label(d.best(), 14, true, Theme.C.PRIMARY));
        dlg.body().add(left(row));
        PillButton close = new PillButton("Close", PillButton.Style.SECONDARY);
        PillButton plan = new PillButton("Plan this trip", PillButton.Style.PRIMARY).trailing(Ico.ARROW_RIGHT);
        close.addActionListener(e -> dlg.dispose());
        plan.addActionListener(e -> { dlg.dispose(); app.navigate("packages"); });
        dlg.footer().add(close);
        dlg.footer().add(plan);
        dlg.open();
    }

    /** Page indicator dots. */
    private static final class Dots extends JComponent {
        private final int n;
        private int active;

        Dots(int n) { this.n = n; setOpaque(false); }

        @Override public Dimension getMaximumSize() { return getPreferredSize(); }

        void set(int i) { active = i; repaint(); }

        @Override public Dimension getPreferredSize() { return new Dimension(22 + (n - 1) * 14 + 6, 24); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            int x = 0;
            for (int i = 0; i < n; i++) {
                boolean a = i == active;
                g.setColor(new Color(255, 255, 255, a ? 255 : 120));
                int w = a ? 22 : 8;
                g.fillRoundRect(x, getHeight() - 14, w, 8, 8, 8);
                x += w + 6;
            }
            g.dispose();
        }
    }
}
