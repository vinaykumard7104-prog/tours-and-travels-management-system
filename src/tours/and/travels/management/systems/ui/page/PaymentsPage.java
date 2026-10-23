package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.security.SecureRandom;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.BookingDao;
import tours.and.travels.management.systems.dao.PaymentDao;
import tours.and.travels.management.systems.model.Models.Payment;
import tours.and.travels.management.systems.model.Models.Stats;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Dialogs;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.ModernTable;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.ProgressRing;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.Segmented;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Fmt;
import tours.and.travels.management.systems.util.Money;

/** Outstanding balance, a (simulated) checkout and payment history. */
public class PaymentsPage extends Page {

    private static final SecureRandom RNG = new SecureRandom();

    private final VerticalPanel content = body();
    private final ProgressRing ring = new ProgressRing();
    private final ThemedLabel total = Ui.title("", 15), paid = Ui.label("", 15, true, Theme.C.SUCCESS), due = Ui.label("", 15, true, Theme.C.ACCENT);
    private final Segmented method = new Segmented("UPI", "Card", "Net banking");
    private final Field amount = new Field("Amount", Ico.WALLET, false);
    private final Field upi = new Field("name@bank", Ico.PHONE, false);
    private final Field cardNo = new Field("1234 5678 9012 3456", Ico.CARD, false);
    private final Field expiry = new Field("MM/YY", Ico.CALENDAR, false);
    private final Combo bank = new Combo("State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Kotak Mahindra Bank", "Canara Bank").icon(Ico.BANK);
    private final CardLayout methodCards = new CardLayout();
    private final JPanel methodPanel = new JPanel(methodCards);
    private final ThemedLabel error = Ui.label(" ", 13, true, Theme.C.DANGER);
    private final PillButton pay = new PillButton("Pay now", PillButton.Style.PRIMARY).stretch();
    private final ModernTable history = new ModernTable(new String[]{"Reference", "Method", "Date", "Amount"},
            new int[]{ModernTable.LEFT, ModernTable.LEFT, ModernTable.LEFT, ModernTable.RIGHT});
    private int outstanding;

    public PaymentsPage(MainFrame app) {
        super(app);
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.gridx = 0; gc.weightx = 0;
        row.add(summaryCard(), gc);
        gc.gridx = 1; gc.weightx = 1;
        row.add(payCard(), gc);
        content.add(row);
        content.add(gap(18));

        content.add(left(section("Payment history", "Every payment you\u2019ve made", null)));
        content.add(gap(8));
        Card hc = new Card(new BorderLayout(), 0).noLift().clipContent();
        history.scroll.setPreferredSize(new java.awt.Dimension(400, 250));
        hc.add(history.scroll, BorderLayout.CENTER);
        content.add(left(hc));
        add(Scroll.page(content), BorderLayout.CENTER);

        method.onChange(() -> methodCards.show(methodPanel, new String[]{"upi", "card", "bank"}[method.getSelectedIndex()]));
        pay.addActionListener(e -> doPay());
    }

    private Card summaryCard() {
        Card c = new Card(new BorderLayout(0, 8), 20).noLift();
        c.setPreferredSize(new java.awt.Dimension(400, 440));
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(Ui.title("Your balance", 18), BorderLayout.NORTH);
        c.add(top, BorderLayout.NORTH);
        JPanel mid = new JPanel(new java.awt.GridBagLayout());
        mid.setOpaque(false);
        mid.add(ring);
        c.add(mid, BorderLayout.CENTER);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.add(Forms.kv("Total booked", total));
        rows.add(Ui.vgap(6));
        rows.add(Forms.kv("Paid", paid));
        rows.add(Ui.vgap(6));
        rows.add(Forms.kv("Outstanding", due));
        c.add(rows, BorderLayout.SOUTH);
        return c;
    }

