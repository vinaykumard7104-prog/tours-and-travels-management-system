package tours.and.travels.management.systems.ui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Brand mark: gradient tile with a paper-plane, plus optional wordmark. */
public class Logo extends JComponent {

    private final int size;
    private final boolean wordmark;
    private final Color textColor;
    private final String title, subtitle;

    public Logo(int size, boolean wordmark, Color textColor, String title, String subtitle) {
        this.size = size;
        this.wordmark = wordmark;
        this.textColor = textColor;
        this.title = title;
        this.subtitle = subtitle;
        setOpaque(false);
    }

    public static Logo mark(int size) { return new Logo(size, false, null, "", ""); }

    @Override public Dimension getPreferredSize() {
        if (!wordmark) return new Dimension(size, size);
        FontMetrics fm = getFontMetrics(Theme.bold(size * 0.42f));
        return new Dimension(size + 14 + fm.stringWidth(title) + 4, size);
    }

    @Override public Dimension getMaximumSize() { return getPreferredSize(); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        g.setPaint(new GradientPaint(0, 0, new Color(0x3B82F6), size, size, new Color(0x06B6D4)));
        g.fill(new RoundRectangle2D.Float(0, 0, size, size, size * 0.34f, size * 0.34f));
        Ico.PLANE.paint(g, size * 0.22, size * 0.22, size * 0.56, Color.WHITE, 2.2);
        if (wordmark) {
            g.setFont(Theme.bold(size * 0.42f));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(textColor);
            int y = subtitle == null || subtitle.isEmpty() ? (size - fm.getHeight()) / 2 + fm.getAscent()
                    : (int) (size * 0.46);
            g.drawString(title, size + 14, y);
            if (subtitle != null && !subtitle.isEmpty()) {
                g.setFont(Theme.regular(size * 0.27f));
                g.setColor(Theme.alpha(textColor, 170));
                g.drawString(subtitle, size + 14, (int) (size * 0.82));
            }
        }
        g.dispose();
    }
}
