package iboto.ui;

import javax.swing.*;
import java.awt.*;

/** One reusable screen for every success / warning / error message. */
public class StatusScreen extends Screen {
    public enum Kind { SUCCESS, WARNING, ERROR }

    public StatusScreen(Kind kind, String title, String message,
                        String primaryLabel, Runnable primary, String secondaryLabel, Runnable secondary) {
        add(new Icon(kind));
        add(Theme.gap(18));
        add(Theme.title(title));
        add(Theme.gap(10));
        add(Theme.text(Theme.esc(message)));
        add(Theme.gap(24));
        JPanel buttons = Theme.row(Theme.primary(primaryLabel, primary));
        if (secondaryLabel != null) buttons.add(Theme.outline(secondaryLabel, secondary));
        add(buttons);
    }

    private static class Icon extends JComponent {
        private final Kind kind;

        Icon(Kind kind) {
            this.kind = kind;
            setAlignmentX(Component.CENTER_ALIGNMENT);
            Dimension d = new Dimension(90, 90);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color color = kind == Kind.SUCCESS ? Theme.GREEN : kind == Kind.WARNING ? Theme.ORANGE : Theme.RED;
            g2.setColor(color);
            g2.fillOval(0, 0, 90, 90);
            g2.setColor(Color.WHITE);
            if (kind == Kind.SUCCESS) {
                g2.setStroke(new BasicStroke(9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawPolyline(new int[]{26, 40, 65}, new int[]{46, 60, 32}, 3);
            } else {
                g2.setFont(Theme.font(Font.BOLD, 54));
                String glyph = kind == Kind.WARNING ? "!" : "X";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(glyph, (90 - fm.stringWidth(glyph)) / 2, (90 - fm.getHeight()) / 2 + fm.getAscent());
            }
            g2.dispose();
        }
    }
}
