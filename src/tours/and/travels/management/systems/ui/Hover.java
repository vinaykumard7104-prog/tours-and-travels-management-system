package tours.and.travels.management.systems.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.util.function.DoubleConsumer;
import javax.swing.Timer;

/** Smoothly animated 0..1 "hover amount" for a component (and optionally all of its children). */
public final class Hover {

    private final JComponent owner;
    private double value, target;
    private final Timer timer;
    private DoubleConsumer onTick;

    private final MouseAdapter listener = new MouseAdapter() {
        @Override public void mouseEntered(MouseEvent e) { setTarget(1); }
        @Override public void mouseExited(MouseEvent e) {
            Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), owner);
            setTarget(owner.contains(p) ? 1 : 0);
        }
    };

    public Hover(JComponent owner) {
        this.owner = owner;
        timer = new Timer(15, e -> tick());
        owner.addMouseListener(listener);
    }

    /** Makes hovering over any descendant count as hovering over the owner. Call after the children exist. */
    public void attachTree(Component c) {
        if (c != owner) c.addMouseListener(listener);
        if (c instanceof Container ct) for (Component child : ct.getComponents()) attachTree(child);
    }

    /** Called on every animation step with the current 0..1 value. */
    public Hover onTick(DoubleConsumer c) { this.onTick = c; return this; }

    public double get() { return value; }

    public void setTarget(double t) {
        target = t;
        if (!timer.isRunning()) timer.start();
    }

    private void tick() {
        double d = target - value;
        if (Math.abs(d) < 0.02) {
            value = target;
            timer.stop();
        } else {
            value += d * 0.28;
        }
        if (onTick != null) onTick.accept(value);
        owner.repaint();
    }
}
