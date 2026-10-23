package tours.and.travels.management.systems.ui.widget;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.util.Images;

/** Rounded "cover-fit" picture with optional shading and zoom; loads its image off the UI thread. */
public class ImagePanel extends JComponent {

    private BufferedImage src, scaled, prevScaled;
    private int sw = -1, sh = -1;
    private final int radius;
    private double zoom = 1;
    private float bottomShade, sideShade;
    private float fade = 1f;
    private Color tint;
    private String loading;

    public ImagePanel(int radius) {
        this.radius = radius;
        setOpaque(false);
        setPreferredSize(new Dimension(200, 140));
    }

    public ImagePanel bottomShade(float a) { this.bottomShade = a; return this; }

    public ImagePanel sideShade(float a) { this.sideShade = a; return this; }

    public ImagePanel tint(Color c) { this.tint = c; return this; }

    public void setZoom(double z) { this.zoom = z; repaint(); }

    public double getZoom() { return zoom; }

    public String getResource() { return loading; }

    /** Loads "images/…" from the classpath in the background and fades it in. */
    public void setResource(String path) {
        if (path != null && path.equals(loading)) return;
        loading = path;
        new SwingWorker<BufferedImage, Void>() {
            @Override protected BufferedImage doInBackground() { return Images.load(path); }
            @Override protected void done() {
                try {
                    if (!path.equals(loading)) return;
                    setImage(get(), true);
                } catch (Exception ignored) { }
            }
        }.execute();
    }

    public void setImage(BufferedImage img, boolean animate) {
        this.prevScaled = (animate && scaled != null) ? scaled : null;   // keep old picture for the crossfade
        this.src = img;
        this.scaled = null;
        if (animate && img != null) Anim.run(520, p -> { fade = (float) p; repaint(); }, () -> prevScaled = null);
        else { fade = 1f; repaint(); }
    }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) { g.dispose(); return; }
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, radius * 2f, radius * 2f);
        g.setPaint(new GradientPaint(0, 0, Theme.C.PRIMARY.alpha(70), w, h, Theme.C.PRIMARY2.alpha(60)));
        g.fill(shape);
        if (src != null) {
            if (scaled == null || sw != w || sh != h) {
                scaled = Images.cover(src, w, h);
                sw = w;
                sh = h;
            }
            double zw = w * zoom, zh = h * zoom;
            Rectangle2D anchor = new Rectangle2D.Double((w - zw) / 2, (h - zh) / 2, zw, zh);
            if (fade < 1f && prevScaled != null && prevScaled.getWidth() == w && prevScaled.getHeight() == h) {
                g.setPaint(new TexturePaint(prevScaled, anchor));
                g.fill(shape);
            }
            if (fade < 1f) g.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, fade)));
            g.setPaint(new TexturePaint(scaled, anchor));
            g.fill(shape);
            g.setComposite(AlphaComposite.SrcOver);
        }
        if (tint != null) {
            g.setColor(tint);
            g.fill(shape);
        }
        if (bottomShade > 0) {
            g.setPaint(new GradientPaint(0, h * 0.35f, new Color(5, 10, 25, 0), 0, h, new Color(5, 10, 25, (int) (255 * bottomShade))));
            g.fill(shape);
        }
        if (sideShade > 0) {
            g.setPaint(new GradientPaint(0, 0, new Color(5, 12, 35, (int) (255 * sideShade)), w * 0.8f, 0, new Color(5, 12, 35, 0)));
            g.fill(shape);
        }
        g.dispose();
    }

}
