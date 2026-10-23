package tours.and.travels.management.systems.ui.page;

import java.util.List;
import tours.and.travels.management.systems.dao.BookingDao;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Customer;
import tours.and.travels.management.systems.model.Models.Hotel;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.ModernDialog;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Segmented;
import tours.and.travels.management.systems.ui.widget.Stepper;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.ThemedPanel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Money;

/** Booking form for a hotel stay with live price. */
final class BookHotelDialog {

    private BookHotelDialog() { }

    static void show(MainFrame app, List<Hotel> all, Hotel preselected) {
        ModernDialog d = new ModernDialog(app, "Book a hotel", "Pick your stay \u2013 the total updates as you go.", 560);
        Combo combo = new Combo(all.stream().map(Hotel::name).toArray(String[]::new));
        if (preselected != null) combo.setSelectedItem(preselected.name());
        Stepper persons = new Stepper(2, 1, 20, "guest");
        Stepper nights = new Stepper(2, 1, 60, "night");
        Segmented room = new Segmented("Non-AC", "AC");
        Segmented food = new Segmented("No meals", "Meals included");

        d.body().add(Forms.labeled("Hotel", combo));
        d.body().add(Ui.vgap(14));
        d.body().add(Forms.row(14, Forms.labeled("Guests", persons), Forms.labeled("Nights", nights)));
        d.body().add(Ui.vgap(14));
        d.body().add(Forms.row(14, Forms.labeled("Room type", room), Forms.labeled("Food", food)));
        d.body().add(Ui.vgap(16));

        ThemedLabel rate = Ui.title("", 14), stay = Ui.title("", 14), total = Ui.label("", 24, true, Theme.C.PRIMARY);
        ThemedPanel sum = Forms.box(14);
        sum.add(Forms.kv("Rate per guest / night", rate));
        sum.add(Ui.vgap(4));
        sum.add(Forms.kv("Stay", stay));
        sum.add(Ui.vgap(10));
        sum.add(Forms.kv("Total", total));
        d.body().add(sum);

        Runnable update = () -> {
            Hotel h = all.get(Math.max(0, combo.getSelectedIndex()));
            int r = h.costPerPerson() + (room.getSelectedIndex() == 1 ? h.ac() : 0) + (food.getSelectedIndex() == 1 ? h.food() : 0);
            rate.setText(Money.format(r));
            stay.setText(persons.getValue() + " \u00D7 " + nights.getValue());
            total.setText(Money.format((long) r * persons.getValue() * nights.getValue()));
        };
        combo.addActionListener(e -> update.run());
        persons.onChange(update);
        nights.onChange(update);
        room.onChange(update);
        food.onChange(update);
        update.run();

        Customer cu = app.customer();
        PillButton cancel = new PillButton("Cancel", PillButton.Style.SECONDARY);
        PillButton ok = new PillButton(cu == null ? "Complete profile" : "Confirm booking", PillButton.Style.PRIMARY);
        cancel.addActionListener(e -> d.dispose());
        ok.addActionListener(e -> {
            if (app.customer() == null) { d.dispose(); app.navigate("profile"); return; }
            Hotel h = all.get(combo.getSelectedIndex());
            int g = persons.getValue(), n = nights.getValue();
            boolean ac = room.getSelectedIndex() == 1, meals = food.getSelectedIndex() == 1;
            int rt = h.costPerPerson() + (ac ? h.ac() : 0) + (meals ? h.food() : 0);
            ok.setEnabled(false);
            Async.run(() -> { BookingDao.bookHotel(app.account().username(), app.customer(), h.name(), g, n, ac, meals, rt * g * n); return true; },
                    r -> { d.dispose(); app.refresh(); Toast.success(app, h.name() + " booked for " + n + (n == 1 ? " night" : " nights")); },
                    t -> { ok.setEnabled(true); Toast.error(d, Database.explain(t)); });
        });
        d.footer().add(cancel);
        d.footer().add(ok);
        d.getRootPane().setDefaultButton(ok);
        d.open();
    }
}
