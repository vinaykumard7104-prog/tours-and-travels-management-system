package tours.and.travels.management.systems.ui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Circle with the user's initials. */
public class Avatar extends JComponent {

    private String name;
    private final int size;

    public Avatar(String name, int size) {
        this.name = name;
        this.size = size;
        setOpaque(false);
    }

    public void setName(String n) { this.name = n; repaint(); }

    @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
    @Override public Dimension getMaximumSize() { return getPreferredSize(); }
    @Override public Dimension getMinimumSize() { return getPreferredSize(); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        g.setPaint(new GradientPaint(0, 0, new Color(0xF59E0B), size, size, new Color(0xEF4444)));
        g.fillOval(0, 0, size, size);
        g.setFont(Theme.bold(size * 0.38f));
        FontMetrics fm = g.getFontMetrics();
        String s = Ui.initials(name);
        g.setColor(Color.WHITE);
        g.drawString(s, (size - fm.stringWidth(s)) / 2, (size - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
    }
}
