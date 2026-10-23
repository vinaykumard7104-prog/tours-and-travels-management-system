package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.CatalogDao;
import tours.and.travels.management.systems.model.Models.TravelPackage;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Chip;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Money;

/** Gold / Silver / Bronze tour packages. */
public class PackagesPage extends Page {

    private final VerticalPanel content = body();
    private List<TravelPackage> packages = List.of();

    public PackagesPage(MainFrame app) {
        super(app);
        add(Scroll.page(content), BorderLayout.CENTER);
    }

    @Override public String title() { return "Tour packages"; }

    @Override public String subtitle() { return "All-inclusive trips, priced per person."; }

    @Override public void onShow() {
        Async.run(CatalogDao::packages, list -> { packages = list; rebuild(); }, this::failed);
    }

    private void rebuild() {
        content.removeAll();
        for (TravelPackage p : packages) {
            content.add(left(card(p)));
            content.add(gap(6));
        }
        content.revalidate();
        content.repaint();
    }

    private Card card(TravelPackage p) {
        Card c = new Card(new BorderLayout(26, 0), 14);
        ImagePanel img = new ImagePanel(16).bottomShade(0.25f);
        img.setResource("images/packages/" + p.image());
        img.setPreferredSize(new Dimension(360, 270));
        c.add(img, BorderLayout.WEST);

        JPanel d = new JPanel();
        d.setOpaque(false);
        d.setLayout(new BoxLayout(d, BoxLayout.Y_AXIS));
        d.add(left(new Chip(p.season(), p.season().startsWith("SUMMER") ? Theme.C.ACCENT : Theme.C.PRIMARY)));
        d.add(Ui.vgap(10));
        d.add(left(Ui.title(p.name(), 26)));
        d.add(Ui.vgap(2));
        d.add(left(Ui.muted(p.tagline() + "  \u00B7  " + p.duration(), 14.5f)));
        d.add(Ui.vgap(16));
        JPanel feats = new JPanel(new GridLayout(0, 2, 14, 9));
        feats.setOpaque(false);
        for (String f : p.features()) feats.add(new CheckRow(f));
        d.add(left(feats));
        d.add(javax.swing.Box.createVerticalGlue());
        d.add(Ui.vgap(14));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JPanel price = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        price.setOpaque(false);
        price.add(Ui.label(Money.format(p.price()), 28, true, Theme.C.PRIMARY));
        price.add(Ui.hgap(8));
        price.add(Ui.muted("per person", 13.5f));
        bottom.add(price, BorderLayout.WEST);
        PillButton book = new PillButton("Book now", PillButton.Style.PRIMARY).trailing(Ico.ARROW_RIGHT);
        book.addActionListener(e -> BookPackageDialog.show(app, packages, p));
        bottom.add(book, BorderLayout.EAST);
        d.add(left(bottom));
        c.add(d, BorderLayout.CENTER);
        return c.arm();
    }

    /** Tick + text. */
    private static final class CheckRow extends JComponent {
        private final String text;

        CheckRow(String text) { this.text = text; }

        @Override public Dimension getPreferredSize() { return new Dimension(200, 26); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            g.setColor(Theme.C.SUCCESS.alpha(36));
            g.fillOval(0, 3, 20, 20);
            Ico.CHECK.paint(g, 4, 7, 12, Theme.C.SUCCESS.get(), 2.8);
            g.setFont(Theme.regular(14));
            java.awt.FontMetrics fm = g.getFontMetrics();
            g.setColor(Theme.C.TEXT.get());
            String s = text;
            while (fm.stringWidth(s) > getWidth() - 30 && s.length() > 4) s = s.substring(0, s.length() - 2);
            g.drawString(s.equals(text) ? s : s + "\u2026", 30, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }
}
