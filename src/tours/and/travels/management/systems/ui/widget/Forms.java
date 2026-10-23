package tours.and.travels.management.systems.ui.widget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Layout helpers for forms. */
public final class Forms {

    private Forms() { }

    /** Small caption above a control. */
    public static JPanel labeled(String caption, JComponent control) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        ThemedLabel l = Ui.label(caption, 12.5f, true, Theme.C.MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        control.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Ui.vgap(7));
        p.add(control);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    /** Equal-width columns. */
    public static JPanel row(int gap, JComponent... cols) {
        JPanel p = new JPanel(new GridLayout(1, cols.length, gap, 0)) {
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, getPreferredSize().height); }
        };
        p.setOpaque(false);
        for (JComponent c : cols) p.add(c);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    public static <T extends JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    /** "Label ........ value" row used in price summaries. */
    public static JPanel kv(String key, ThemedLabel value) {
        JPanel p = new JPanel(new java.awt.BorderLayout());
        p.setOpaque(false);
        p.add(Ui.label(key, 14, false, Theme.C.MUTED), java.awt.BorderLayout.WEST);
        p.add(value, java.awt.BorderLayout.EAST);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, p.getPreferredSize().height + 6));
        return p;
    }

    /** Soft tinted box with padding. */
    public static ThemedPanel box(int pad) {
        ThemedPanel p = new ThemedPanel(null, Theme.C.SURFACE_ALT, 16) {
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, getPreferredSize().height); }
        };
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(pad, pad + 2, pad, pad + 2));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }
}
