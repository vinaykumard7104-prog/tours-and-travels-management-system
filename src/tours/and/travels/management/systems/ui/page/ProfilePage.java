package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.dao.AccountDao;
import tours.and.travels.management.systems.dao.CustomerDao;
import tours.and.travels.management.systems.model.Models.Customer;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Avatar;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Chip;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Dialogs;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.Segmented;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Validate;

/** Traveller profile (add / update in one place) and account deletion. */
public class ProfilePage extends Page {

    private static final List<String> ID_TYPES = List.of("Passport", "Aadhaar Card", "PAN Card", "Ration Card", "Driving Licence");

    private final VerticalPanel content = body();
    private final Combo idType = new Combo(ID_TYPES.toArray(new String[0])).icon(Ico.ID);
    private final Field idNumber = new Field("ID number", Ico.ID, false);
    private final Segmented gender = new Segmented("Male", "Female", "Other");
    private final Field country = new Field("Country", Ico.GLOBE, false);
    private final Field address = new Field("Street, city, PIN", Ico.PIN, false);
    private final Field phone = new Field("+91 98765 43210", Ico.PHONE, false);
    private final Field email = new Field("you@example.com", Ico.MAIL, false);
    private final ThemedLabel error = Ui.label(" ", 13, true, Theme.C.DANGER);
    private final PillButton save = new PillButton("Save profile", PillButton.Style.PRIMARY).icon(Ico.CHECK);
    private final JPanel statusHost = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

    public ProfilePage(MainFrame app) {
        super(app);
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.NORTH;
        gc.gridx = 0; gc.weightx = 0;
        row.add(sideColumn(), gc);
        gc.gridx = 1; gc.weightx = 1;
        row.add(formCard(), gc);
        content.add(row);
        add(Scroll.page(content), BorderLayout.CENTER);
        save.addActionListener(e -> doSave());
        country.setText("India");
    }

    private JPanel sideColumn() {
        JPanel col = fixedWidth(350);
        Card who = new Card(new BorderLayout(), 24).noLift();
        JPanel in = new JPanel();
        in.setOpaque(false);
        in.setLayout(new BoxLayout(in, BoxLayout.Y_AXIS));
        Avatar av = new Avatar(app.account().name(), 92);
        av.setAlignmentX(Component.CENTER_ALIGNMENT);
        in.add(av);
        in.add(Ui.vgap(14));
        ThemedLabel n = Ui.title(app.account().name(), 20);
        n.setAlignmentX(Component.CENTER_ALIGNMENT);
        ThemedLabel u = Ui.muted("@" + app.account().username(), 14);
        u.setAlignmentX(Component.CENTER_ALIGNMENT);
        in.add(n);
        in.add(u);
        in.add(Ui.vgap(14));
        statusHost.setOpaque(false);
        in.add(statusHost);
        who.add(in, BorderLayout.CENTER);
        col.add(who);
        col.add(Ui.vgap(8));

        Card danger = new Card(new BorderLayout(), 20).noLift();
        JPanel d = new JPanel();
        d.setOpaque(false);
        d.setLayout(new BoxLayout(d, BoxLayout.Y_AXIS));
        d.add(left(Ui.label("Danger zone", 15, true, Theme.C.DANGER)));
        d.add(Ui.vgap(6));
        d.add(left(Ui.muted("Deleting your account removes your profile, bookings and payments for good.", 13)));
        d.add(Ui.vgap(14));
        PillButton del = new PillButton("Delete my account", PillButton.Style.DANGER_SOFT).icon(Ico.TRASH).compact();
        del.addActionListener(e -> deleteAccount());
        d.add(left(del));
        danger.add(d, BorderLayout.CENTER);
        col.add(danger);
        return col;
    }

    private Card formCard() {
        Card c = new Card(new BorderLayout(), 26).noLift();
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.add(left(Ui.title("Traveller details", 20)));
        f.add(left(Ui.muted("Used to confirm your bookings. Keep it accurate.", 13.5f)));
        f.add(Ui.vgap(20));
        f.add(Forms.row(16, Forms.labeled("ID type", idType), Forms.labeled("ID number", idNumber)));
        f.add(Ui.vgap(14));
        f.add(Forms.row(16, Forms.labeled("Gender", gender), Forms.labeled("Country", country)));
        f.add(Ui.vgap(14));
        f.add(Forms.labeled("Address", address));
        f.add(Ui.vgap(14));
        f.add(Forms.row(16, Forms.labeled("Phone", phone), Forms.labeled("Email", email)));
        f.add(Ui.vgap(6));
        f.add(left(error));
        f.add(Ui.vgap(8));
        JPanel b = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        b.setOpaque(false);
        b.add(save);
        f.add(left(b));
        c.add(f, BorderLayout.CENTER);
        return c;
    }

    @Override public String title() { return "My profile"; }

    @Override public String subtitle() { return "Your traveller details."; }

    @Override public void onShow() {
        Customer cu = app.customer();
        if (cu != null) {
            List<String> types = new ArrayList<>(ID_TYPES);
            if (cu.idType() != null && !types.contains(cu.idType())) types.add(0, cu.idType());
            idType.setItems(types);
            idType.setSelectedItem(cu.idType());
            idNumber.setText(cu.idNumber());
            country.setText(cu.country());
            address.setText(cu.address());
            phone.setText(cu.phone());
            email.setText(cu.email());
            gender.setSelectedSilently("Female".equalsIgnoreCase(cu.gender()) ? 1 : "Male".equalsIgnoreCase(cu.gender()) ? 0 : 2);
        }
        setStatus(cu != null);
    }

    private void setStatus(boolean complete) {
        statusHost.removeAll();
        statusHost.add(complete ? new Chip("PROFILE COMPLETE", Theme.C.SUCCESS) : new Chip("PROFILE INCOMPLETE", Theme.C.ACCENT));
        statusHost.revalidate();
        statusHost.repaint();
    }

    private void doSave() {
        error.setText(" ");
        if (Validate.blank(idNumber.getText())) { bad("Please enter your ID number.", idNumber); return; }
        if (Validate.blank(country.getText())) { bad("Please enter your country.", country); return; }
        if (Validate.blank(address.getText())) { bad("Please enter your address.", address); return; }
        if (!Validate.phone(phone.getText())) { bad("Enter a valid phone number (7\u201316 digits).", phone); return; }
        if (!Validate.email(email.getText())) { bad("Enter a valid email address.", email); return; }
        Customer cu = new Customer(app.account().username(), idType.getSelectedItem(), idNumber.getText().trim(),
                app.account().name(), gender.getSelected(), country.getText().trim(), address.getText().trim(),
                phone.getText().trim(), email.getText().trim());
        save.setEnabled(false);
        Async.run(() -> { CustomerDao.save(cu); return true; }, ok -> {
            save.setEnabled(true);
            app.setCustomer(cu);
            Toast.success(app, "Profile saved");
        }, t -> { save.setEnabled(true); failed(t); });
    }

    private void bad(String msg, Field f) { error.setText(msg); f.setError(true); }

    private void deleteAccount() {
        if (!Dialogs.confirm(this, "Delete your account?",
                "This permanently removes your account, traveller profile, bookings and payments. This can\u2019t be undone.",
                "Delete everything", true)) return;
        Async.run(() -> { AccountDao.deleteEverything(app.account().username()); return true; },
                ok -> app.logout(), this::failed);
    }
}
