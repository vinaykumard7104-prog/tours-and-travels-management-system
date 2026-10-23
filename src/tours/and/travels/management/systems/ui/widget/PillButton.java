package tours.and.travels.management.systems.ui.widget;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Fully custom-painted pill button with hover/press animation. */
public class PillButton extends JButton {

    public enum Style { PRIMARY, SECONDARY, GHOST, DANGER, DANGER_SOFT, ACCENT, GLASS }

    private final Style style;
    private Ico icon;
    private boolean trailing;
    private int height = 44;
    private boolean stretch;
    private final Hover hover = new Hover(this);

    public PillButton(String text, Style style) {
        super(text);
        this.style = style;
        setFont(Theme.bold(14));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public PillButton icon(Ico i) { this.icon = i; this.trailing = false; return this; }

    public PillButton trailing(Ico i) { this.icon = i; this.trailing = true; return this; }

    public PillButton compact() { this.height = 36; setFont(Theme.bold(13)); return this; }

    public PillButton stretch() { this.stretch = true; return this; }

    @Override public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int pad = height <= 36 ? 18 : 26;
        int w = fm.stringWidth(getText()) + 2 * pad + (icon != null ? (getText().isEmpty() ? 0 : 26) : 0);
        if (icon != null && getText().isEmpty()) w = height;
        return new Dimension(w, height);
    }

    @Override public Dimension getMinimumSize() { return getPreferredSize(); }

    @Override public Dimension getMaximumSize() {
        Dimension d = getPreferredSize();
        return stretch ? new Dimension(Integer.MAX_VALUE, d.height) : d;
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int w = getWidth(), h = getHeight();
        double hv = hover.get();
        boolean press = getModel().isPressed() && isEnabled();
        if (!isEnabled()) g.setComposite(AlphaComposite.SrcOver.derive(0.45f));
        if (press) g.translate(0, 1);
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h - 1, h, h);
        Color fg;
        Color white = Color.WHITE;
        switch (style) {
            case PRIMARY -> {
                Color a = Theme.mix(Theme.C.PRIMARY.get(), white, 0.10 * hv + (press ? -0.08 : 0));
                Color b = Theme.mix(Theme.C.PRIMARY2.get(), white, 0.10 * hv);
                g.setPaint(new GradientPaint(0, 0, a, w, h, b));
                g.fill(shape);
                fg = white;
            }
            case ACCENT -> {
                g.setPaint(new GradientPaint(0, 0, Theme.mix(Theme.C.ACCENT.get(), white, 0.12 * hv),
                        w, h, Theme.mix(new Color(0xF97316), white, 0.12 * hv)));
                g.fill(shape);
                fg = new Color(0x2A1600);
            }
            case SECONDARY -> {
                g.setColor(Theme.mix(Theme.C.SURFACE_ALT.get(), Theme.C.BORDER.get(), 0.55 * hv));
                g.fill(shape);
                g.setColor(Theme.C.BORDER.get());
                g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 2, h, h));
                fg = Theme.C.TEXT.get();
            }
            case GHOST -> {
                g.setColor(Theme.alpha(Theme.C.TEXT.get(), (int) (26 * hv)));
                g.fill(shape);
                fg = Theme.mix(Theme.C.MUTED.get(), Theme.C.TEXT.get(), hv);
            }
            case DANGER -> {
                g.setColor(Theme.mix(Theme.C.DANGER.get(), white, 0.12 * hv));
                g.fill(shape);
                fg = white;
            }
            case DANGER_SOFT -> {
                g.setColor(Theme.alpha(Theme.C.DANGER.get(), 30 + (int) (34 * hv)));
                g.fill(shape);
                fg = Theme.C.DANGER.get();
            }
            default -> {   // GLASS
                g.setColor(new Color(255, 255, 255, 40 + (int) (44 * hv)));
                g.fill(shape);
                g.setColor(new Color(255, 255, 255, 95));
                g.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 2, h, h));
                fg = white;
            }
        }
        FontMetrics fm = g.getFontMetrics(getFont());
        String text = getText();
        int iw = icon != null ? 18 : 0;
        int gap = icon != null && !text.isEmpty() ? 8 : 0;
        int total = fm.stringWidth(text) + iw + gap;
        int x = (w - total) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() - 1;
        g.setFont(getFont());
        if (icon != null && !trailing) {
            icon.paint(g, x, (h - 18) / 2.0 - 0.5, 18, fg, 2.1);
            x += iw + gap;
        }
        g.setColor(fg);
        g.drawString(text, x, ty);
        if (icon != null && trailing) {
            icon.paint(g, x + fm.stringWidth(text) + gap, (h - 18) / 2.0 - 0.5, 18, fg, 2.1);
        }
        g.dispose();
    }
}
