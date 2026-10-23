package tours.and.travels.management.systems.ui.widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.View;
import tours.and.travels.management.systems.ui.Theme;

/** Read-only wrapping paragraph with theme colours; its height follows the width it is given. */
public class WrapText extends JTextArea {

    private Theme.C role;

    public WrapText(String text, Font font, Theme.C role) {
        super(text);
        this.role = role;
        setFont(font);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setBorder(null);
        setMargin(new Insets(0, 0, 0, 0));
    }

    @Override public Color getForeground() { return role == null ? super.getForeground() : role.get(); }

    @Override public Color getDisabledTextColor() { return getForeground(); }

    @Override public Dimension getPreferredSize() {
        int w = getWidth() > 0 ? getWidth() : 320;
        View v = getUI().getRootView(this);
        v.setSize(w, 0);
        return new Dimension(w, (int) Math.ceil(v.getPreferredSpan(View.Y_AXIS)));
    }

    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, getPreferredSize().height); }

    @Override public void setBounds(int x, int y, int w, int h) {
        boolean widthChanged = w != getWidth();
        super.setBounds(x, y, w, h);
        if (widthChanged) SwingUtilities.invokeLater(this::revalidate);
    }
}
