package tours.and.travels.management.systems.ui.screen;

import java.awt.Window;
import java.sql.Connection;
import java.sql.DriverManager;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.db.DbConfig;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.ModernDialog;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.util.Async;

/** Lets the user fix MySQL connection settings without editing any file. */
final class DbSettingsDialog {

    private DbSettingsDialog() { }

    static void show(Window owner, Runnable onSaved) {
        DbConfig c = Database.config();
        ModernDialog d = new ModernDialog(owner, "Database settings",
                "Same details you use to connect in MySQL Workbench.", 520);
        Field host = new Field("localhost", Ico.GLOBE, false);
        Field port = new Field("3306", null, false);
        Field name = new Field("travelmanagementsystem", Ico.NOTE, false);
        Field user = new Field("root", Ico.USER, false);
        Field pass = new Field("MySQL password", Ico.LOCK, true);
        host.setText(c.host); port.setText(c.port); name.setText(c.database); user.setText(c.user); pass.setText(c.password);
        ThemedLabel result = Ui.label(" ", 13, true, Theme.C.MUTED);

        d.body().add(Forms.row(14, Forms.labeled("Host", host), Forms.labeled("Port", port)));
        d.body().add(Ui.vgap(12));
        d.body().add(Forms.labeled("Database name", name));
        d.body().add(Ui.vgap(12));
        d.body().add(Forms.row(14, Forms.labeled("User", user), Forms.labeled("Password", pass)));
        d.body().add(Ui.vgap(12));
        d.body().add(Forms.left(result));

        PillButton test = new PillButton("Test connection", PillButton.Style.SECONDARY);
        PillButton save = new PillButton("Save & retry", PillButton.Style.PRIMARY);
        d.footer().add(test);
        d.footer().add(save);

        java.util.function.Supplier<DbConfig> read = () -> {
            DbConfig n = new DbConfig();
            n.host = host.getText().trim(); n.port = port.getText().trim(); n.database = name.getText().trim();
            n.user = user.getText().trim(); n.password = pass.getText();
            return n;
        };
        test.addActionListener(e -> {
            DbConfig n = read.get();
            result.role(Theme.C.MUTED).setText("Testing\u2026");
            Async.run(() -> {
                try (Connection con = DriverManager.getConnection(n.serverUrl(), n.user, n.password)) {
                    return "OK";
                }
            }, ok -> result.role(Theme.C.SUCCESS).setText("Connected to MySQL successfully."),
               err -> {
                   Database.config();
                   result.role(Theme.C.DANGER).setText(shorten(explain(n, err)));
               });
        });
        save.addActionListener(e -> {
            DbConfig n = read.get();
            try {
                n.save();
                Database.reloadConfig();
                d.dispose();
                onSaved.run();
            } catch (Exception ex) {
                result.role(Theme.C.DANGER).setText("Could not save: " + ex.getMessage());
            }
        });
        d.open();
    }

    private static String explain(DbConfig n, Throwable t) {
        String m = String.valueOf(t.getMessage());
        if (m.contains("Access denied")) return "Wrong user name or password.";
        if (m.contains("Communications link failure") || m.contains("refused"))
            return "MySQL is not reachable at " + n.host + ":" + n.port + " - is the service running?";
        return m;
    }

    private static String shorten(String s) { return s.length() > 80 ? s.substring(0, 78) + "\u2026" : s; }
}
