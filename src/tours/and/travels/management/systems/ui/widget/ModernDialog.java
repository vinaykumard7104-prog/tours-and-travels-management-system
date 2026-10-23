package tours.and.travels.management.systems.ui.widget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;

/** Borderless, rounded, draggable modal dialog with header / body / footer regions. */
public class ModernDialog extends JDialog {

    private static final int SHADOW = 22;

    private final JPanel body = new JPanel();
    private final JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    private final boolean translucent = Ui.translucencySupported();

    public ModernDialog(Window owner, String title, String subtitle, int bodyWidth) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        if (translucent) setBackground(new Color(0, 0, 0, 0));

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Ui.g2(g0);
                int w = getWidth() - 2 * SHADOW, h = getHeight() - 2 * SHADOW;
                if (translucent) {
                    Ui.shadow(g, SHADOW, SHADOW, w, h, 22, SHADOW, 2.2f);
                    g.setColor(Theme.C.SURFACE.get());
                    g.fill(new RoundRectangle2D.Float(SHADOW, SHADOW, w, h, 44, 44));
                    g.setColor(Theme.C.BORDER.get());
                    g.draw(new RoundRectangle2D.Float(SHADOW + .5f, SHADOW + .5f, w - 1, h - 1, 44, 44));
                } else {
                    g.setColor(Theme.C.SURFACE.get());
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Theme.C.BORDER.get());
                    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                }
                g.dispose();
            }
        };
        root.setOpaque(false);
        int inset = translucent ? SHADOW : 1;
        root.setBorder(new EmptyBorder(inset, inset, inset, inset));
        setContentPane(root);

        // header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 28, 6, 18));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(Ui.title(title, 22));
        if (subtitle != null && !subtitle.isEmpty()) {
            titles.add(Ui.vgap(4));
            titles.add(Ui.muted(subtitle, 13.5f));
        }
        header.add(titles, BorderLayout.CENTER);
        IconButton close = new IconButton(Ico.CLOSE, 38);
        close.addActionListener(e -> dispose());
        JPanel closeWrap = new JPanel(new BorderLayout());
        closeWrap.setOpaque(false);
        closeWrap.add(close, BorderLayout.NORTH);
        header.add(closeWrap, BorderLayout.EAST);
        MouseAdapter drag = new MouseAdapter() {
            Point start;
            @Override public void mousePressed(MouseEvent e) { start = e.getPoint(); }
            @Override public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - start.x, p.y + e.getY() - start.y);
            }
        };
        header.addMouseListener(drag);
        header.addMouseMotionListener(drag);
        root.add(header, BorderLayout.NORTH);

        // body
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(14, 28, 8, 28));
        body.setPreferredSize(null);
        JPanel bodyHost = new JPanel(new BorderLayout()) {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(bodyWidth, d.height);
            }
        };
        bodyHost.setOpaque(false);
        bodyHost.add(body, BorderLayout.CENTER);
        root.add(bodyHost, BorderLayout.CENTER);

        // footer
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(8, 28, 24, 18));
        root.add(footer, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().setBorder(BorderFactory.createEmptyBorder());
    }

    public JPanel body() { return body; }

    public JPanel footer() { return footer; }

    public void open() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
