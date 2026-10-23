package tours.and.travels.management.systems.ui.page;

import java.util.List;
import tours.and.travels.management.systems.dao.BookingDao;
import tours.and.travels.management.systems.model.Models.Customer;
import tours.and.travels.management.systems.model.Models.TravelPackage;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.ModernDialog;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Stepper;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.ThemedPanel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Money;

/** Booking form for a tour package with live price. */
final class BookPackageDialog {

    private BookPackageDialog() { }

    static void show(MainFrame app, List<TravelPackage> all, TravelPackage preselected) {
        ModernDialog d = new ModernDialog(app, "Book a package", "Choose your package and group size.", 520);
        Combo combo = new Combo(all.stream().map(TravelPackage::name).toArray(String[]::new));
        if (preselected != null) combo.setSelectedItem(preselected.name());
        Stepper persons = new Stepper(2, 1, 30, "person");

        d.body().add(Forms.row(14, Forms.labeled("Package", combo), Forms.labeled("Travellers", persons)));
        d.body().add(Ui.vgap(16));

        Customer cu = app.customer();
        ThemedPanel who = Forms.box(14);
        if (cu == null) {
            who.add(Ui.label("Your traveller profile is incomplete", 14, true, Theme.C.ACCENT));
            who.add(Ui.vgap(4));
            who.add(Ui.muted("We need your ID and phone number to confirm a booking.", 13));
        } else {
            who.add(Ui.label("Booking for", 12, true, Theme.C.MUTED));
            who.add(Ui.vgap(4));
            who.add(Ui.title(cu.name() == null || cu.name().isBlank() ? app.account().name() : cu.name(), 15));
            who.add(Ui.muted(cu.idType() + " \u00B7 " + cu.idNumber() + "   |   " + cu.phone(), 13));
        }
        d.body().add(who);
        d.body().add(Ui.vgap(14));

        ThemedLabel perPerson = Ui.title("", 14), count = Ui.title("", 14), total = Ui.label("", 24, true, Theme.C.PRIMARY);
        ThemedPanel sum = Forms.box(14);
        sum.add(Forms.kv("Price per person", perPerson));
        sum.add(Ui.vgap(4));
        sum.add(Forms.kv("Travellers", count));
        sum.add(Ui.vgap(10));
        sum.add(Forms.kv("Total", total));
        d.body().add(sum);

        Runnable update = () -> {
            TravelPackage p = all.get(Math.max(0, combo.getSelectedIndex()));
            perPerson.setText(Money.format(p.price()));
            count.setText("\u00D7 " + persons.getValue());
            total.setText(Money.format((long) p.price() * persons.getValue()));
        };
        combo.addActionListener(e -> update.run());
        persons.onChange(update);
        update.run();

        PillButton cancel = new PillButton("Cancel", PillButton.Style.SECONDARY);
        PillButton ok = new PillButton(cu == null ? "Complete profile" : "Confirm booking", PillButton.Style.PRIMARY);
        cancel.addActionListener(e -> d.dispose());
        ok.addActionListener(e -> {
            if (app.customer() == null) { d.dispose(); app.navigate("profile"); return; }
            TravelPackage p = all.get(combo.getSelectedIndex());
            int n = persons.getValue();
            ok.setEnabled(false);
            Async.run(() -> { BookingDao.bookPackage(app.account().username(), app.customer(), p.name(), n, p.price() * n); return true; },
                    r -> { d.dispose(); app.refresh(); Toast.success(app, p.name() + " booked for " + n + (n == 1 ? " traveller" : " travellers")); },
                    t -> { ok.setEnabled(true); Toast.error(d, Database.explain(t)); });
        });
        d.footer().add(cancel);
        d.footer().add(ok);
        d.getRootPane().setDefaultButton(ok);
        d.open();
    }
}
