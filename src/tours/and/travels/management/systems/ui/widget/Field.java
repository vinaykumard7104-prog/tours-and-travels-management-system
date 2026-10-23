package tours.and.travels.management.systems.ui.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import tours.and.travels.management.systems.ui.Hover;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Rounded text / password input with leading icon, placeholder, focus glow and error state. */
public class Field extends JPanel {

    private final JTextField input;
    private final Ico icon;
    private final String placeholder;
    private boolean focused, error;
    private final Hover hover = new Hover(this);
    private IconButton eye;

    public Field(String placeholder, Ico icon, boolean password) {
        super(new BorderLayout());
        this.icon = icon;
        this.placeholder = placeholder;
        setOpaque(false);
        input = password ? new JPasswordField() {
            @Override protected void paintComponent(Graphics g) { styleAndPaint(this, g, () -> super.paintComponent(g)); }
        } : new JTextField() {
            @Override protected void paintComponent(Graphics g) { styleAndPaint(this, g, () -> super.paintComponent(g)); }
        };
        input.setOpaque(false);
        input.setBorder(null);
        input.setFont(Theme.regular(15));
        add(input, BorderLayout.CENTER);
        setBorder(new EmptyBorder(0, icon != null ? 46 : 16, 0, password ? 6 : 16));
        input.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
        });
        input.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { clearError(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { clearError(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });
        if (password) {
            JPasswordField pf = (JPasswordField) input;
            char echo = pf.getEchoChar();
            eye = new IconButton(Ico.EYE, 36);
            eye.addActionListener(e -> {
                boolean hidden = pf.getEchoChar() != 0;
                pf.setEchoChar(hidden ? (char) 0 : echo);
                eye.setIco(hidden ? Ico.EYE_OFF : Ico.EYE);
            });
            add(eye, BorderLayout.EAST);
        }
    }

    private void styleAndPaint(JTextComponent c, Graphics g, Runnable paint) {
        c.setForeground(Theme.C.TEXT.get());
        c.setCaretColor(Theme.C.PRIMARY.get());
        c.setSelectionColor(Theme.C.PRIMARY.alpha(90));
        c.setSelectedTextColor(Theme.C.TEXT.get());
        paint.run();
        if (c.getDocument().getLength() == 0 && placeholder != null) {
            Graphics2D g2 = Ui.g2(g);
            g2.setFont(c.getFont());
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(Theme.alpha(Theme.C.MUTED.get(), 190));
            g2.drawString(placeholder, 0, (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    public String getText() {
        return input instanceof JPasswordField pf ? new String(pf.getPassword()) : input.getText();
    }

    public void setText(String t) { input.setText(t == null ? "" : t); }

    public void setError(boolean e) { this.error = e; repaint(); }

    private void clearError() { if (error) setError(false); }

    public void setEditable(boolean e) { input.setEditable(e); }

    public void onEnter(ActionListener l) { input.addActionListener(l); }

    @Override public void requestFocus() { input.requestFocusInWindow(); }

    @Override public boolean requestFocusInWindow() { return input.requestFocusInWindow(); }

    public JTextField getInput() { return input; }

    @Override public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 48); }

    @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 48); }

    @Override public Dimension getMinimumSize() { return new Dimension(80, 48); }

    @Override protected void paintComponent(Graphics g0) {
        Graphics2D g = Ui.g2(g0);
        InputStyle.paintBox(g, getWidth(), getHeight(), focused, error, hover.get());
        if (icon != null) {
            Color c = error ? Theme.C.DANGER.get() : (focused ? Theme.C.PRIMARY.get() : Theme.C.MUTED.get());
            icon.paint(g, 16, (getHeight() - 20) / 2.0, 20, c, 1.8);
        }
        g.dispose();
    }
}
