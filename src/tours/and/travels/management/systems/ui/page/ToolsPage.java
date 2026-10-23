package tours.and.travels.management.systems.ui.page;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.screen.MainFrame;
import tours.and.travels.management.systems.ui.widget.Card;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.Scroll;
import tours.and.travels.management.systems.ui.widget.Stepper;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.ui.widget.VerticalPanel;
import tours.and.travels.management.systems.util.Money;

/** Trip notes (auto-saved), system calculator launcher and a bill splitter. */
public class ToolsPage extends Page {

    private final VerticalPanel content = body();
    private final JTextArea notes = new JTextArea() {
        @Override protected void paintComponent(java.awt.Graphics g) {
            setForeground(Theme.C.TEXT.get());
            setCaretColor(Theme.C.PRIMARY.get());
            setSelectionColor(Theme.C.PRIMARY.alpha(90));
            super.paintComponent(g);
        }
    };
    private final ThemedLabel saved = Ui.muted(" ", 12.5f);
    private final Field bill = new Field("Total bill", Ico.WALLET, false);
    private final Stepper people = new Stepper(4, 1, 50, "person");
    private final ThemedLabel each = Ui.label("\u20B90", 30, true, Theme.C.PRIMARY);
    private Timer saveTimer;
    private boolean loading;

    public ToolsPage(MainFrame app) {
        super(app);
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1;
        gc.gridx = 0; gc.weightx = 1;
        row.add(notesCard(), gc);
        JPanel col = fixedWidth(400);
        col.add(calcCard());
        col.add(Ui.vgap(6));
        col.add(splitCard());
        gc.gridx = 1; gc.weightx = 0;
        row.add(col, gc);
        content.add(row);
        add(Scroll.page(content), BorderLayout.CENTER);
    }

    private Path notesFile() {
        return Paths.get(System.getProperty("user.home"), ".tours-travels", "notes-" + app.account().username() + ".txt");
    }

    private Card notesCard() {
        Card c = new Card(new BorderLayout(0, 12), 22).noLift();
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(left(Ui.title("Trip notes", 20)));
        t.add(left(Ui.muted("Packing lists, flight numbers, ideas \u2013 saved automatically.", 13.5f)));
        head.add(t, BorderLayout.WEST);
        head.add(saved, BorderLayout.EAST);
        c.add(head, BorderLayout.NORTH);

        notes.setOpaque(false);
        notes.setFont(Theme.regular(15));
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        JPanel box = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(java.awt.Graphics g0) {
                java.awt.Graphics2D g = Ui.g2(g0);
                g.setColor(Theme.C.INPUT.get());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
                g.setColor(Theme.C.BORDER.get());
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 28, 28);
                g.dispose();
            }
        };
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        javax.swing.JScrollPane sp = Scroll.of(notes);
        box.add(sp, BorderLayout.CENTER);
        box.setPreferredSize(new java.awt.Dimension(300, 360));
        c.add(box, BorderLayout.CENTER);

        notes.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void changedUpdate(DocumentEvent e) { }
        });
        return c;
    }

    private void changed() {
        if (loading) return;
        saved.setText("Saving\u2026");
        if (saveTimer != null) saveTimer.stop();
        saveTimer = new Timer(700, e -> saveNotes());
        saveTimer.setRepeats(false);
        saveTimer.start();
    }

    private void saveNotes() {
        try {
            Path f = notesFile();
            Files.createDirectories(f.getParent());
            Files.writeString(f, notes.getText(), StandardCharsets.UTF_8);
            saved.setText("Saved \u2713");
        } catch (IOException ex) {
            saved.setText("Could not save");
        }
    }

    private Card calcCard() {
        Card c = new Card(new BorderLayout(14, 0), 20).noLift();
        JPanel t = new JPanel();
        t.setOpaque(false);
        t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
        t.add(left(Ui.title("Calculator", 18)));
        t.add(left(Ui.muted("Opens your system calculator.", 13)));
        c.add(t, BorderLayout.CENTER);
        PillButton open = new PillButton("Open", PillButton.Style.SECONDARY).icon(Ico.CALC).compact();
        open.addActionListener(e -> launchCalculator());
        c.add(open, BorderLayout.EAST);
        return c;
    }

    private void launchCalculator() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String[][] attempts = os.contains("win") ? new String[][]{{"calc.exe"}}
                : os.contains("mac") ? new String[][]{{"open", "-a", "Calculator"}}
                : new String[][]{{"gnome-calculator"}, {"kcalc"}, {"xcalc"}};
        for (String[] cmd : attempts) {
            try { new ProcessBuilder(cmd).start(); return; } catch (IOException ignored) { }
        }
        Toast.error(this, "Couldn\u2019t open a calculator on this system.");
    }

    private Card splitCard() {
        Card c = new Card(new BorderLayout(), 22).noLift();
        JPanel f = new JPanel();
        f.setOpaque(false);
        f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
        f.add(left(Ui.title("Split the bill", 18)));
        f.add(left(Ui.muted("Share costs evenly with your travel group.", 13)));
        f.add(Ui.vgap(14));
        f.add(Forms.labeled("Total amount (\u20B9)", bill));
        f.add(Ui.vgap(12));
        f.add(Forms.labeled("Split between", people));
        f.add(Ui.vgap(16));
        f.add(left(Ui.muted("Each person pays", 13)));
        f.add(left(each));
        c.add(f, BorderLayout.CENTER);
        Runnable calc = () -> each.setText(Money.format(Math.round(Money.parse(bill.getText()) / (double) people.getValue())));
        bill.getInput().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calc.run(); }
            public void removeUpdate(DocumentEvent e) { calc.run(); }
            public void changedUpdate(DocumentEvent e) { }
        });
        people.onChange(calc);
        return c;
    }

    @Override public String title() { return "Tools"; }

    @Override public String subtitle() { return "Small helpers for your trips."; }

    @Override public void onShow() {
        loading = true;
        try {
            Path f = notesFile();
            notes.setText(Files.isRegularFile(f) ? Files.readString(f, StandardCharsets.UTF_8) : "");
        } catch (IOException e) {
            notes.setText("");
        }
        loading = false;
        saved.setText(" ");
    }

    @Override public void onHide() {
        if (saveTimer != null && saveTimer.isRunning()) { saveTimer.stop(); saveNotes(); }
    }
}
