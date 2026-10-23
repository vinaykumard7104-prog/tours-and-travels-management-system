package tours.and.travels.management.systems.ui.widget;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import tours.and.travels.management.systems.ui.Theme;

/** A label whose colour follows the active theme (so dark mode needs no rebuild). */
public class ThemedLabel extends JLabel {

    private Theme.C role;
    private Color fixed;

    public ThemedLabel(String text, Font font, Theme.C role) {
        super(text);
        setFont(font);
        this.role = role;
        setOpaque(false);
    }

    public ThemedLabel role(Theme.C r) { this.role = r; this.fixed = null; repaint(); return this; }

    public ThemedLabel color(Color c) { this.fixed = c; repaint(); return this; }

    @Override public Color getForeground() {
        if (fixed != null) return fixed;
        return role == null ? super.getForeground() : role.get();
    }
}
