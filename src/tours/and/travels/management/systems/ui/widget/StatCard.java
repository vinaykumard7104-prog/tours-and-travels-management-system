package tours.and.travels.management.systems.ui.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.util.Money;

/** KPI tile: gradient icon badge, caption and an animated count-up value. */
public class StatCard extends Card {

    private final ThemedLabel value;
    private final boolean money;
    private int shown;

    public StatCard(Ico icon, String caption, boolean money, Color from, Color to) {
        super(new BorderLayout(14, 0), 18);
        this.money = money;
        JComponent badge = new JComponent() {
            @Override public Dimension getPreferredSize() { return new Dimension(52, 52); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Ui.g2(g0);
                g.setPaint(new GradientPaint(0, 0, from, 52, 52, to));
                g.fill(new RoundRectangle2D.Float(0, 0, 52, 52, 30, 30));
                icon.paint(g, 14, 14, 24, Color.WHITE, 2.1);
                g.dispose();
            }
        };
        JPanel badgeWrap = new JPanel(new BorderLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badge, BorderLayout.NORTH);
        add(badgeWrap, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(Ui.muted(caption, 13));
        text.add(Ui.vgap(2));
        value = Ui.title(money ? Money.format(0) : "0", 26);
        text.add(value);
        add(text, BorderLayout.CENTER);
        arm();
    }

    public void setValue(int target) {
        final int from = shown;
        Anim.run(750, p -> {
            shown = (int) Math.round(from + (target - from) * p);
            value.setText(money ? Money.format(shown) : String.valueOf(shown));
        }, () -> {
            shown = target;
            value.setText(money ? Money.format(target) : String.valueOf(target));
        });
    }
}
