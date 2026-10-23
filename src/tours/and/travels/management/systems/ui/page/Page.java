package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.ThemedPanel;

/** Base class for the screens shown inside the main window. */
public abstract class Page extends ThemedPanel {

    protected final MainFrame app;

    protected Page(MainFrame app) {
        super(new BorderLayout());
        this.app = app;
    }

    public abstract String title();

    public abstract String subtitle();

    /** Called every time the page becomes visible - refresh data here. */
    public void onShow() { }

    /** Called when the user navigates away - stop timers here. */
    public void onHide() { }

    /** Section heading with an optional action button on the right. */
    protected static JPanel section(String title, String subtitle, PillButton action) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new javax.swing.BoxLayout(t, javax.swing.BoxLayout.Y_AXIS));
        t.add(Ui.title(title, 20));
        if (subtitle != null) {
            t.add(Ui.vgap(2));
            t.add(Ui.muted(subtitle, 13.5f));
        }
        p.add(t, BorderLayout.WEST);
        if (action != null) {
            JPanel a = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            a.setOpaque(false);
            a.add(action);
            p.add(a, BorderLayout.EAST);
        }
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, p.getPreferredSize().height + 4));
        return p;
    }

    /** Scrollable page body with the standard margins. */
    protected static tours.and.travels.management.systems.ui.widget.VerticalPanel body() {
        tours.and.travels.management.systems.ui.widget.VerticalPanel v = new tours.and.travels.management.systems.ui.widget.VerticalPanel();
        v.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 40, 26));
        return v;
    }

    /** Equal-width grid whose height follows its content. */
    protected static JPanel grid(int cols, int gap) {
        JPanel g = new JPanel(new java.awt.GridLayout(0, cols, gap, gap)) {
            @Override public java.awt.Dimension getMaximumSize() {
                return new java.awt.Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        g.setOpaque(false);
        g.setAlignmentX(Component.LEFT_ALIGNMENT);
        return g;
    }

    /** A panel that keeps a fixed width while its height follows the content. */
    protected static JPanel fixedWidth(int width) {
        JPanel p = new JPanel() {
            @Override public java.awt.Dimension getPreferredSize() { return new java.awt.Dimension(width, super.getPreferredSize().height); }
            @Override public java.awt.Dimension getMinimumSize() { return getPreferredSize(); }
        };
        p.setOpaque(false);
        p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));
        return p;
    }

    /** Aligns any component to the left edge of a vertical stack. */
    protected static <T extends javax.swing.JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    protected static java.awt.Component gap(int h) { return Ui.vgap(h); }

    protected void failed(Throwable t) {
        t.printStackTrace();
        tours.and.travels.management.systems.ui.widget.Toast.error(this,
                tours.and.travels.management.systems.db.Database.explain(t));
    }
}
