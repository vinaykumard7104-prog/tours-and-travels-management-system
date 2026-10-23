package tours.and.travels.management.systems.ui.widget;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.BoxLayout;
import javax.swing.Scrollable;
import tours.and.travels.management.systems.ui.Theme;

/** A top-to-bottom stack that follows the width of the scroll pane it lives in. */
public class VerticalPanel extends ThemedPanel implements Scrollable {

    public VerticalPanel() { this(null); }

    public VerticalPanel(Theme.C bg) {
        super(null, bg);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 28; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return Math.max(100, r.height - 80); }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
