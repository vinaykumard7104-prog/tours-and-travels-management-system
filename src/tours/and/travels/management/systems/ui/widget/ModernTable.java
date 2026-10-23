package tours.and.travels.management.systems.ui.widget;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import tours.and.travels.management.systems.ui.Theme;

/** A flat, airy JTable with hover rows, used for bookings and payment history. */
public final class ModernTable {

    public final DefaultTableModel model;
    public final JTable table;
    public final JScrollPane scroll;
    private int hoverRow = -1;

    /** @param alignments one of SwingConstants.LEFT / RIGHT / CENTER per column */
    public ModernTable(String[] columns, int[] alignments) {
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(54);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setOpaque(false);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(Theme.regular(14));
        table.setFocusable(false);
        table.setRowSelectionAllowed(true);

        TableCellRenderer cell = (t, value, sel, focus, row, col) -> {
            JLabel l = new JLabel(value == null ? "" : String.valueOf(value)) {
                @Override public void paintComponent(java.awt.Graphics g) {
                    java.awt.Graphics2D g2 = tours.and.travels.management.systems.ui.Ui.g2(g);
                    if (sel) g2.setColor(Theme.C.PRIMARY.alpha(34));
                    else if (row == hoverRow) g2.setColor(Theme.alpha(Theme.C.TEXT.get(), 12));
                    else g2.setColor(Theme.alpha(Theme.C.TEXT.get(), 0));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Theme.C.BORDER.get());
                    g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            l.setOpaque(false);
            l.setFont(col == 0 ? Theme.bold(14) : Theme.regular(14));
            l.setForeground(col == 0 ? Theme.C.TEXT.get() : Theme.mix(Theme.C.TEXT.get(), Theme.C.MUTED.get(), 0.25));
            l.setHorizontalAlignment(alignments[col]);
            l.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            return l;
        };
        table.setDefaultRenderer(Object.class, cell);

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 46));
        header.setDefaultRenderer((t, value, sel, focus, row, col) -> {
            JLabel l = new JLabel(String.valueOf(value).toUpperCase()) {
                @Override public void paintComponent(java.awt.Graphics g) {
                    java.awt.Graphics2D g2 = tours.and.travels.management.systems.ui.Ui.g2(g);
                    g2.setColor(Theme.C.SURFACE.get());
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Theme.C.BORDER.get());
                    g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            l.setOpaque(false);
            l.setFont(Theme.bold(11.5f));
            l.setForeground(Theme.C.MUTED.get());
            l.setHorizontalAlignment(alignments[col]);
            l.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
            return l;
        });

        table.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r != hoverRow) { hoverRow = r; table.repaint(); }
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hoverRow = -1; table.repaint(); }
        });

        scroll = Scroll.of(table);
    }

    public void clear() { model.setRowCount(0); }

    public int selectedRow() { return table.getSelectedRow(); }

    /** Keeps the compiler happy for callers that only need alignment constants. */
    public static int[] aligned(int... a) { return a; }

    public static final int LEFT = SwingConstants.LEFT, RIGHT = SwingConstants.RIGHT, CENTER = SwingConstants.CENTER;

    public Component component() { return scroll; }
}
