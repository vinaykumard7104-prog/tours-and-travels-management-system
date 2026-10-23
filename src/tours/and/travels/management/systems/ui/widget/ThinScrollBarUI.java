package tours.and.travels.management.systems.ui.widget;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Slim, rounded, theme-aware scroll bar. */
public class ThinScrollBarUI extends BasicScrollBarUI {

    @Override protected JButton createDecreaseButton(int orientation) { return zero(); }

    @Override protected JButton createIncreaseButton(int orientation) { return zero(); }

    private static JButton zero() {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(0, 0));
        b.setMinimumSize(new Dimension(0, 0));
        b.setMaximumSize(new Dimension(0, 0));
        return b;
    }

    @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { }

    @Override protected void paintThumb(Graphics g0, JComponent c, Rectangle r) {
        if (r.isEmpty() || !scrollbar.isEnabled()) return;
        Graphics2D g = Ui.g2(g0);
        boolean active = isThumbRollover() || isDragging;
        g.setColor(Theme.alpha(Theme.C.MUTED.get(), active ? 170 : 90));
        int inset = 2;
        if (scrollbar.getOrientation() == java.awt.Adjustable.VERTICAL) {
            int w = Math.min(r.width - inset * 2, 7);
            g.fillRoundRect(r.x + (r.width - w) / 2, r.y + 2, w, r.height - 4, w, w);
        } else {
            int h = Math.min(r.height - inset * 2, 7);
            g.fillRoundRect(r.x + 2, r.y + (r.height - h) / 2, r.width - 4, h, h, h);
        }
        g.dispose();
    }
}
