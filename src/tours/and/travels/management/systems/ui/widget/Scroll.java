package tours.and.travels.management.systems.ui.widget;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

/** Factory for transparent scroll panes with the thin scroll bar. */
public final class Scroll {

    private Scroll() { }

    /** Page-level scroll pane: the bar space is always reserved so every page has the same width. */
    public static JScrollPane page(Component view) {
        JScrollPane sp = of(view);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return sp;
    }

    public static JScrollPane of(Component view) {
        JScrollPane sp = new JScrollPane(view, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(24);
        return sp;
    }
}
