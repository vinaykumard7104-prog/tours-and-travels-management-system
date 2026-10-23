package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.BookingDao;
import tours.and.travels.management.systems.dao.CatalogDao;
import tours.and.travels.management.systems.model.Models.TravelPackage;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.StatCard;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Money;

/** Overview: hero, key numbers, featured packages and destinations. */
public class HomePage extends Page {

    private final VerticalPanel content = body();
    private final JPanel banner = new JPanel(new BorderLayout());
    private final StatCard trips = new StatCard(Ico.SUITCASE, "Package trips", false, new Color(0x3B82F6), new Color(0x06B6D4));
    private final StatCard stays = new StatCard(Ico.HOTEL, "Hotel stays", false, new Color(0x8B5CF6), new Color(0x6366F1));
    private final StatCard booked = new StatCard(Ico.TICKET, "Total booked", true, new Color(0xF59E0B), new Color(0xF97316));
    private final StatCard due = new StatCard(Ico.WALLET, "Amount due", true, new Color(0x10B981), new Color(0x14B8A6));
    private final JPanel featured = grid(3, 2);
    private boolean featuredLoaded;

    public HomePage(MainFrame app) {
        super(app);
        banner.setOpaque(false);
        content.add(left(banner));
        content.add(left(hero()));
        content.add(gap(18));
        JPanel stats = grid(4, 2);
        stats.add(trips);
        stats.add(stays);
        stats.add(booked);
        stats.add(due);
        content.add(left(stats));
        content.add(gap(26));

        PillButton all = new PillButton("View all", PillButton.Style.GHOST).compact().trailing(Ico.ARROW_RIGHT);
        all.addActionListener(e -> app.navigate("packages"));
        content.add(left(section("Featured packages", "Hand-picked trips for the season", all)));
        content.add(gap(8));
        content.add(left(featured));
        content.add(gap(26));

        PillButton more = new PillButton("Explore all", PillButton.Style.GHOST).compact().trailing(Ico.ARROW_RIGHT);
        more.addActionListener(e -> app.navigate("destinations"));
        content.add(left(section("Popular destinations", "Where travellers are heading", more)));
        content.add(gap(8));
        JPanel dg = grid(4, 2);
        for (int i : new int[]{4, 7, 2, 0}) {
            DestinationsPage.Dest d = DestinationsPage.ALL.get(i);
            dg.add(DestinationsPage.buildCard(d, 210, () -> app.navigate("destinations")));
        }
        content.add(left(dg));
        add(Scroll.page(content), BorderLayout.CENTER);
    }

    @Override public String title() {
        int h = LocalTime.now().getHour();
        String g = h < 12 ? "Good morning" : h < 17 ? "Good afternoon" : "Good evening";
        return g + ", " + app.firstName();
    }

    @Override public String subtitle() { return "Here\u2019s what\u2019s happening with your trips."; }

    private JPanel hero() {
        ImagePanel hero = new ImagePanel(28).sideShade(0.82f).tint(new Color(10, 20, 60, 40));
        hero.setResource("images/hero/home.jpg");
        hero.setPreferredSize(new Dimension(900, 300));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(0, 48, 0, 40));
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(Box_glue());
        t.add(left(Ui.label("PLAN YOUR NEXT ESCAPE", 12, true, null).color(new Color(0x67E8F9))));
        t.add(Ui.vgap(10));
        t.add(left(Ui.label("Where to next?", 44, true, null).color(Color.WHITE)));
        t.add(Ui.vgap(8));
        t.add(left(Ui.label("Beaches, mountains and heritage cities \u2013 book it all in a few clicks.", 16, false, null)
                .color(new Color(255, 255, 255, 220))));
        t.add(Ui.vgap(24));
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        b.setOpaque(false);
        PillButton p1 = new PillButton("Explore packages", PillButton.Style.ACCENT).trailing(Ico.ARROW_RIGHT);
        PillButton p2 = new PillButton("Browse hotels", PillButton.Style.GLASS).icon(Ico.HOTEL);
        p1.addActionListener(e -> app.navigate("packages"));
        p2.addActionListener(e -> app.navigate("hotels"));
        b.add(p1);
        b.add(Ui.hgap(12));
        b.add(p2);
        t.add(left(b));
        t.add(Box_glue());
        hero.add(t, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        wrap.add(hero);
        return wrap;
    }

    private static java.awt.Component Box_glue() { return javax.swing.Box.createVerticalGlue(); }

    @Override public void onShow() {
        // profile reminder
        banner.removeAll();
        if (app.customer() == null) {
            Card c = new Card(new BorderLayout(16, 0), 16).fill(Theme.C.SURFACE);
            JPanel txt = new JPanel();
            txt.setOpaque(false);
            txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
            txt.add(left(Ui.label("Complete your traveller profile", 16, true, Theme.C.ACCENT)));
            txt.add(left(Ui.muted("Add your ID and phone number once \u2013 then booking takes two clicks.", 13.5f)));
            c.add(txt, BorderLayout.CENTER);
            PillButton go = new PillButton("Complete profile", PillButton.Style.ACCENT).compact();
            go.addActionListener(e -> app.navigate("profile"));
            c.add(go, BorderLayout.EAST);
            banner.add(c);
            banner.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        }
        banner.revalidate();

        Async.run(() -> BookingDao.stats(app.account().username()), s -> {
            trips.setValue(s.packageTrips());
            stays.setValue(s.hotelStays());
            booked.setValue(s.totalBooked());
            due.setValue(s.outstanding());
        }, this::failed);

        if (!featuredLoaded) {
            Async.run(CatalogDao::packages, list -> {
                featuredLoaded = true;
                featured.removeAll();
                for (TravelPackage p : list) featured.add(packageCard(p, list));
                featured.revalidate();
                featured.repaint();
            }, this::failed);
        }
    }

    private Card packageCard(TravelPackage p, List<TravelPackage> all) {
        Card c = new Card(new BorderLayout(0, 12), 10);
        ImagePanel img = new ImagePanel(16).bottomShade(0.2f);
        img.setResource("images/packages/" + p.image());
        img.setPreferredSize(new Dimension(200, 170));
        c.add(img, BorderLayout.NORTH);
        JPanel d = new JPanel(new BorderLayout());
        d.setOpaque(false);
        d.setBorder(BorderFactory.createEmptyBorder(0, 6, 4, 6));
        JPanel l = new JPanel();
        l.setOpaque(false);
        l.setLayout(new BoxLayout(l, BoxLayout.Y_AXIS));
        l.add(left(Ui.title(p.name(), 17)));
        l.add(left(Ui.muted(p.tagline(), 13)));
        d.add(l, BorderLayout.CENTER);
        JPanel r = new JPanel(new BorderLayout());
        r.setOpaque(false);
        r.add(Ui.label(Money.format(p.price()), 18, true, Theme.C.PRIMARY), BorderLayout.NORTH);
        d.add(r, BorderLayout.EAST);
        c.add(d, BorderLayout.CENTER);
        c.onClick(() -> BookPackageDialog.show(app, all, p));
        return c;
    }
}
