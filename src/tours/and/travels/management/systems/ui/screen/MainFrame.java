package tours.and.travels.management.systems.ui.screen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import tours.and.travels.management.systems.dao.CustomerDao;
import tours.and.travels.management.systems.model.Models.Account;
import tours.and.travels.management.systems.model.Models.Customer;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.page.BookingsPage;
import tours.and.travels.management.systems.ui.page.DestinationsPage;
import tours.and.travels.management.systems.ui.page.HomePage;
import tours.and.travels.management.systems.ui.page.HotelsPage;
import tours.and.travels.management.systems.ui.page.Page;
import tours.and.travels.management.systems.ui.page.PackagesPage;
import tours.and.travels.management.systems.ui.page.PaymentsPage;
import tours.and.travels.management.systems.ui.page.ProfilePage;
import tours.and.travels.management.systems.ui.page.ToolsPage;
import tours.and.travels.management.systems.ui.widget.ThemedPanel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Session;

/** The signed-in application window: sidebar + top bar + switchable pages. */
public class MainFrame extends JFrame {

    private final Account account;
    private Customer customer;
    private final CardLayout cards = new CardLayout();
    private final Map<String, Page> pages = new LinkedHashMap<>();
    private final Sidebar sidebar;
    private final TopBar topBar;
    private final FadeHost host;
    private String current;

    /** Content area that fades each new page in from the background colour. */
    private static final class FadeHost extends JPanel {
        private float veil;

        FadeHost(CardLayout l) { super(l); setOpaque(false); }

        void fade() { Anim.run(260, p -> { veil = (float) (1 - p); repaint(); }, () -> veil = 0); }

        @Override public void paint(Graphics g) {
            super.paint(g);
            if (veil > 0.01f) {
                Graphics2D g2 = (Graphics2D) g.create();
                Color c = Theme.C.BG.get();
                g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (veil * 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        }
    }

    public MainFrame(Account account) {
        super("Tours & Travels");
        this.account = account;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 720));
        setSize(1420, 860);
        setLocationRelativeTo(null);

        ThemedPanel root = new ThemedPanel(new BorderLayout(), Theme.C.BG);
        root.setOpaque(true);
        setContentPane(root);

        sidebar = new Sidebar(this);
        topBar = new TopBar(this);
        host = new FadeHost(cards);

        pages.put("home", new HomePage(this));
        pages.put("packages", new PackagesPage(this));
        pages.put("hotels", new HotelsPage(this));
        pages.put("destinations", new DestinationsPage(this));
        pages.put("bookings", new BookingsPage(this));
        pages.put("payments", new PaymentsPage(this));
        pages.put("profile", new ProfilePage(this));
        pages.put("tools", new ToolsPage(this));
        pages.forEach((k, p) -> host.add(p, k));

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(topBar, BorderLayout.NORTH);
        right.add(host, BorderLayout.CENTER);
        root.add(sidebar, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);

        Theme.onChange(() -> { if (isDisplayable()) repaint(); });

        Async.run(() -> CustomerDao.find(account.username()).orElse(null), c -> {
            customer = c;
            Page p = pages.get(current);
            if (p != null) p.onShow();
        }, t -> t.printStackTrace());

        navigate("home");
    }

    // ------------------------------------------------------------------ API for pages
    public Account account() { return account; }

    public Customer customer() { return customer; }

    public void setCustomer(Customer c) { this.customer = c; }

    public String firstName() {
        String n = account.name() == null || account.name().isBlank() ? account.username() : account.name();
        return n.trim().split("\\s+")[0];
    }

    public void navigate(String key) {
        Page next = pages.get(key);
        if (next == null) return;
        if (current != null && !current.equals(key)) pages.get(current).onHide();
        boolean changed = !key.equals(current);
        current = key;
        cards.show(host, key);
        topBar.set(next.title(), next.subtitle());
        sidebar.select(key);
        next.onShow();
        if (changed) host.fade();
    }

    /** Re-loads the data of the visible page (after a booking, payment, etc.). */
    public void refresh() {
        Page p = pages.get(current);
        if (p != null) p.onShow();
    }

    public void toast(String message) { Toast.success(this, message); }

    public void logout() {
        pages.values().forEach(Page::onHide);
        Session.clear();
        dispose();
        new AuthFrame().setVisible(true);
    }
}
