package iboto.ui;

import iboto.model.Voter;

import javax.swing.*;
import java.awt.*;

/** Step 2: SIMULATED face check. No camera and no image processing is used. */
public class FaceScreen extends Screen {
    private final JLabel status = Theme.label(" ", Font.PLAIN, 14, Theme.GRAY);
    private final JButton scan;
    private final JButton back;
    private final JButton mismatch;

    public FaceScreen(IbotoApp app, Voter voter) {
        scan = Theme.primary("Scan Face", () -> runScan(app, voter, false));
        back = Theme.outline("Back", app::showVoterLogin);
        mismatch = Theme.link("Face does not match", () -> runScan(app, voter, true));

        add(Theme.title("Face Verification"));
        add(Theme.gap(8));
        add(Theme.text("Step 2 of 2: Welcome, <b>" + Theme.esc(voter.getName()) + "</b>. Position your face inside the guide."));
        add(Theme.gap(18));
        add(Theme.row(captioned(new Placeholder(false), "Live camera (simulated)"),
                captioned(new Placeholder(true), "Photo on Voter's ID (sample)")));
        add(Theme.gap(8));
        add(status);
        add(Theme.gap(8));
        add(Theme.row(scan, back));
        add(Theme.gap(14));
        add(Theme.row(Theme.label("Demo:", Font.PLAIN, 13, Theme.GRAY), mismatch));
    }

    private void runScan(IbotoApp app, Voter voter, boolean forceFail) {
        scan.setEnabled(false);
        back.setEnabled(false);
        mismatch.setEnabled(false);
        status.setText("Scanning face... (simulated)");
        Timer timer = new Timer(1400, e -> {
            if (forceFail) {
                app.show(new StatusScreen(StatusScreen.Kind.ERROR, "Face verification failed",
                        "The scan did not match the photo on your Voter's ID. Make sure your face is well lit and try again.",
                        "Try Again", () -> app.showFace(voter), "Cancel", app::showHome));
            } else {
                app.showBallot(voter, null);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static JPanel captioned(JComponent box, String caption) {
        JPanel p = Theme.column();
        p.add(box);
        p.add(Theme.gap(6));
        p.add(Theme.label(caption, Font.PLAIN, 13, Theme.NAVY));
        return p;
    }

    private static class Placeholder extends JComponent {
        private final boolean idPhoto;

        Placeholder(boolean idPhoto) {
            this.idPhoto = idPhoto;
            setAlignmentX(Component.CENTER_ALIGNMENT);
            Dimension d = new Dimension(250, 190);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.PANEL);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
            if (idPhoto) {
                g2.setColor(new Color(0x858AA3));
                g2.fillOval(95, 38, 60, 60);
                g2.fillOval(62, 108, 126, 110);
                g2.setColor(Theme.PANEL);
                g2.fillRect(0, 160, getWidth(), 40);
            } else {
                g2.setColor(Theme.RED);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f, new float[]{9f, 7f}, 0f));
                g2.drawOval(80, 22, 90, 146);
            }
            g2.dispose();
        }
    }
}
