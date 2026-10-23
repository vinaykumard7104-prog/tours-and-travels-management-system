package tours.and.travels.management.systems.ui.widget;

import java.awt.Component;
import javax.swing.SwingUtilities;
import tours.and.travels.management.systems.ui.Theme;

/** Modern replacements for JOptionPane. */
public final class Dialogs {

    private Dialogs() { }

    public static boolean confirm(Component parent, String title, String message, String confirmText, boolean danger) {
        final boolean[] result = {false};
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, null, 440);
        d.body().add(new WrapText(message, Theme.regular(14.5f), Theme.C.MUTED));
        PillButton cancel = new PillButton("Cancel", PillButton.Style.SECONDARY);
        PillButton ok = new PillButton(confirmText, danger ? PillButton.Style.DANGER : PillButton.Style.PRIMARY);
        cancel.addActionListener(e -> d.dispose());
        ok.addActionListener(e -> { result[0] = true; d.dispose(); });
        d.footer().add(cancel);
        d.footer().add(ok);
        d.getRootPane().setDefaultButton(ok);
        d.open();
        return result[0];
    }

    public static void message(Component parent, String title, String message) {
        ModernDialog d = new ModernDialog(SwingUtilities.getWindowAncestor(parent), title, null, 440);
        d.body().add(new WrapText(message, Theme.regular(14.5f), Theme.C.MUTED));
        PillButton ok = new PillButton("Got it", PillButton.Style.PRIMARY);
        ok.addActionListener(e -> d.dispose());
        d.footer().add(ok);
        d.getRootPane().setDefaultButton(ok);
        d.open();
    }
}
