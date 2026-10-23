package tours.and.travels.management.systems.ui.widget;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Circular progress indicator with centred text. */
public class ProgressRing extends JComponent {

    private double shown, target;
    private String big = "", small = "";

    public ProgressRing() { setOpaque(false); }

    public void set(double progress, String big, String small) {
        this.big = big;
        this.small = small;
        final double from = shown;
        target = Math.max(0, Math.min(1, progress));
        Anim.run(800, p -> { shown = from + (target - from) * p; repaint(); }, null);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(190, 190); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int s = Math.min(getWidth(), getHeight()) - 24;
        int x = (getWidth() - s) / 2, y = (getHeight() - s) / 2;
        g.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Theme.C.BORDER.get());
        g.draw(new Arc2D.Double(x, y, s, s, 0, 360, Arc2D.OPEN));
        if (shown > 0.003) {
            g.setPaint(new GradientPaint(x, y, Theme.C.PRIMARY.get(), x + s, y + s, Theme.C.PRIMARY2.get()));
            g.draw(new Arc2D.Double(x, y, s, s, 90, -360 * shown, Arc2D.OPEN));
        }
        g.setFont(Theme.bold(28));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Theme.C.TEXT.get());
        g.drawString(big, (getWidth() - fm.stringWidth(big)) / 2, getHeight() / 2 + 4);
        g.setFont(Theme.regular(12.5f));
        fm = g.getFontMetrics();
        g.setColor(Theme.C.MUTED.get());
        g.drawString(small, (getWidth() - fm.stringWidth(small)) / 2, getHeight() / 2 + 24);
        g.dispose();
    }
}
