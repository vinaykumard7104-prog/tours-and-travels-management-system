package tours.and.travels.management.systems.ui.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Rounded drop-down with a custom popup list. */
public class Combo extends JComponent {

    private final List<String> items = new ArrayList<>();
    private int selected = -1;
    private boolean focused;
    private final Hover hover = new Hover(this);
    private final List<ActionListener> listeners = new ArrayList<>();
    private JPopupMenu popup;
    private Ico icon;

    public Combo(String... items) {
        setItems(Arrays.asList(items));
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { requestFocusInWindow(); togglePopup(); }
        });
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
        });
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) select(Math.min(Combo.this.items.size() - 1, selected + 1));
                else if (e.getKeyCode() == KeyEvent.VK_UP) select(Math.max(0, selected - 1));
                else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) togglePopup();
            }
        });
    }

    public Combo icon(Ico i) { this.icon = i; return this; }

    public void setItems(Collection<String> newItems) {
        String previous = getSelectedItem();
        items.clear();
        items.addAll(newItems);
        selected = items.isEmpty() ? -1 : Math.max(0, previous == null ? 0 : items.indexOf(previous));
        repaint();
    }

    public String getSelectedItem() { return selected >= 0 && selected < items.size() ? items.get(selected) : null; }

    public int getSelectedIndex() { return selected; }

    public void setSelectedItem(String s) {
        int i = items.indexOf(s);
        if (i >= 0) { selected = i; repaint(); }
    }

    public void addActionListener(ActionListener l) { listeners.add(l); }

    private void select(int i) {
        if (i < 0 || i >= items.size() || i == selected) return;
        selected = i;
        repaint();
        ActionEvent ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "changed");
        for (ActionListener l : new ArrayList<>(listeners)) l.actionPerformed(ev);
    }

    private void togglePopup() {
        if (popup != null && popup.isVisible()) { popup.setVisible(false); return; }
        if (items.isEmpty()) return;
        popup = new JPopupMenu();
        popup.setBorder(new LineBorder(Theme.C.BORDER.get(), 1, true));
        popup.setBackground(Theme.C.SURFACE.get());
        ThemedPanel list = new ThemedPanel(null, Theme.C.SURFACE);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        int w = getWidth();
        for (int i = 0; i < items.size(); i++) {
            final int idx = i;
            list.add(new Row(items.get(i), i == selected, w - 12, () -> { popup.setVisible(false); select(idx); }));
        }
        JScrollPane sp = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(Theme.C.SURFACE.get());
        sp.getVerticalScrollBar().setUI(new ThinScrollBarUI());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        int rows = Math.min(items.size(), 7);
        sp.setPreferredSize(new Dimension(w, rows * 40 + 12));
        popup.add(sp);
        popup.show(this, 0, getHeight() + 4);
    }

    @Override public Dimension getPreferredSize() { return new Dimension(200, 48); }

    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 48); }

    @Override public Dimension getMinimumSize() { return new Dimension(80, 48); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int w = getWidth(), h = getHeight();
        InputStyle.paintBox(g, w, h, focused || (popup != null && popup.isVisible()), false, hover.get());
        int x = 16;
        if (icon != null) {
            icon.paint(g, 16, (h - 20) / 2.0, 20, focused ? Theme.C.PRIMARY.get() : Theme.C.MUTED.get(), 1.8);
            x = 46;
        }
        g.setFont(Theme.regular(15));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Theme.C.TEXT.get());
        String s = getSelectedItem() == null ? "" : getSelectedItem();
        while (fm.stringWidth(s) > w - x - 44 && s.length() > 3) s = s.substring(0, s.length() - 2);
        g.drawString(s, x, (h - fm.getHeight()) / 2 + fm.getAscent());
        Ico.CHEVRON_DOWN.paint(g, w - 34, (h - 18) / 2.0, 18, Theme.C.MUTED.get(), 2);
        g.dispose();
    }

    /** One selectable row of the popup. */
    private static final class Row extends JComponent {
        private final String text;
        private final boolean selected;
        private final Hover hover = new Hover(this);
        private final int width;

        Row(String text, boolean selected, int width, Runnable onPick) {
            this.text = text;
            this.selected = selected;
            this.width = width;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseReleased(MouseEvent e) { onPick.run(); }
            });
        }

        @Override public Dimension getPreferredSize() { return new Dimension(width, 40); }
        @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 40); }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = Ui.g2(g0);
            int w = getWidth(), h = getHeight();
            if (selected) g.setColor(Theme.C.PRIMARY.alpha(30));
            else g.setColor(Theme.alpha(Theme.C.TEXT.get(), (int) (18 * hover.get())));
            g.fillRoundRect(0, 2, w, h - 4, 12, 12);
            g.setFont(Theme.font(14, selected ? java.awt.Font.BOLD : java.awt.Font.PLAIN));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(selected ? Theme.C.PRIMARY.get() : Theme.C.TEXT.get());
            g.drawString(text, 12, (h - fm.getHeight()) / 2 + fm.getAscent());
            if (selected) Ico.CHECK.paint(g, w - 28, (h - 16) / 2.0, 16, Theme.C.PRIMARY.get(), 2.2);
            g.dispose();
        }
    }
}
