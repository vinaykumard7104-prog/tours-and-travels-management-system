package tours.and.travels.management.systems;

import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.screen.SplashFrame;

/** Application entry point. */
public final class Main {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Main.class);

    private Main() { }

    public static Preferences prefs() { return PREFS; }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) { }
        ToolTipManager.sharedInstance().setInitialDelay(350);
        Theme.setDark(PREFS.getBoolean("dark", false));
        Theme.onChange(() -> PREFS.putBoolean("dark", Theme.isDark()));
        SwingUtilities.invokeLater(() -> new SplashFrame().setVisible(true));
    }
}