    private Card payCard() {
        Card c = new Card(new BorderLayout(), 22).noLift();
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.add(left(Ui.title("Make a payment", 18)));
        f.add(left(Ui.muted("Demo checkout \u2013 no real money moves; only the amount and method are saved.", 13)));
        f.add(Ui.vgap(16));
        f.add(Forms.labeled("Pay with", method));
        f.add(Ui.vgap(14));

        methodPanel.setOpaque(false);
        methodPanel.add(wrap(Forms.labeled("UPI ID", upi)), "upi");
        JPanel cardForm = new JPanel();
        cardForm.setOpaque(false);
        cardForm.setLayout(new BoxLayout(cardForm, BoxLayout.Y_AXIS));
        cardForm.add(Forms.row(12, Forms.labeled("Card number", cardNo), Forms.labeled("Expiry", expiry)));
        methodPanel.add(wrap(cardForm), "card");
        methodPanel.add(wrap(Forms.labeled("Your bank", bank)), "bank");
        methodPanel.setAlignmentX(LEFT_ALIGNMENT);
        methodPanel.setPreferredSize(new java.awt.Dimension(300, 74));
        methodPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 74));
        f.add(methodPanel);
        f.add(Ui.vgap(12));
        f.add(Forms.labeled("Amount (\u20B9)", amount));
        f.add(Ui.vgap(4));
        f.add(left(error));
        f.add(Ui.vgap(6));
        pay.setAlignmentX(LEFT_ALIGNMENT);
        f.add(pay);
        c.add(f, BorderLayout.CENTER);
        return c;
    }

    private static JPanel wrap(JPanel p) {
        JPanel w = new JPanel(new BorderLayout());
        w.setOpaque(false);
        w.add(p, BorderLayout.NORTH);
        return w;
    }

    @Override public String title() { return "Payments"; }

    @Override public String subtitle() { return "Track what you owe and settle it."; }

    @Override public void onShow() {
        String u = app.account().username();
        Async.run(() -> new Object[]{BookingDao.stats(u), PaymentDao.list(u)}, r -> {
            Stats s = (Stats) r[0];
            @SuppressWarnings("unchecked") List<Payment> list = (List<Payment>) r[1];
            outstanding = s.outstanding();
            total.setText(Money.format(s.totalBooked()));
            paid.setText(Money.format(s.totalPaid()));
            due.setText(Money.format(outstanding));
            double ratio = s.totalBooked() == 0 ? 0 : Math.min(1, s.totalPaid() / (double) s.totalBooked());
            ring.set(ratio, Math.round(ratio * 100) + "%", s.totalBooked() == 0 ? "nothing booked" : "paid");
            amount.setText(outstanding > 0 ? String.valueOf(outstanding) : "");
            pay.setEnabled(true);
            history.clear();
            for (Payment p : list) history.model.addRow(new Object[]{p.reference(), p.method(), Fmt.date(p.paidOn()), Money.format(p.amount())});
        }, this::failed);
    }

    private void doPay() {
        int amt = Money.parse(amount.getText());
        String m = method.getSelected();
        if (amt <= 0) { fail("Enter an amount to pay.", amount); return; }
        if (outstanding > 0 && amt > outstanding) { fail("That is more than the " + Money.format(outstanding) + " outstanding.", amount); return; }
        if (outstanding == 0) { fail("You have nothing outstanding right now.", amount); return; }
        switch (method.getSelectedIndex()) {
            case 0 -> { if (!upi.getText().trim().matches("[\\w.\\-]{2,}@[\\w]{2,}")) { fail("Enter a valid UPI ID, e.g. name@bank.", upi); return; } }
            case 1 -> {
                if (!cardNo.getText().replaceAll("\\s", "").matches("\\d{16}")) { fail("Card number must be 16 digits.", cardNo); return; }
                if (!expiry.getText().trim().matches("(0[1-9]|1[0-2])/\\d{2}")) { fail("Expiry must look like 08/28.", expiry); return; }
            }
            default -> { }
        }
        if (!Dialogs.confirm(this, "Confirm payment", "Pay " + Money.format(amt) + " using " + m + "?", "Pay " + Money.format(amt), false)) return;
        String ref = "TXN" + (100000 + RNG.nextInt(900000)) + Long.toString(System.currentTimeMillis() % 100000);
        pay.setEnabled(false);
        error.setText(" ");
        Async.run(() -> { PaymentDao.add(app.account().username(), amt, m, ref); return true; }, ok -> {
            onShow();
            Toast.success(app, "Payment of " + Money.format(amt) + " received \u00B7 " + ref);
        }, t -> { pay.setEnabled(true); failed(t); });
    }

    private void fail(String msg, Field f) {
        error.setText(msg);
        f.setError(true);
    }
}
