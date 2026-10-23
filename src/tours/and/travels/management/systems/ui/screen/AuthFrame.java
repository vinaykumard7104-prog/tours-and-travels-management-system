package tours.and.travels.management.systems.ui.screen;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import tours.and.travels.management.systems.Main;
import tours.and.travels.management.systems.dao.AccountDao;
import tours.and.travels.management.systems.dao.DaoException;
import tours.and.travels.management.systems.db.Database;
import tours.and.travels.management.systems.model.Models.Account;
import tours.and.travels.management.systems.ui.Anim;
import tours.and.travels.management.systems.ui.Ico;
import tours.and.travels.management.systems.ui.Theme;
import tours.and.travels.management.systems.ui.Ui;
import tours.and.travels.management.systems.ui.widget.Combo;
import tours.and.travels.management.systems.ui.widget.Field;
import tours.and.travels.management.systems.ui.widget.Forms;
import tours.and.travels.management.systems.ui.widget.IconButton;
import tours.and.travels.management.systems.ui.widget.ImagePanel;
import tours.and.travels.management.systems.ui.widget.Logo;
import tours.and.travels.management.systems.ui.widget.PillButton;
import tours.and.travels.management.systems.ui.widget.ThemedLabel;
import tours.and.travels.management.systems.ui.widget.ThemedPanel;
import tours.and.travels.management.systems.ui.widget.Toast;
import tours.and.travels.management.systems.util.Async;
import tours.and.travels.management.systems.util.Session;
import tours.and.travels.management.systems.util.Validate;

