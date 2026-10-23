package tours.and.travels.management.systems.ui.widget;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Segmented control with a sliding highlight. */
public class Segmented extends JComponent {

    private final String[] options;
    private int selected;
    private double pos;     // animated highlight position in segment units
    private final List<Runnable> listeners = new ArrayList<>();
    private int height = 46;

    public Segmented(String... options) {
        this.options = options;
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int seg = (int) (e.getX() / (getWidth() / (double) Segmented.this.options.length));
                setSelectedIndex(seg);
            }
        });
    }

    public Segmented compact() { height = 38; return this; }

    public int getSelectedIndex() { return selected; }

    public String getSelected() { return options[selected]; }

    public void setSelectedIndex(int i) {
        if (i < 0 || i >= options.length || i == selected) return;
        final double from = pos;
        selected = i;
        Anim.run(220, p -> { pos = from + (selected - from) * p; repaint(); }, null);
        for (Runnable r : new ArrayList<>(listeners)) r.run();
    }

    /** Selects without animation and without notifying listeners. */
    public void setSelectedSilently(int i) {
        if (i < 0 || i >= options.length) return;
        selected = i;
        pos = i;
        repaint();
    }

    public void onChange(Runnable r) { listeners.add(r); }

    @Override public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(Theme.bold(14));
        int max = 0;
        for (String s : options) max = Math.max(max, fm.stringWidth(s));
        return new Dimension((max + 40) * options.length + 8, height);
    }

    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, height); }

    @Override public Dimension getMinimumSize() { return new Dimension(100, height); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int w = getWidth(), h = getHeight();
        g.setColor(Theme.C.INPUT.get());
        g.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));
        g.setColor(Theme.C.BORDER.get());
        g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, h, h));
        double sw = (w - 8) / (double) options.length;
        double x = 4 + pos * sw;
        g.setPaint(new GradientPaint((float) x, 0, Theme.C.PRIMARY.get(), (float) (x + sw), h, Theme.C.PRIMARY2.get()));
        g.fill(new RoundRectangle2D.Double(x, 4, sw, h - 8, h - 8, h - 8));
        g.setFont(Theme.bold(14));
        FontMetrics fm = g.getFontMetrics();
        for (int i = 0; i < options.length; i++) {
            double t = Math.max(0, 1 - Math.abs(pos - i));
            g.setColor(Theme.mix(Theme.C.MUTED.get(), java.awt.Color.WHITE, t));
            int tx = (int) (4 + i * sw + (sw - fm.stringWidth(options[i])) / 2);
            g.drawString(options[i], tx, (h - fm.getHeight()) / 2 + fm.getAscent());
        }
        g.dispose();
    }
}
