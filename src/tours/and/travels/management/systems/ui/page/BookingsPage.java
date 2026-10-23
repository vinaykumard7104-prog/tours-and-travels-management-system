package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.BookingDao;
import tours.and.travels.management.systems.model.Models.HotelBooking;
import tours.and.travels.management.systems.model.Models.PackageBooking;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Dialogs;
import tours.and.travels.management.systems.ui.widget.ModernTable;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Segmented;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Fmt;
import tours.and.travels.management.systems.util.Money;

/** Package and hotel bookings with cancel. */
public class BookingsPage extends Page {

    private final ModernTable pkgTable = new ModernTable(
            new String[]{"Package", "Travellers", "Booked on", "Total"},
            new int[]{ModernTable.LEFT, ModernTable.CENTER, ModernTable.LEFT, ModernTable.RIGHT});
    private final ModernTable hotelTable = new ModernTable(
            new String[]{"Hotel", "Guests", "Nights", "Room", "Meals", "Booked on", "Total"},
            new int[]{ModernTable.LEFT, ModernTable.CENTER, ModernTable.CENTER, ModernTable.CENTER, ModernTable.CENTER, ModernTable.LEFT, ModernTable.RIGHT});
    private final CardLayout cards = new CardLayout();
    private final JPanel tables = new JPanel(cards);
    private final Segmented tabs = new Segmented("Packages", "Hotels").compact();
    private final ThemedLabel summary = Ui.label("", 14, true, Theme.C.MUTED);
    private final PillButton cancel = new PillButton("Cancel booking", PillButton.Style.DANGER_SOFT).icon(Ico.TRASH).compact();
    private java.util.List<PackageBooking> pkgs = java.util.List.of();
    private java.util.List<HotelBooking> hotels = java.util.List.of();
    private final JPanel emptyPkg = empty("No package bookings yet", "Pick a trip and it will show up here.", "packages", "Browse packages");
    private final JPanel emptyHotel = empty("No hotel bookings yet", "Book a stay and it will show up here.", "hotels", "Browse hotels");
    private final CardLayout pkgCards = new CardLayout(), hotelCards = new CardLayout();
    private final JPanel pkgHost = new JPanel(pkgCards), hotelHost = new JPanel(hotelCards);

    public BookingsPage(MainFrame app) {
        super(app);
        setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 38));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(0, 10, 12, 10));
        JPanel l = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        l.setOpaque(false);
        l.add(tabs);
        l.add(Ui.hgap(18));
        l.add(summary);
        top.add(l, BorderLayout.WEST);
        cancel.setEnabled(false);
        top.add(cancel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        pkgHost.setOpaque(false);
        pkgHost.add(pkgTable.scroll, "t");
        pkgHost.add(emptyPkg, "e");
        hotelHost.setOpaque(false);
        hotelHost.add(hotelTable.scroll, "t");
        hotelHost.add(emptyHotel, "e");
        tables.setOpaque(false);
        tables.add(pkgHost, "p");
        tables.add(hotelHost, "h");
        Card c = new Card(new BorderLayout(), 0).noLift().clipContent();
        c.add(tables, BorderLayout.CENTER);
        add(c, BorderLayout.CENTER);

        tabs.onChange(() -> { cards.show(tables, tabs.getSelectedIndex() == 0 ? "p" : "h"); updateSummary(); });
        javax.swing.event.ListSelectionListener sel = e -> cancel.setEnabled(current().selectedRow() >= 0);
        pkgTable.table.getSelectionModel().addListSelectionListener(sel);
        hotelTable.table.getSelectionModel().addListSelectionListener(sel);
        cancel.addActionListener(e -> doCancel());
    }

    private ModernTable current() { return tabs.getSelectedIndex() == 0 ? pkgTable : hotelTable; }

    private JPanel empty(String head, String sub, String target, String action) {
        JPanel p = new JPanel(new java.awt.GridBagLayout());
        p.setOpaque(false);
        JPanel in = new JPanel();
        in.setOpaque(false);
        in.setLayout(new BoxLayout(in, BoxLayout.Y_AXIS));
        ThemedLabel h = Ui.title(head, 20), s = Ui.muted(sub, 14);
        h.setAlignmentX(0.5f);
        s.setAlignmentX(0.5f);
        PillButton b = new PillButton(action, PillButton.Style.PRIMARY).compact();
        b.setAlignmentX(0.5f);
        b.addActionListener(e -> app.navigate(target));
        in.add(h);
        in.add(Ui.vgap(4));
        in.add(s);
        in.add(Ui.vgap(16));
        in.add(b);
        p.add(in);
        return p;
    }

    @Override public String title() { return "My bookings"; }

    @Override public String subtitle() { return "Everything you\u2019ve booked, in one place."; }

    @Override public void onShow() {
        String u = app.account().username();
        Async.run(() -> new Object[]{BookingDao.packageBookings(u), BookingDao.hotelBookings(u)}, r -> {
            @SuppressWarnings("unchecked") java.util.List<PackageBooking> p = (java.util.List<PackageBooking>) r[0];
            @SuppressWarnings("unchecked") java.util.List<HotelBooking> h = (java.util.List<HotelBooking>) r[1];
            pkgs = p;
            hotels = h;
            pkgTable.clear();
            for (PackageBooking b : p) pkgTable.model.addRow(new Object[]{b.packageName(), b.persons(), Fmt.date(b.bookedOn()), Money.format(b.total())});
            hotelTable.clear();
            for (HotelBooking b : h) hotelTable.model.addRow(new Object[]{b.hotel(), b.persons(), b.days(), b.ac(), b.food(), Fmt.date(b.bookedOn()), Money.format(b.total())});
            pkgCards.show(pkgHost, p.isEmpty() ? "e" : "t");
            hotelCards.show(hotelHost, h.isEmpty() ? "e" : "t");
            updateSummary();
        }, this::failed);
    }

    private void updateSummary() {
        long total;
        int n;
        if (tabs.getSelectedIndex() == 0) { n = pkgs.size(); total = pkgs.stream().mapToLong(PackageBooking::total).sum(); }
        else { n = hotels.size(); total = hotels.stream().mapToLong(HotelBooking::total).sum(); }
        summary.setText(n + (n == 1 ? " booking" : " bookings") + "  \u00B7  " + Money.format(total));
        cancel.setEnabled(current().selectedRow() >= 0);
    }

    private void doCancel() {
        int row = current().selectedRow();
        if (row < 0) return;
        boolean pk = tabs.getSelectedIndex() == 0;
        String what = pk ? pkgs.get(row).packageName() : hotels.get(row).hotel();
        if (!Dialogs.confirm(this, "Cancel this booking?", "Your booking for " + what + " will be removed. This can\u2019t be undone.", "Yes, cancel it", true)) return;
        int id = pk ? pkgs.get(row).bookingId() : hotels.get(row).bookingId();
        Async.run(() -> { if (pk) BookingDao.cancelPackage(app.account().username(), id); else BookingDao.cancelHotel(app.account().username(), id); return true; },
                ok -> { Toast.success(app, "Booking cancelled"); onShow(); }, this::failed);
    }
}
