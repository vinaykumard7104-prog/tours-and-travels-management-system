package tours.and.travels.management.systems.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/** Crisp vector line-icons drawn with Java2D on a 24x24 grid (no image files needed). */
public enum Ico {
    HOME, SUITCASE, HOTEL, GLOBE, TICKET, WALLET, USER, USERS, GRID, LOGOUT, SUN, MOON, PLANE, PIN, CHECK, CLOSE,
    PLUS, MINUS, EYE, EYE_OFF, CALENDAR, STAR, TRASH, LOCK, MAIL, PHONE, ARROW_RIGHT, NOTE, CHEVRON_DOWN, CARD,
    BANK, CLOCK, CALC, SHIELD, BED, ID, SEARCH;

    public void paint(Graphics2D g0, double x, double y, double size, Color color) {
        paint(g0, x, y, size, color, 1.9);
    }

    public void paint(Graphics2D g0, double x, double y, double size, Color color, double stroke) {
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.translate(x, y);
        double s = size / 24.0;
        g.scale(s, s);
        g.setColor(color);
        g.setStroke(new BasicStroke((float) stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        draw(g);
        g.dispose();
    }

    // ----------------------------------------------------------------------------- shapes
    private static void line(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void poly(Graphics2D g, boolean close, double... p) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(p[0], p[1]);
        for (int i = 2; i < p.length; i += 2) path.lineTo(p[i], p[i + 1]);
        if (close) path.closePath();
        g.draw(path);
    }

    private static void rr(Graphics2D g, double x, double y, double w, double h, double r) {
        g.draw(new RoundRectangle2D.Double(x, y, w, h, r * 2, r * 2));
    }

    private static void circ(Graphics2D g, double cx, double cy, double r) {
        g.draw(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }

    private static void dot(Graphics2D g, double cx, double cy, double r) {
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }

    private void draw(Graphics2D g) {
        switch (this) {
            case HOME -> {
                poly(g, false, 3, 11, 12, 3, 21, 11);
                poly(g, false, 5, 9.5, 5, 21, 19, 21, 19, 9.5);
                poly(g, false, 10, 21, 10, 14, 14, 14, 14, 21);
            }
            case SUITCASE -> {
                rr(g, 3, 7, 18, 13, 3);
                poly(g, false, 9, 7, 9, 4.5, 15, 4.5, 15, 7);
                line(g, 3, 13, 21, 13);
                line(g, 11, 13, 13, 13);
            }
            case HOTEL -> {
                rr(g, 5, 3, 14, 18, 1.5);
                line(g, 9, 7.5, 10, 7.5); line(g, 14, 7.5, 15, 7.5);
                line(g, 9, 11.5, 10, 11.5); line(g, 14, 11.5, 15, 11.5);
                poly(g, false, 10, 21, 10, 16, 14, 16, 14, 21);
            }
            case GLOBE -> {
                circ(g, 12, 12, 9);
                g.draw(new Ellipse2D.Double(8, 3, 8, 18));
                line(g, 3, 12, 21, 12);
            }
            case TICKET -> {
                rr(g, 3, 6, 18, 12, 2.5);
                g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                        new float[]{0.1f, 3.2f}, 0f));
                line(g, 15, 7.5, 15, 16.5);
            }
            case WALLET -> {
                rr(g, 3, 5.5, 18, 14.5, 3);
                line(g, 3, 9.5, 21, 9.5);
                dot(g, 16.5, 14.5, 1.3);
            }
            case USER -> {
                circ(g, 12, 8, 4);
                Path2D.Double p = new Path2D.Double();
                p.moveTo(4.5, 21); p.curveTo(4.5, 16.5, 8, 14.5, 12, 14.5); p.curveTo(16, 14.5, 19.5, 16.5, 19.5, 21);
                g.draw(p);
            }
            case USERS -> {
                circ(g, 9, 8, 3.5);
                Path2D.Double p = new Path2D.Double();
                p.moveTo(2.5, 20); p.curveTo(2.5, 16.5, 5.5, 14.5, 9, 14.5); p.curveTo(12.5, 14.5, 15.5, 16.5, 15.5, 20);
                g.draw(p);
                circ(g, 17, 9, 2.7);
                Path2D.Double q = new Path2D.Double();
                q.moveTo(17.2, 14.5); q.curveTo(19.8, 14.7, 21.5, 16.3, 21.5, 19.5);
                g.draw(q);
            }
            case GRID -> {
                rr(g, 4, 4, 6.5, 6.5, 1.8); rr(g, 13.5, 4, 6.5, 6.5, 1.8);
                rr(g, 4, 13.5, 6.5, 6.5, 1.8); rr(g, 13.5, 13.5, 6.5, 6.5, 1.8);
            }
            case LOGOUT -> {
                poly(g, false, 9, 3, 4.5, 3, 4.5, 21, 9, 21);
                poly(g, false, 16, 7, 21, 12, 16, 17);
                line(g, 21, 12, 9, 12);
            }
            case SUN -> {
                circ(g, 12, 12, 4.2);
                for (int i = 0; i < 8; i++) {
                    double a = Math.PI / 4 * i;
                    line(g, 12 + Math.cos(a) * 7.6, 12 + Math.sin(a) * 7.6, 12 + Math.cos(a) * 10, 12 + Math.sin(a) * 10);
                }
            }
            case MOON -> {
                Area a = new Area(new Ellipse2D.Double(3, 3, 18, 18));
                a.subtract(new Area(new Ellipse2D.Double(8.5, -0.5, 16, 16)));
                g.fill(a);
            }
            case PLANE -> {
                poly(g, true, 21, 3, 3, 10.5, 10, 13.5, 13.5, 21);
                line(g, 21, 3, 10, 13.5);
            }
            case PIN -> {
                Path2D.Double p = new Path2D.Double();
                p.moveTo(12, 21.5); p.curveTo(7, 16, 5, 12.5, 5, 9.5); p.curveTo(5, 5.6, 8.1, 2.5, 12, 2.5);
                p.curveTo(15.9, 2.5, 19, 5.6, 19, 9.5); p.curveTo(19, 12.5, 17, 16, 12, 21.5);
                g.draw(p);
                circ(g, 12, 9.5, 2.7);
            }
            case CHECK -> poly(g, false, 4.5, 12.5, 9.5, 17.5, 19.5, 6.5);
            case CLOSE -> { line(g, 6, 6, 18, 18); line(g, 18, 6, 6, 18); }
            case PLUS -> { line(g, 12, 5, 12, 19); line(g, 5, 12, 19, 12); }
            case MINUS -> line(g, 5, 12, 19, 12);
            case EYE, EYE_OFF -> {
                Path2D.Double p = new Path2D.Double();
                p.moveTo(2, 12); p.curveTo(5, 6.5, 9, 5.5, 12, 5.5); p.curveTo(15, 5.5, 19, 6.5, 22, 12);
                p.curveTo(19, 17.5, 15, 18.5, 12, 18.5); p.curveTo(9, 18.5, 5, 17.5, 2, 12);
                g.draw(p);
                circ(g, 12, 12, 3);
                if (this == EYE_OFF) line(g, 4, 3.5, 20, 20.5);
            }
            case CALENDAR -> {
                rr(g, 3, 5, 18, 16, 3); line(g, 3, 10, 21, 10); line(g, 8, 3, 8, 7); line(g, 16, 3, 16, 7);
            }
            case STAR -> {
                Path2D.Double p = new Path2D.Double();
                for (int i = 0; i < 10; i++) {
                    double r = (i % 2 == 0) ? 10 : 4.3;
                    double a = -Math.PI / 2 + Math.PI / 5 * i;
                    double px = 12 + Math.cos(a) * r, py = 12.6 + Math.sin(a) * r;
                    if (i == 0) p.moveTo(px, py); else p.lineTo(px, py);
                }
                p.closePath();
                g.fill(p);
            }
            case TRASH -> {
                line(g, 4, 7, 20, 7);
                poly(g, false, 9, 7, 9, 4, 15, 4, 15, 7);
                poly(g, false, 6, 7, 7, 20, 17, 20, 18, 7);
                line(g, 10, 11, 10, 16); line(g, 14, 11, 14, 16);
            }
            case LOCK -> {
                rr(g, 5, 11, 14, 10, 2.5);
                Path2D.Double p = new Path2D.Double();
                p.moveTo(8, 11); p.lineTo(8, 8); p.curveTo(8, 3, 16, 3, 16, 8); p.lineTo(16, 11);
                g.draw(p);
            }
            case MAIL -> { rr(g, 3, 5, 18, 14, 2.5); poly(g, false, 3.5, 7.5, 12, 13.5, 20.5, 7.5); }
            case PHONE -> { rr(g, 7, 2.5, 10, 19, 2.5); line(g, 11, 18.5, 13, 18.5); }
            case ARROW_RIGHT -> { line(g, 5, 12, 19, 12); poly(g, false, 13, 6, 19, 12, 13, 18); }
            case NOTE -> {
                rr(g, 5, 3, 14, 18, 2.5); line(g, 9, 8, 15, 8); line(g, 9, 12, 15, 12); line(g, 9, 16, 12, 16);
            }
            case CHEVRON_DOWN -> poly(g, false, 6, 9.5, 12, 15.5, 18, 9.5);
            case CARD -> { rr(g, 2, 5, 20, 14, 3); line(g, 2, 10, 22, 10); line(g, 6, 15, 10, 15); }
            case BANK -> {
                poly(g, true, 3, 10, 12, 4, 21, 10);
                line(g, 6, 13, 6, 18); line(g, 10, 13, 10, 18); line(g, 14, 13, 14, 18); line(g, 18, 13, 18, 18);
                line(g, 3, 21, 21, 21);
            }
            case CLOCK -> { circ(g, 12, 12, 9); poly(g, false, 12, 7, 12, 12, 15.5, 14); }
            case CALC -> {
                rr(g, 5, 2.5, 14, 19, 2.5); rr(g, 8, 6, 8, 4, 1);
                for (int r = 0; r < 2; r++) for (int c = 0; c < 3; c++) dot(g, 9 + c * 3, 14 + r * 4, 0.9);
            }
            case SHIELD -> {
                Path2D.Double p = new Path2D.Double();
                p.moveTo(12, 3); p.lineTo(20, 6); p.lineTo(20, 12); p.curveTo(20, 17, 16, 20, 12, 21.5);
                p.curveTo(8, 20, 4, 17, 4, 12); p.lineTo(4, 6); p.closePath();
                g.draw(p);
                poly(g, false, 8.5, 12, 11, 14.5, 15.5, 9.5);
            }
            case BED -> {
                line(g, 3, 5, 3, 19); line(g, 3, 15, 21, 15); line(g, 21, 15, 21, 19);
                rr(g, 6, 9, 6, 4, 1.2);
                poly(g, false, 12, 11, 19, 11, 21, 15);
            }
            case SEARCH -> { circ(g, 10.5, 10.5, 6.5); line(g, 15.5, 15.5, 21, 21); }
            case ID -> {
                rr(g, 2.5, 5, 19, 14, 2.5); circ(g, 8.5, 11, 2); line(g, 5.5, 16, 11.5, 16);
                line(g, 14, 10, 19, 10); line(g, 14, 13.5, 18, 13.5);
            }
        }
    }
}
