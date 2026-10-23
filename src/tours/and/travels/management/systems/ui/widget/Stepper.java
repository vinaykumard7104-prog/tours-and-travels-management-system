package tours.and.travels.management.systems.ui.widget;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** "-  3  +" number stepper. */
public class Stepper extends JComponent {

    private int value, min, max;
    private final String unit;
    private final List<Runnable> listeners = new ArrayList<>();
    private int hot;   // 0 none, -1 minus, 1 plus

    public Stepper(int value, int min, int max, String unit) {
        this.value = value;
        this.min = min;
        this.max = max;
        this.unit = unit;
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (e.getX() < getHeight()) set(Stepper.this.value - 1);
                else if (e.getX() > getWidth() - getHeight()) set(Stepper.this.value + 1);
            }
            @Override public void mouseExited(MouseEvent e) { hot = 0; repaint(); }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int h = e.getX() < getHeight() ? -1 : (e.getX() > getWidth() - getHeight() ? 1 : 0);
                if (h != hot) { hot = h; repaint(); }
            }
        });
    }

    public int getValue() { return value; }

    public void set(int v) {
        v = Math.max(min, Math.min(max, v));
        if (v == value) return;
        value = v;
        repaint();
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    public void onChange(Runnable r) { listeners.add(r); }

    @Override public Dimension getPreferredSize() { return new Dimension(200, 48); }
    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 48); }
    @Override public Dimension getMinimumSize() { return new Dimension(140, 48); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int w = getWidth(), h = getHeight();
        InputStyle.paintBox(g, w, h, false, false, 0);
        paintButton(g, 5, Ico.MINUS, hot == -1, value > min, h);
        paintButton(g, w - h + 5, Ico.PLUS, hot == 1, value < max, h);
        String s = value + (unit == null || unit.isEmpty() ? "" : " " + (value == 1 ? unit : unit + "s"));
        g.setFont(Theme.bold(16));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Theme.C.TEXT.get());
        g.drawString(s, (w - fm.stringWidth(s)) / 2, (h - fm.getHeight()) / 2 + fm.getAscent());
        g.dispose();
    }

    private void paintButton(Graphics2D g, int x, Ico ico, boolean hot, boolean enabled, int h) {
        int d = h - 10;
        g.setColor(hot && enabled ? Theme.C.PRIMARY.alpha(40) : Theme.C.SURFACE.get());
        g.fillOval(x, 5, d, d);
        ico.paint(g, x + d * 0.28, 5 + d * 0.28, d * 0.44,
                enabled ? Theme.C.PRIMARY.get() : Theme.alpha(Theme.C.MUTED.get(), 120), 2.3);
    }
}
