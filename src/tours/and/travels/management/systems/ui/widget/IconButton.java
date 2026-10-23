package tours.and.travels.management.systems.ui.widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Round icon-only button. */
public class IconButton extends JButton {

    private Ico icon;
    private final int size;
    private final Hover hover = new Hover(this);
    private boolean filled;
    private Theme.C tint = Theme.C.MUTED;

    public IconButton(Ico icon, int size) {
        this.icon = icon;
        this.size = size;
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public IconButton filled() { this.filled = true; return this; }

    public IconButton tint(Theme.C c) { this.tint = c; return this; }

    public void setIco(Ico i) { this.icon = i; repaint(); }

    @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
    @Override public Dimension getMinimumSize() { return getPreferredSize(); }
    @Override public Dimension getMaximumSize() { return getPreferredSize(); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        double hv = hover.get();
        int w = getWidth(), h = getHeight();
        Color bg = filled ? Theme.mix(Theme.C.SURFACE_ALT.get(), Theme.C.BORDER.get(), hv * 0.8)
                          : Theme.alpha(Theme.C.TEXT.get(), (int) (24 * hv));
        g.setColor(bg);
        g.fill(new Ellipse2D.Double(0, 0, w, h));
        if (filled) {
            g.setColor(Theme.C.BORDER.get());
            g.draw(new Ellipse2D.Double(0.5, 0.5, w - 1, h - 1));
        }
        Color c = Theme.mix(tint.get(), Theme.C.TEXT.get(), hv);
        double is = size * 0.5;
        icon.paint(g, (w - is) / 2, (h - is) / 2, is, c, 2.1);
        g.dispose();
    }
}
