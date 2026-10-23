package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.CatalogDao;
import tours.and.travels.management.systems.model.Models.Hotel;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Chip;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Money;

/** Searchable, sortable grid of hotels. */
public class HotelsPage extends Page {

    private final VerticalPanel content = body();
    private final JPanel gridHost = new JPanel(new BorderLayout());
    private final Field search = new Field("Search hotels\u2026", Ico.SEARCH, false);
    private final Combo sort = new Combo("Recommended", "Price: low to high", "Price: high to low", "Name A\u2013Z");
    private List<Hotel> hotels = List.of();

    public HotelsPage(MainFrame app) {
        super(app);
        JPanel bar = new JPanel(new BorderLayout(14, 0));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        bar.add(search, BorderLayout.CENTER);
        sort.setPreferredSize(new Dimension(230, 48));
        bar.add(sort, BorderLayout.EAST);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        content.add(left(bar));
        content.add(gap(8));
        gridHost.setOpaque(false);
        content.add(left(gridHost));
        add(Scroll.page(content), BorderLayout.CENTER);
        search.getInput().getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { rebuild(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { rebuild(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });
        sort.addActionListener(e -> rebuild());
    }

    @Override public String title() { return "Hotels"; }

    @Override public String subtitle() { return "Comfortable stays for every budget."; }

    @Override public void onShow() {
        Async.run(CatalogDao::hotels, list -> { hotels = list; rebuild(); }, this::failed);
    }

    private void rebuild() {
        String q = search.getText().trim().toLowerCase(Locale.ROOT);
        List<Hotel> shown = new ArrayList<>();
        for (Hotel h : hotels) if (q.isEmpty() || h.name().toLowerCase(Locale.ROOT).contains(q)) shown.add(h);
        switch (sort.getSelectedIndex()) {
            case 1 -> shown.sort(Comparator.comparingInt(Hotel::costPerPerson));
            case 2 -> shown.sort(Comparator.comparingInt(Hotel::costPerPerson).reversed());
            case 3 -> shown.sort(Comparator.comparing(Hotel::name));
            default -> { }
        }
        gridHost.removeAll();
        if (shown.isEmpty()) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
            empty.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));
            empty.add(left(Ui.title("No hotels match \u201C" + search.getText().trim() + "\u201D", 18)));
            empty.add(left(Ui.muted("Try a different name.", 14)));
            gridHost.add(empty, BorderLayout.CENTER);
        } else {
            JPanel grid = grid(3, 2);
            for (Hotel h : shown) grid.add(card(h));
            gridHost.add(grid, BorderLayout.CENTER);
        }
        gridHost.revalidate();
        gridHost.repaint();
    }

    private Card card(Hotel h) {
        Card c = new Card(new BorderLayout(0, 14), 10);
        ImagePanel img = new ImagePanel(16).bottomShade(0.55f);
        img.setResource("images/hotels/" + h.image());
        img.setPreferredSize(new Dimension(300, 190));
        img.setLayout(new BorderLayout());
        img.add(new PriceTag(h.costPerPerson()), BorderLayout.SOUTH);
        c.add(img, BorderLayout.NORTH);

        JPanel d = new JPanel();
        d.setOpaque(false);
        d.setBorder(BorderFactory.createEmptyBorder(0, 6, 4, 6));
        d.setLayout(new BoxLayout(d, BoxLayout.Y_AXIS));
        d.add(left(Ui.title(h.name(), 17)));
        d.add(Ui.vgap(10));
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chips.setOpaque(false);
        chips.add(new Chip("AC +" + Money.format(h.ac()), Theme.C.PRIMARY));
        chips.add(Ui.hgap(8));
        chips.add(new Chip("Meals +" + Money.format(h.food()), Theme.C.ACCENT));
        d.add(left(chips));
        d.add(Ui.vgap(14));
        PillButton book = new PillButton("Book now", PillButton.Style.PRIMARY).stretch().compact();
        book.addActionListener(e -> BookHotelDialog.show(app, hotels, h));
        d.add(left(book));
        c.add(d, BorderLayout.CENTER);
        return c.arm();
    }

    /** "from ₹x / night" badge over the photo. */
    private static final class PriceTag extends JComponent {
        private final int price;

        PriceTag(int price) { this.price = price; setOpaque(false); }

        @Override public Dimension getPreferredSize() { return new Dimension(100, 52); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            g.setFont(Theme.regular(12));
            g.setColor(new java.awt.Color(255, 255, 255, 200));
            g.drawString("from", 16, 22);
            g.setFont(Theme.bold(20));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(java.awt.Color.WHITE);
            String p = Money.format(price);
            g.drawString(p, 16, 44);
            g.setFont(Theme.regular(12));
            g.setColor(new java.awt.Color(255, 255, 255, 200));
            g.drawString("/ person / night", 16 + fm.stringWidth(p) + 6, 44);
            g.dispose();
        }
    }
}
