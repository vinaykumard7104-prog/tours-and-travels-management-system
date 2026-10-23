package tours.and.travels.management.systems.ui.widget;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Container;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Rounded surface with a soft shadow that lifts slightly on hover. */
public class Card extends JPanel {

    public static final int SHADOW = 10;

    private final int radius;
    private final int pad;
    private Theme.C fill = Theme.C.SURFACE;
    private final Hover hover = new Hover(this);
    private boolean lift = true;
    private boolean clip;

    public Card(LayoutManager lm, int pad) { this(lm, pad, 20); }

    public Card(LayoutManager lm, int pad, int radius) {
        super(lm);
        this.pad = pad;
        this.radius = radius;
        setOpaque(false);
        setBorder(new EmptyBorder(SHADOW + pad, SHADOW + pad, SHADOW + pad, SHADOW + pad));
    }

    public Card fill(Theme.C c) { this.fill = c; return this; }

    public Card noLift() { this.lift = false; return this; }

    /** Clips children to the rounded outline (for tables / images that run edge to edge). */
    public Card clipContent() { this.clip = true; return this; }

    @Override protected void paintChildren(Graphics g) {
        if (!clip) { super.paintChildren(g); return; }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.clip(new RoundRectangle2D.Double(SHADOW, SHADOW, getWidth() - 2 * SHADOW, getHeight() - 2 * SHADOW, radius * 2, radius * 2));
        super.paintChildren(g2);
        g2.dispose();
    }

    /** Call once all children have been added so hovering over them also animates the card. */
    public Card arm() {
        hover.attachTree(this);
        return this;
    }

    /** Makes the whole card clickable (buttons and inputs inside keep their own behaviour). */
    public Card onClick(Runnable r) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MouseAdapter click = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { r.run(); }
        };
        attachClick(this, click);
        hover.attachTree(this);
        return this;
    }

    private void attachClick(Component c, MouseAdapter click) {
        if (c instanceof AbstractButton || c instanceof Field) return;
        c.addMouseListener(click);
        if (c != this && c instanceof JComponent jc) jc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (c instanceof Container ct) for (Component k : ct.getComponents()) attachClick(k, click);
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        double hv = lift ? hover.get() : 0;
        int w = getWidth() - 2 * SHADOW, h = getHeight() - 2 * SHADOW;
        double y = SHADOW - hv * 2;
        Ui.shadow(g, SHADOW, y, w, h, radius, SHADOW, (float) (0.8 + 0.9 * hv));
        g.setColor(fill.get());
        g.fill(new RoundRectangle2D.Double(SHADOW, y, w, h, radius * 2, radius * 2));
        g.setColor(Theme.mix(Theme.C.BORDER.get(), Theme.C.PRIMARY.get(), hv * 0.55));
        g.draw(new RoundRectangle2D.Double(SHADOW + 0.5, y + 0.5, w - 1, h - 1, radius * 2, radius * 2));
        g.dispose();
        super.paintComponent(g0);
    }
}
