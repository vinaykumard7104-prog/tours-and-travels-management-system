package tours.and.travels.management.systems.ui.screen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.widget.IconButton;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;

/** Page title, date and light/dark switch. */
final class TopBar extends JPanel {

    private final ThemedLabel title = Ui.title("", 28);
    private final ThemedLabel subtitle = Ui.muted("", 14);

    TopBar(MainFrame app) {
        super(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(26, 40, 8, 36));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(Ui.vgap(3));
        left.add(subtitle);
        add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        right.setOpaque(false);
        right.add(Ui.label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")), 13.5f, true, Theme.C.MUTED));
        IconButton mode = new IconButton(Theme.isDark() ? Ico.SUN : Ico.MOON, 42).filled();
        mode.setToolTipText("Switch light / dark mode");
        mode.addActionListener(e -> {
            Theme.toggle();
            mode.setIco(Theme.isDark() ? Ico.SUN : Ico.MOON);
            app.repaint();
        });
        right.add(mode);
        add(right, BorderLayout.EAST);
    }

    void set(String t, String s) {
        title.setText(t);
        subtitle.setText(s);
    }
}
