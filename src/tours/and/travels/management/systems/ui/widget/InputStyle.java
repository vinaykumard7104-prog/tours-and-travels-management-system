package tours.and.travels.management.systems.ui.widget;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import tours.and.travels.management.systems.ui.Theme;

/** Shared look for text fields, combo boxes and steppers. */
final class InputStyle {

    static final int RADIUS = 14;

    private InputStyle() { }

    static void paintBox(Graphics2D g, int w, int h, boolean focused, boolean error, double hover) {
        RoundRectangle2D box = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, RADIUS * 2, RADIUS * 2);
        g.setColor(Theme.C.INPUT.get());
        g.fill(box);
        if (focused || error) {
            Theme.C c = error ? Theme.C.DANGER : Theme.C.PRIMARY;
            g.setColor(c.alpha(55));
            g.setStroke(new BasicStroke(4f));
            g.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, RADIUS * 2, RADIUS * 2));
            g.setColor(c.get());
            g.setStroke(new BasicStroke(1.6f));
            g.draw(box);
        } else {
            g.setColor(Theme.mix(Theme.C.BORDER.get(), Theme.C.PRIMARY.get(), hover * 0.45));
            g.setStroke(new BasicStroke(1f));
            g.draw(box);
        }
    }
}
