package iboto.ui;

import javax.swing.*;
import java.awt.*;

/** One place for colours, fonts and small component factories so every screen looks the same. */
public final class Theme {
    public static final Color RED = new Color(0xC00000);
    public static final Color NAVY = new Color(0x2B2F6B);
    public static final Color GRAY = new Color(0x7D819B);
    public static final Color BORDER = new Color(0xC9CCE0);
    public static final Color TINT = new Color(0xFBEFEF);
    public static final Color PANEL = new Color(0xEBEDF7);
    public static final Color GREEN = new Color(0x1F9A55);
    public static final Color ORANGE = new Color(0xE58C1A);

    private Theme() { }

    /** Call once before any component is created: recolours the default tab style to match iBOTO. */
    public static void install() {
        UIManager.put("TabbedPane.selected", TINT);
        UIManager.put("TabbedPane.selectHighlight", RED);
        UIManager.put("TabbedPane.focus", RED);
        UIManager.put("TabbedPane.light", BORDER);
        UIManager.put("TabbedPane.highlight", BORDER);
        UIManager.put("TabbedPane.darkShadow", BORDER);
        UIManager.put("TabbedPane.shadow", BORDER);
        UIManager.put("TabbedPane.contentAreaColor", BORDER);
        UIManager.put("ScrollBar.thumb", PANEL);
    }

    public static Font font(int style, int size) { return new Font(Font.SANS_SERIF, style, size); }

    /** Escapes text before it is placed inside HTML labels. */
    public static String esc(String s) { return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); }

    public static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    public static JLabel title(String text) { return label(text, Font.BOLD, 30, NAVY); }

    /** Centred, wrapped body text. The argument may contain simple HTML such as bold tags. */
    public static JLabel text(String html) {
        JLabel l = label("<html><div style='text-align:center;width:430px'>" + html + "</div></html>", Font.PLAIN, 15, GRAY);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    public static JPanel column() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static JPanel row(JComponent... items) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (JComponent c : items) p.add(c);
        return p;
    }

    public static Component gap(int height) { return Box.createVerticalStrut(height); }

    public static JButton primary(String text, Runnable action) { return new RoundButton(text, true, action); }

    public static JButton outline(String text, Runnable action) { return new RoundButton(text, false, action); }

    /** Small underlined text button used for the demo shortcuts. */
    public static JButton link(String text, Runnable action) {
        JButton b = new JButton("<html><u>" + text + "</u></html>");
        b.setFont(font(Font.PLAIN, 13));
        b.setForeground(GRAY);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> action.run());
        return b;
    }

    public static JTextField field() { return style(new JTextField()); }

    public static JPasswordField password() { return style(new JPasswordField()); }

    private static <T extends JTextField> T style(T f) {
        f.setFont(font(Font.PLAIN, 15));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        f.setPreferredSize(new Dimension(380, 46));
        f.setMaximumSize(new Dimension(380, 46));
        f.setAlignmentX(Component.CENTER_ALIGNMENT);
        return f;
    }

    private static class RoundButton extends JButton {
        private final boolean filled;

        RoundButton(String text, boolean filled, Runnable action) {
            super(text);
            this.filled = filled;
            setFont(font(Font.BOLD, 15));
            setForeground(filled ? Color.WHITE : RED);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(Math.max(130, getPreferredSize().width + 40), 46));
            addActionListener(e -> action.run());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color pale = new Color(0xD9B3B3);
            if (filled) {
                g2.setColor(!isEnabled() ? pale : getModel().isPressed() ? RED.darker() : RED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(isEnabled() ? RED : pale);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