/** Sign in / create account / reset password, on one split-screen window. */
public class AuthFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final ShakePanel stack = new ShakePanel(cards);

    // sign in
    private final Field inUser = new Field("Username", Ico.USER, false);
    private final Field inPass = new Field("Password", Ico.LOCK, true);
    private final ThemedLabel inError = errorLabel();
    private PillButton inBtn;

    // sign up
    private final Field upName = new Field("Full name", Ico.ID, false);
    private final Field upUser = new Field("Choose a username", Ico.USER, false);
    private final Field upPass = new Field("At least 6 characters", Ico.LOCK, true);
    private final Field upPass2 = new Field("Repeat password", Ico.LOCK, true);
    private final Combo upQuestion = new Combo("Your favourite superhero", "Name of your first pet",
            "Your lucky number", "City you were born in", "Your favourite movie").icon(Ico.SHIELD);
    private final Field upAnswer = new Field("Your answer", Ico.CHECK, false);
    private final ThemedLabel upError = errorLabel();
    private PillButton upBtn;

    // reset
    private final Field rsUser = new Field("Your username", Ico.USER, false);
    private final ThemedLabel rsQuestion = Ui.label(" ", 14, true, Theme.C.PRIMARY);
    private final Field rsAnswer = new Field("Your answer", Ico.SHIELD, false);
    private final Field rsPass = new Field("New password", Ico.LOCK, true);
    private final ThemedLabel rsError = errorLabel();
    private JPanel rsStep2;
    private PillButton rsFind, rsBtn;

    private static final class ShakePanel extends JPanel {
        private int dx;
        ShakePanel(CardLayout l) { super(l); setOpaque(false); }
        void shake() {
            Anim.run(420, p -> { dx = (int) (Math.sin(p * Math.PI * 7) * 11 * (1 - p)); repaint(); }, () -> { dx = 0; repaint(); });
        }
        @Override public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(dx, 0);
            super.paint(g2);
            g2.dispose();
        }
    }

    private static ThemedLabel errorLabel() { return Ui.label(" ", 13, true, Theme.C.DANGER); }

    public AuthFrame() {
        super("Tours & Travels \u2013 Sign in");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1120, 720);
        setLocationRelativeTo(null);

        ThemedPanel root = new ThemedPanel(new BorderLayout(), Theme.C.BG);
        root.setOpaque(true);
        setContentPane(root);
        root.add(buildHero(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 18));
        top.setOpaque(false);
        IconButton mode = new IconButton(Theme.isDark() ? Ico.SUN : Ico.MOON, 42).filled();
        mode.addActionListener(e -> { Theme.toggle(); mode.setIco(Theme.isDark() ? Ico.SUN : Ico.MOON); repaint(); });
        top.add(mode);
        right.add(top, BorderLayout.NORTH);

        stack.add(signInForm(), "in");
        stack.add(signUpForm(), "up");
        stack.add(resetForm(), "reset");
        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.CENTER;
        stack.setPreferredSize(new Dimension(410, 560));
        centre.add(stack, gc);
        right.add(centre, BorderLayout.CENTER);
        root.add(right, BorderLayout.CENTER);

        String last = Main.prefs().get("lastUser", "");
        inUser.setText(last);
        SwingUtilities.invokeLater(() -> (last.isEmpty() ? inUser : inPass).requestFocusInWindow());
        getRootPane().setDefaultButton(inBtn);
    }

    // ----------------------------------------------------------------------------------- hero
    private javax.swing.JComponent buildHero() {
        ImagePanel hero = new ImagePanel(0).bottomShade(0.8f).tint(new Color(10, 22, 60, 90));
        hero.setResource("images/hero/home.jpg");
        hero.setPreferredSize(new Dimension(520, 100));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(40, 44, 44, 44));
        hero.add(new Logo(46, true, Color.WHITE, "Tours & Travels", "Management System"), BorderLayout.NORTH);

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.add(white("Discover the world,", 38, true));
        txt.add(white("one booking at a time.", 38, true));
        txt.add(Ui.vgap(16));
        txt.add(white("Packages, hotels and payments \u2013 all in one elegant place.", 15, false));
        txt.add(Ui.vgap(26));
        txt.add(bullet(Ico.SUITCASE, "Curated tour packages"));
        txt.add(Ui.vgap(10));
        txt.add(bullet(Ico.HOTEL, "Hotels to suit every budget"));
        txt.add(Ui.vgap(10));
        txt.add(bullet(Ico.SHIELD, "Your data stays secure"));
        hero.add(txt, BorderLayout.SOUTH);
        return hero;
    }

    private static ThemedLabel white(String s, float size, boolean bold) {
        ThemedLabel l = Ui.label(s, size, bold, null).color(bold ? Color.WHITE : new Color(255, 255, 255, 205));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JPanel bullet(Ico ico, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        javax.swing.JComponent icon = new javax.swing.JComponent() {
            @Override public Dimension getPreferredSize() { return new Dimension(36, 36); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Ui.g2(g0);
                g.setColor(new Color(255, 255, 255, 38));
                g.fillOval(0, 0, 36, 36);
                ico.paint(g, 9, 9, 18, Color.WHITE, 1.9);
                g.dispose();
            }
        };
        p.add(icon);
        p.add(Ui.hgap(12));
        p.add(Ui.label(text, 14.5f, true, null).color(Color.WHITE));
        return p;
    }

    // ----------------------------------------------------------------------------------- forms
    private JPanel formShell() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    private JPanel signInForm() {
        JPanel f = formShell();
        f.add(Forms.left(Ui.title("Welcome back", 32)));
        f.add(Ui.vgap(6));
        f.add(Forms.left(Ui.muted("Sign in to manage your trips and bookings.", 14.5f)));
        f.add(Ui.vgap(30));
        f.add(Forms.labeled("Username", inUser));
        f.add(Ui.vgap(16));
        f.add(Forms.labeled("Password", inPass));
        f.add(Ui.vgap(6));
        PillButton forgot = new PillButton("Forgot password?", PillButton.Style.GHOST).compact();
        forgot.addActionListener(e -> show("reset"));
        JPanel fr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        fr.setOpaque(false);
        fr.setAlignmentX(Component.LEFT_ALIGNMENT);
        fr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        fr.add(forgot);
        f.add(fr);
        f.add(Forms.left(inError));
        f.add(Ui.vgap(8));
        inBtn = new PillButton("Sign in", PillButton.Style.PRIMARY).stretch().trailing(Ico.ARROW_RIGHT);
        inBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        inBtn.addActionListener(e -> doSignIn());
        f.add(inBtn);
        f.add(Ui.vgap(26));
        f.add(Forms.left(divider("New to Tours & Travels?")));
        f.add(Ui.vgap(16));
        PillButton create = new PillButton("Create an account", PillButton.Style.SECONDARY).stretch();
        create.setAlignmentX(Component.LEFT_ALIGNMENT);
        create.addActionListener(e -> show("up"));
        f.add(create);
        inUser.onEnter(e -> inPass.requestFocus());
        inPass.onEnter(e -> doSignIn());
        return f;
    }

    private JPanel signUpForm() {
        JPanel f = formShell();
        f.add(Forms.left(Ui.title("Create your account", 30)));
        f.add(Ui.vgap(6));
        f.add(Forms.left(Ui.muted("It takes less than a minute.", 14.5f)));
        f.add(Ui.vgap(20));
        f.add(Forms.row(12, Forms.labeled("Full name", upName), Forms.labeled("Username", upUser)));
        f.add(Ui.vgap(12));
        f.add(Forms.row(12, Forms.labeled("Password", upPass), Forms.labeled("Confirm", upPass2)));
        f.add(Ui.vgap(12));
        f.add(Forms.labeled("Security question", upQuestion));
        f.add(Ui.vgap(12));
        f.add(Forms.labeled("Answer (used to reset your password)", upAnswer));
        f.add(Ui.vgap(6));
        f.add(Forms.left(upError));
        f.add(Ui.vgap(6));
        upBtn = new PillButton("Create account", PillButton.Style.PRIMARY).stretch().trailing(Ico.ARROW_RIGHT);
        upBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        upBtn.addActionListener(e -> doSignUp());
        f.add(upBtn);
        f.add(Ui.vgap(10));
        PillButton back = new PillButton("Back to sign in", PillButton.Style.GHOST).stretch();
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> show("in"));
        f.add(back);
        upAnswer.onEnter(e -> doSignUp());
        return f;
    }

    private JPanel resetForm() {
        JPanel f = formShell();
        f.add(Forms.left(Ui.title("Reset password", 30)));
        f.add(Ui.vgap(6));
        f.add(Forms.left(Ui.muted("Answer your security question to choose a new password.", 14.5f)));
        f.add(Ui.vgap(24));
        f.add(Forms.labeled("Username", rsUser));
        f.add(Ui.vgap(12));
        rsFind = new PillButton("Find my account", PillButton.Style.SECONDARY).stretch();
        rsFind.setAlignmentX(Component.LEFT_ALIGNMENT);
        rsFind.addActionListener(e -> doFind());
        f.add(rsFind);

        rsStep2 = formShell();
        rsStep2.setAlignmentX(Component.LEFT_ALIGNMENT);
        rsStep2.add(Ui.vgap(18));
        rsStep2.add(Forms.left(rsQuestion));
        rsStep2.add(Ui.vgap(10));
        rsStep2.add(Forms.labeled("Answer", rsAnswer));
        rsStep2.add(Ui.vgap(12));
        rsStep2.add(Forms.labeled("New password", rsPass));
        rsStep2.add(Ui.vgap(14));
        rsBtn = new PillButton("Set new password", PillButton.Style.PRIMARY).stretch();
        rsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rsBtn.addActionListener(e -> doReset());
        rsStep2.add(rsBtn);
        rsStep2.setVisible(false);
        f.add(rsStep2);
        f.add(Ui.vgap(6));
        f.add(Forms.left(rsError));
        f.add(Ui.vgap(10));
        PillButton back = new PillButton("Back to sign in", PillButton.Style.GHOST).stretch();
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.addActionListener(e -> show("in"));
        f.add(back);
        rsUser.onEnter(e -> doFind());
        rsPass.onEnter(e -> doReset());
        return f;
    }

    private static JPanel divider(String text) {
        JPanel p = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = Ui.g2(g0);
                g.setColor(Theme.C.BORDER.get());
                int y = getHeight() / 2;
                java.awt.FontMetrics fm = g.getFontMetrics(Theme.regular(13));
                int tw = fm.stringWidth(text) + 24;
                int x0 = (getWidth() - tw) / 2;
                g.drawLine(0, y, x0, y);
                g.drawLine(x0 + tw, y, getWidth(), y);
                g.dispose();
            }
        };
        p.setOpaque(false);
        p.add(Ui.muted(text, 13), BorderLayout.CENTER);
        ((ThemedLabel) p.getComponent(0)).setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        return p;
    }

    private void show(String card) {
        inError.setText(" "); upError.setText(" "); rsError.setText(" ");
        cards.show(stack, card);
        getRootPane().setDefaultButton("in".equals(card) ? inBtn : "up".equals(card) ? upBtn : rsFind);
        switch (card) {
            case "in" -> inUser.requestFocus();
            case "up" -> upName.requestFocus();
            default -> rsUser.requestFocus();
        }
    }

    private void fail(ThemedLabel label, String msg, Field... bad) {
        label.setText(msg);
        for (Field f : bad) f.setError(true);
        stack.shake();
    }

    // ----------------------------------------------------------------------------------- actions
    private void doSignIn() {
        String u = inUser.getText().trim(), p = inPass.getText();
        if (u.isEmpty() || p.isEmpty()) {
            fail(inError, "Enter your username and password.", u.isEmpty() ? inUser : inPass);
            return;
        }
        inBtn.setEnabled(false);
        inBtn.setText("Signing in\u2026");
        inError.setText(" ");
        Async.run(() -> AccountDao.login(u, p), (Optional<Account> acc) -> {
            inBtn.setEnabled(true);
            inBtn.setText("Sign in");
            if (acc.isEmpty()) {
                fail(inError, "Incorrect username or password.", inUser, inPass);
                return;
            }
            Session.set(acc.get());
            Main.prefs().put("lastUser", acc.get().username());
            MainFrame mf = new MainFrame(acc.get());
            mf.setVisible(true);
            dispose();
            SwingUtilities.invokeLater(() -> Toast.success(mf, "Welcome back, " + mf.firstName() + "!"));
        }, t -> {
            inBtn.setEnabled(true);
            inBtn.setText("Sign in");
            fail(inError, Database.explain(t));
        });
    }

    private void doSignUp() {
        String name = upName.getText().trim(), user = upUser.getText().trim(), pw = upPass.getText();
        String answer = upAnswer.getText().trim();
        if (Validate.blank(name)) { fail(upError, "Please enter your full name.", upName); return; }
        if (!Validate.username(user)) { fail(upError, "Username: 3\u201330 letters, digits, . or _", upUser); return; }
        if (!Validate.password(pw)) { fail(upError, "Password must be at least 6 characters.", upPass); return; }
        if (!pw.equals(upPass2.getText())) { fail(upError, "The two passwords do not match.", upPass2); return; }
        if (Validate.blank(answer)) { fail(upError, "Please answer the security question.", upAnswer); return; }
        upBtn.setEnabled(false);
        String q = upQuestion.getSelectedItem();
        Async.run(() -> AccountDao.register(user, name, pw, q, answer), acc -> {
            upBtn.setEnabled(true);
            inUser.setText(user);
            inPass.setText("");
            show("in");
            Toast.success(this, "Account created \u2013 you can sign in now.");
        }, t -> {
            upBtn.setEnabled(true);
            fail(upError, t instanceof DaoException ? t.getMessage() : Database.explain(t), upUser);
        });
    }

    private void doFind() {
        String u = rsUser.getText().trim();
        if (u.isEmpty()) { fail(rsError, "Enter your username first.", rsUser); return; }
        Async.run(() -> AccountDao.findRecovery(u), opt -> {
            if (opt.isEmpty()) { fail(rsError, "No account with that username.", rsUser); return; }
            rsError.setText(" ");
            rsQuestion.setText(opt.get().question());
            rsStep2.setVisible(true);
            rsFind.setVisible(false);
            rsUser.setEditable(false);
            rsAnswer.requestFocus();
            revalidate();
        }, t -> fail(rsError, Database.explain(t)));
    }

    private void doReset() {
        String a = rsAnswer.getText(), p = rsPass.getText();
        if (Validate.blank(a)) { fail(rsError, "Type your answer.", rsAnswer); return; }
        if (!Validate.password(p)) { fail(rsError, "New password needs 6+ characters.", rsPass); return; }
        String u = rsUser.getText().trim();
        Async.run(() -> AccountDao.resetPassword(u, a, p), ok -> {
            if (!ok) { fail(rsError, "That answer doesn\u2019t match our records.", rsAnswer); return; }
            rsAnswer.setText(""); rsPass.setText(""); rsStep2.setVisible(false); rsFind.setVisible(true);
            rsUser.setEditable(true);
            inUser.setText(u);
            inPass.setText("");
            show("in");
            Toast.success(this, "Password updated. Please sign in.");
        }, t -> fail(rsError, Database.explain(t)));
    }
}
