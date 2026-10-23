package tours.and.travels.management.systems.ui.widget;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Small rounded tag, e.g. "WINTER SPECIAL" or "AC +\u20B9500". */
public class Chip extends JComponent {

    private final String text;
    private final Theme.C role;

    public Chip(String text, Theme.C role) {
        this.text = text;
        this.role = role;
        setOpaque(false);
    }

    @Override public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(Theme.bold(11.5f));
        return new Dimension(fm.stringWidth(text) + 26, 26);
    }

    @Override public Dimension getMaximumSize() { return getPreferredSize(); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        g.setColor(role.alpha(32));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g.setFont(Theme.bold(11.5f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(role.get());
        g.drawString(text, 13, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
    }
}
