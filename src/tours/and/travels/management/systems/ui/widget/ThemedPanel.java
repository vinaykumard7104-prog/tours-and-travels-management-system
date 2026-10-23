package tours.and.travels.management.systems.ui.widget;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Panel with an optional theme-aware background, rounded corners and hairline border. */
public class ThemedPanel extends JPanel {

    private Theme.C bg;
    private Theme.C border;
    private int radius;

    public ThemedPanel(LayoutManager lm) { this(lm, null, 0); }

    public ThemedPanel(LayoutManager lm, Theme.C bg) { this(lm, bg, 0); }

    public ThemedPanel(LayoutManager lm, Theme.C bg, int radius) {
        super(lm);
        this.bg = bg;
        this.radius = radius;
        setOpaque(false);
    }

    public ThemedPanel background(Theme.C c) { this.bg = c; repaint(); return this; }

    public ThemedPanel border(Theme.C c) { this.border = c; repaint(); return this; }

    public ThemedPanel radius(int r) { this.radius = r; repaint(); return this; }

    @Override protected void paintComponent(Graphics g0) {
        Theme.C fillRole = bg != null ? bg : (isOpaque() ? Theme.C.BG : null);
        if (fillRole != null || border != null) {
            Graphics2D g = Ui.g2(g0);
            int w = getWidth(), h = getHeight();
            if (fillRole != null) {
                g.setColor(fillRole.get());
                if (radius > 0) g.fill(new RoundRectangle2D.Float(0, 0, w, h, radius * 2f, radius * 2f));
                else g.fillRect(0, 0, w, h);
            }
            if (border != null) {
                g.setColor(border.get());
                g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, radius * 2f, radius * 2f));
            }
            g.dispose();
        }
        // deliberately no super.paintComponent(): the L&F would repaint its default grey over our colours
    }
}
