package tours.and.travels.management.systems.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/** Loads bundled images once (soft-cached) and scales them with good quality. */
public final class Images {

    private static final Map<String, SoftReference<BufferedImage>> CACHE = new HashMap<>();

    private Images() { }

    /** @param path classpath-relative, e.g. "images/hotels/hotel1.jpg". Returns null when missing. */
    public static synchronized BufferedImage load(String path) {
        SoftReference<BufferedImage> ref = CACHE.get(path);
        BufferedImage img = ref == null ? null : ref.get();
        if (img != null) return img;
        URL url = Images.class.getClassLoader().getResource(path);
        if (url == null) {
            System.err.println("[Images] resource not found: " + path);
            return null;
        }
        try {
            img = ImageIO.read(url);
            if (img != null) CACHE.put(path, new SoftReference<>(img));
            return img;
        } catch (IOException e) {
            System.err.println("[Images] could not read " + path + ": " + e.getMessage());
            return null;
        }
    }

    /** Scales and centre-crops {@code src} so it exactly fills w x h ("object-fit: cover"). */
    public static BufferedImage cover(BufferedImage src, int w, int h) {
        w = Math.max(1, w);
        h = Math.max(1, h);
        double scale = Math.max(w / (double) src.getWidth(), h / (double) src.getHeight());
        int sw = Math.max(w, (int) Math.ceil(src.getWidth() * scale));
        int sh = Math.max(h, (int) Math.ceil(src.getHeight() * scale));
        BufferedImage scaled = progressive(src, sw, sh);
        int x = (sw - w) / 2, y = (sh - h) / 2;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(scaled, 0, 0, w, h, x, y, x + w, y + h, null);
        g.dispose();
        return out;
    }

    /** Step-down scaling (halving) gives much smoother results than one big bilinear jump. */
    private static BufferedImage progressive(BufferedImage src, int tw, int th) {
        BufferedImage cur = src;
        int cw = src.getWidth(), ch = src.getHeight();
        while (cw / 2 >= tw && ch / 2 >= th) {
            cw /= 2;
            ch /= 2;
            cur = draw(cur, cw, ch);
        }
        return (cw == tw && ch == th) ? cur : draw(cur, tw, th);
    }

    private static BufferedImage draw(BufferedImage src, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }
}
